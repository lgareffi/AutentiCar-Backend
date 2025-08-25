package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddVehiculoDTO;
import app.controller.dtos.DocVehiculoDTO;
import app.model.dao.*;
import app.model.entity.*;
import app.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@Service
public class VehiculosServiceImpl implements IVehiculosService{
    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IPublicacionDAO publicacionDAO;

    @Autowired
    private IImagenVehiculoService imagenVehiculoService;

    @Autowired
    private IDocVehiculoService docVehiculoService;

    @Autowired
    private IEventoVehicularService eventoVehicularService;

    @Autowired
    private IImagenVehiculoDAO imagenVehiculoDAO;


    @Override
    @Transactional
    public Vehiculos findById(long id) {
        try {
            Vehiculos vehiculo = vehiculosDAO.findById(id);
            return vehiculo;
        }catch (Throwable e) {
            System.out.println("Error al buscar el vehiculo con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("El vehiculo no existe");
        }
    }

    @Override
    @Transactional
    public void save(Vehiculos vehiculo) {
        try {
            vehiculosDAO.save(vehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar el vehiculo" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Vehiculos> findAll() {
        try {
            List<Vehiculos> vehiculos = vehiculosDAO.findAll();
            if (!vehiculos.isEmpty()) {
                return vehiculos;
            }
        } catch (Throwable e) {
            throw new NotFoundError("Error al buscar los vehiculos");
        }
        return null;
    }

    @Override
    @Transactional
    public List<DocVehiculo> getDocVehiculo(long id){
        try {
            Vehiculos v = this.vehiculosDAO.findById(id);
            if (v == null)
                throw new NotFoundError("No se encontro el vehiculo");
            if (v.getDocVehiculo().isEmpty())
                throw new Error("No se encontraron documentos del auto");
            return v.getDocVehiculo();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<EventoVehicular> getEventoVehicular(long id){
        try {
            Vehiculos v = this.vehiculosDAO.findById(id);
            if (v == null)
                throw new NotFoundError("No se encontro el vehiculo");
            if (v.getEventoVehicular().isEmpty())
                throw new Error("No se encontraron eventos hechos al auto");
            return v.getEventoVehicular();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<ImagenVehiculo> getImagenVehiculos(long id) {
        Vehiculos v = vehiculosDAO.findById(id);
        if (v == null) {
            throw new NotFoundError("No se encontró el vehículo con id " + id);
        }
        return (v.getImagenVehiculos() != null) ? v.getImagenVehiculos() : java.util.Collections.emptyList();
    }

    @Override
    @Transactional
    public Long saveVehiculoDesdeDTO(AddVehiculoDTO dto) {
        // 1) VIN único
        Vehiculos existente = this.vehiculosDAO.findByVin(dto.vin);
        if (existente != null) {
            throw new RuntimeException("Auto con VIN ya existente");
        }

        // 2) Determinar el dueño objetivo (si no viene, es el usuario actual)
        Long currentUserId = SecurityUtils.currentUserId(); // principal = ID (Long)
        Long ownerId = (dto.usuarioId != null) ? dto.usuarioId : currentUserId;

        // 3) Autorización: solo admin puede crear para otro usuario
        SecurityUtils.requireAdminOrSelf(ownerId); // lanza 403 si no corresponde

        // 4) Cargar dueño desde BD
        Usuarios owner = this.usuariosDAO.findById(ownerId);
        if (owner == null) {
            throw new NotFoundError("No se encontró el usuario destino");
        }

        // 5) Crear y guardar
        Vehiculos vehiculo = new Vehiculos();
        vehiculo.setVin(dto.vin);
        vehiculo.setMarca(dto.marca);
        vehiculo.setModelo(dto.modelo);
        vehiculo.setAnio(dto.anio);
        vehiculo.setKilometraje(dto.kilometraje);
        vehiculo.setPuertas(dto.puertas);
        vehiculo.setMotor(dto.motor);
        vehiculo.setColor(dto.color);
        vehiculo.setTipoCombustible(dto.tipoCombustible);
        vehiculo.setTipoTransmision(dto.tipoTransmision);
        vehiculo.setFechaAlta(LocalDate.now());
        vehiculo.setEstado(Vehiculos.Estado.ACTIVO);
        vehiculo.setUsuario(owner);

        this.vehiculosDAO.save(vehiculo);
        return vehiculo.getIdVehiculo();
    }

    @Override
    @Transactional
    public void eliminarVehiculo(long vehiculoId) {
        Vehiculos vehiculo = vehiculosDAO.findById(vehiculoId);
        if (vehiculo == null) {
            throw new NotFoundError("Vehículo no encontrado: " + vehiculoId);
        }

        // Autorización: dueño del vehículo o ADMIN
        Long ownerId = vehiculo.getUsuario().getIdUsuario();
        app.security.OwnershipGuard.requireOwnerOrAdmin(ownerId);

        // 1) Eliminar publicación asociada (si existe)
        Publicacion pub = publicacionDAO.findByVehiculoId(vehiculoId);
        if (pub != null) {
            publicacionDAO.delete(pub);
        }

        // 2) Eliminar eventos asociados al vehículo
        if (vehiculo.getEventoVehicular() != null && !vehiculo.getEventoVehicular().isEmpty()) {
            for (EventoVehicular ev : vehiculo.getEventoVehicular()) {
                eventoVehicularService.eliminarEvento(ev.getIdEvento());
            }
            vehiculo.getEventoVehicular().clear(); // mantener el contexto limpio
        }

        // 3) Eliminar imágenes asociadas
        List<ImagenVehiculo> imagenes = this.getImagenVehiculos(vehiculoId); // ahora nunca tira NotFound
        for (ImagenVehiculo img : imagenes) {
            imagenVehiculoService.eliminarImagen(img.getIdImagen());
        }

        // 4) Eliminar documentos asociados
        List<DocVehiculoDTO> documentos = docVehiculoService.listarPorVehiculo(vehiculoId); // ahora nunca tira NotFound
        for (DocVehiculoDTO d : documentos) {
            docVehiculoService.eliminarDocumento(d.getIdDocVehiculo());
        }

        // 5) Finalmente, eliminar el vehículo
        vehiculosDAO.delete(vehiculo);
    }


}
