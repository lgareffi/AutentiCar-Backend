package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddPublicacionDTO;
import app.model.dao.IPublicacionDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.*;
import app.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class PublicacionServiceImpl implements IPublicacionService{
    @Autowired
    private IPublicacionDAO publicacionDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Value("${app.precio.usd_ars:1400}")
    private java.math.BigDecimal defaultUsdArs;

    @Override
    @Transactional
    public Publicacion findById(long id) {
        try {
            Publicacion publicacion = publicacionDAO.findById(id);
            return publicacion;
        }catch (Throwable e) {
            System.out.println("Error al buscar la publicacion con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La publicacion no existe");
        }
    }

    @Override
    @Transactional
    public List<Publicacion> findAll() {
        try {
            List<Publicacion> publicaciones = publicacionDAO.findAll();
            if (!publicaciones.isEmpty()) {
                return publicaciones;
            }
        } catch (Throwable e) {
            throw new NotFoundError("Error al buscar las publicaciones");
        }
        return null;
    }

    @Override
    @Transactional
    public List<Publicacion> getPublicacionesPublicas() {
        return publicacionDAO.findActivas();
    }


    @Override
    @Transactional
    public void save(Publicacion publicacion) {
        try {
            publicacionDAO.save(publicacion);
        }catch (Throwable e){
            throw new Error("Error al guardar la publicacion" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void savePublicacionDesdeDTO(AddPublicacionDTO dto) {
        // (Opcional) validación defensiva
        if (dto.vehiculoId == null) {
            throw new IllegalArgumentException("vehiculoId es obligatorio");
        }

        Vehiculos vehiculo = vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        Long ownerId = vehiculo.getUsuario().getIdUsuario();

        // Autorización: dueño o admin
        SecurityUtils.requireAdminOrSelf(ownerId);

        // Crear publicación SIEMPRE con el dueño del vehículo
        Publicacion post = new Publicacion();
        post.setTitulo(dto.titulo);
        post.setDescripcion(dto.descripcion);
        post.setPrecio(dto.precio);
        post.setFechaPublicacion(LocalDate.now());
        post.setEstadoPublicacion(Publicacion.EstadoPublicacion.ACTIVA);
        post.setUsuario(vehiculo.getUsuario());
        post.setVehiculo(vehiculo);
        if (dto.moneda != null) {
            post.setMoneda(dto.moneda);
        }

        publicacionDAO.save(post);
    }

    @Override
    @Transactional
    public void eliminarPublicacion(long publicacionId) {
        Publicacion pub = publicacionDAO.findById(publicacionId);
        if (pub == null) {
            throw new NotFoundError("Publicación no encontrada: " + publicacionId);
        }

        // ===== Autorización =====
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }
        Long me = (Long) auth.getPrincipal(); // subject = id en tu token

        boolean esAdmin = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .anyMatch("ROL_ADMIN"::equals);

        Long creadorId = (pub.getUsuario() != null) ? pub.getUsuario().getIdUsuario() : null;
        Long duenoVehiculoId = (pub.getVehiculo() != null && pub.getVehiculo().getUsuario() != null)
                ? pub.getVehiculo().getUsuario().getIdUsuario() : null;

        boolean permitido =
                esAdmin
                        || (creadorId != null && creadorId.equals(me))          // user = creador
                        || (duenoVehiculoId != null && duenoVehiculoId.equals(me)); // user = dueño del auto

        if (!permitido) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No autorizado para eliminar esta publicación");
        }

        publicacionDAO.delete(pub);
    }

    @Override
    @Transactional
    public void alternarEstado(long publicacionId) {
        Publicacion pub = publicacionDAO.findById(publicacionId);
        if (pub == null){
            throw new NotFoundError("Publicación no encontrada: " + publicacionId);
        }

        // ===== Autorización =====
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }
        Long me = (Long) auth.getPrincipal(); // subject = id del usuario en tu token

        boolean esAdmin = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .anyMatch("ROL_ADMIN"::equals);

        Long creadorId = (pub.getUsuario() != null) ? pub.getUsuario().getIdUsuario() : null;
        Long duenoVehiculoId = (pub.getVehiculo() != null && pub.getVehiculo().getUsuario() != null)
                ? pub.getVehiculo().getUsuario().getIdUsuario() : null;

        boolean permitido =
                esAdmin
                        || (creadorId != null && creadorId.equals(me))          // user creador
                        || (duenoVehiculoId != null && duenoVehiculoId.equals(me)); // user dueño del auto

        if (!permitido) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No autorizado para cambiar el estado de esta publicación");
        }

        // Regla: si está VENDIDA, no se permite alternar (podés cambiar esta regla si querés)
        if (pub.getEstadoPublicacion() == Publicacion.EstadoPublicacion.VENDIDA) {
            throw new RuntimeException("No se puede cambiar el estado de una publicación vendida.");
        }

        Publicacion.EstadoPublicacion nuevo =
                (pub.getEstadoPublicacion() == Publicacion.EstadoPublicacion.ACTIVA)
                        ? Publicacion.EstadoPublicacion.PAUSADA
                        : Publicacion.EstadoPublicacion.ACTIVA;

        pub.setEstadoPublicacion(nuevo);
        publicacionDAO.save(pub);
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByMarca(String marca) {
        requireText("marca", marca);
        return publicacionDAO.findActivasByMarca(marca.trim());
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByMarcaAndModelo(String marca, String modelo) {
        requireText("marca", marca); requireText("modelo", modelo);
        return publicacionDAO.findActivasByMarcaAndModelo(marca.trim(), modelo.trim());
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByColor(String color) {
        requireText("color", color);
        return publicacionDAO.findActivasByColor(color.trim());
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByAnio(int anio) {
        if (anio < 1900) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año es inválido");
        return publicacionDAO.findActivasByAnio(anio);
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByMarcaModeloColor(String marca, String modelo, String color) {
        requireText("marca", marca); requireText("modelo", modelo); requireText("color", color);
        return publicacionDAO.findActivasByMarcaModeloColor(marca.trim(), modelo.trim(), color.trim());
    }

    @Override
    @Transactional
    public List<Publicacion> searchActivasTextoLibre(String queryLibre) {
        requireText("query", queryLibre);
        return publicacionDAO.searchActivasTextoLibre(queryLibre.trim());
    }

    @Override
    @Transactional
    public List<String> findDistinctMarcasActivas() {
        return publicacionDAO.findDistinctMarcasActivas();
    }

    @Override
    @Transactional
    public List<Integer> findDistinctAniosActivos() {
        return publicacionDAO.findDistinctAniosActivos();
    }

    @Override
    @Transactional
    public List<String> findDistinctModelosActivosByMarca(String marca) {
        if (marca == null || marca.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'marca' es obligatorio");
        return publicacionDAO.findDistinctModelosActivosByMarca(marca.trim());
    }

    @Override
    @Transactional
    public List<String> findDistinctColoresActivos() {
        return publicacionDAO.findDistinctColoresActivos();
    }

//    @Override
//    @Transactional
//    public List<Publicacion> findActivasByPrecioBetween(Integer min, Integer max) {
//        if (min != null && min < 0) throw new IllegalArgumentException("Precio mínimo inválido");
//        if (max != null && max < 0) throw new IllegalArgumentException("Precio máximo inválido");
//        if (min != null && max != null && min > max) throw new IllegalArgumentException("Rango de precios inválido");
//        return publicacionDAO.findActivasByPrecioBetween(min, max);
//    }
//
    private void requireText(String field, String val) {
        if (val == null || val.trim().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo '" + field + "' es obligatorio");
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByPrecioArs(Integer minArs, Integer maxArs, java.math.BigDecimal tasaUsdArs) {
        java.math.BigDecimal tasa = (tasaUsdArs != null) ? tasaUsdArs : defaultUsdArs;
        if (tasa.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tasa USD/ARS inválida");
        }
        return publicacionDAO.findActivasByPrecioEnArs(minArs, maxArs, tasa);
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByKilometrajeBetween(Integer minKm, Integer maxKm) {
        if (minKm != null && minKm < 0) throw new IllegalArgumentException("Kilometraje mínimo inválido");
        if (maxKm != null && maxKm < 0) throw new IllegalArgumentException("Kilometraje máximo inválido");
        if (minKm != null && maxKm != null && minKm > maxKm) throw new IllegalArgumentException("Rango de kilometraje inválido");
        return publicacionDAO.findActivasByKilometrajeBetween(minKm, maxKm);
    }

    @Override
    public List<Publicacion> findActivasByFiltro(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            String queryLibre
    ) {
        // normalizaciones
        var marcasN   = normalizeStrings(marcas);
        var coloresN  = normalizeStrings(colores);
        var aniosN    = emptyToNull(anios);
        var minPrcN   = emptyToNull(minPrecioArs);
        var maxPrcN   = emptyToNull(maxPrecioArs);
        var minKmN    = emptyToNull(minKm);
        var maxKmN    = emptyToNull(maxKm);
        var ql        = (queryLibre != null && !queryLibre.isBlank()) ? queryLibre.trim() : null;

        return publicacionDAO.findActivasByFiltro(
                marcasN, coloresN, aniosN, minPrcN, maxPrcN, minKmN, maxKmN, ql
        );
    }

    // ===== helpers =====
    private List<String> normalizeStrings(List<String> xs) {
        if (xs == null) return null;
        var out = xs.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .toList();
        return out.isEmpty() ? null : out;
    }

    private <T> List<T> emptyToNull(List<T> xs) {
        if (xs == null) return null;
        var out = xs.stream().filter(Objects::nonNull).toList();
        return out.isEmpty() ? null : out;
    }

}
