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
        if (dto.vehiculoId == null) {
            throw new IllegalArgumentException("vehiculoId es obligatorio");
        }

        Vehiculos vehiculo = vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        Long ownerId = vehiculo.getUsuario().getIdUsuario();

        SecurityUtils.requireAdminOrSelf(ownerId);

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

//        var auth = org.springframework.security.core.context.SecurityContextHolder
//                .getContext().getAuthentication();
//        if (auth == null || auth.getPrincipal() == null) {
//            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
//        }
//        Long me = (Long) auth.getPrincipal();
//
//        boolean esAdmin = auth.getAuthorities().stream()
//                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
//                .anyMatch("ROL_ADMIN"::equals);
//
//        Long creadorId = (pub.getUsuario() != null) ? pub.getUsuario().getIdUsuario() : null;
//        Long duenoVehiculoId = (pub.getVehiculo() != null && pub.getVehiculo().getUsuario() != null)
//                ? pub.getVehiculo().getUsuario().getIdUsuario() : null;

        Long me    = app.security.SecurityUtils.currentUserId();
        boolean esAdmin = app.security.SecurityUtils.isAdmin();

        Long creadorId = (pub.getUsuario() != null) ? pub.getUsuario().getIdUsuario() : null;
        Long duenoVehiculoId =
                (pub.getVehiculo() != null && pub.getVehiculo().getUsuario() != null)
                        ? pub.getVehiculo().getUsuario().getIdUsuario()
                        : null;

        boolean permitido = esAdmin
                || (creadorId != null && creadorId.equals(me))
                || (duenoVehiculoId != null && duenoVehiculoId.equals(me));

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

        Long me = app.security.SecurityUtils.currentUserId();
        boolean esAdmin = app.security.SecurityUtils.isAdmin();

        Long creadorId = (pub.getUsuario() != null) ? pub.getUsuario().getIdUsuario() : null;
        Long duenoVehiculoId = (pub.getVehiculo() != null && pub.getVehiculo().getUsuario() != null)
                ? pub.getVehiculo().getUsuario().getIdUsuario() : null;


        boolean permitido = esAdmin
                || (creadorId != null && creadorId.equals(me))
                || (duenoVehiculoId != null && duenoVehiculoId.equals(me));

        if (!permitido) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No autorizado para cambiar el estado de esta publicación");
        }

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


    @Override
    public List<Publicacion> findActivasByFiltro(
            List<String> marcas, List<String> colores, List<Integer> anios,
            List<Integer> minPrecioArs, List<Integer> maxPrecioArs,
            List<Integer> minKm, List<Integer> maxKm, List<String> roles,
            String queryLibre
    ) {
        return publicacionDAO.findActivasByFiltro(
                marcas, colores, anios, minPrecioArs, maxPrecioArs, minKm, maxKm,
                convertToEnumRoles(roles), queryLibre,
                null, null
        );
    }

    @Override
    public List<Publicacion> findActivasByFiltroMisPublicaciones(
            List<String> marcas, List<String> colores, List<Integer> anios,
            List<Integer> minPrecioArs, List<Integer> maxPrecioArs,
            List<Integer> minKm, List<Integer> maxKm, List<String> roles,
            String queryLibre, Long usuarioId
    ) {
        return publicacionDAO.findActivasByFiltro(
                marcas, colores, anios, minPrecioArs, maxPrecioArs, minKm, maxKm,
                convertToEnumRoles(roles), queryLibre,
                usuarioId, null
        );
    }

    @Override
    public List<Publicacion> findActivasByFiltroPublicacionesTaller(
            List<String> marcas, List<String> colores, List<Integer> anios,
            List<Integer> minPrecioArs, List<Integer> maxPrecioArs,
            List<Integer> minKm, List<Integer> maxKm, List<String> roles,
            String queryLibre, Long tallerId
    ) {
        return publicacionDAO.findActivasByFiltro(
                marcas, colores, anios, minPrecioArs, maxPrecioArs, minKm, maxKm,
                convertToEnumRoles(roles), queryLibre,
                null, tallerId
        );
    }

    private List<Usuarios.Rol> convertToEnumRoles(List<String> roles) {
        if (roles == null) return null;

        var out = roles.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(s -> {
                    try { return Usuarios.Rol.valueOf(s); }
                    catch (IllegalArgumentException ex) { return null; }
                })
                .filter(Objects::nonNull)
                .toList();

        return out.isEmpty() ? null : out;
    }

}
