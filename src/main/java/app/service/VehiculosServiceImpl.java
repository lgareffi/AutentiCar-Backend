package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddVehiculoDTO;
import app.controller.dtos.DocVehiculoDTO;
import app.model.dao.*;
import app.model.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<ImagenVehiculo> getImagenVehiculos(long id){
        try {
            Vehiculos v = this.vehiculosDAO.findById(id);
            if (v == null)
                throw new NotFoundError("No se encontro el vehiculo");
            if (v.getImagenVehiculos().isEmpty())
                throw new NotFoundError("No se encontraron imágenes del auto");
            return v.getImagenVehiculos();
        } catch(Throwable e) {
            throw new NotFoundError(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long saveVehiculoDesdeDTO(AddVehiculoDTO dto) {
        // Verifica si el VIN ya existe
        Vehiculos existente = this.vehiculosDAO.findByVin(dto.vin);
        if (existente != null)
            throw new RuntimeException("Auto con VIN ya existente");

        // Buscar el usuario
        Usuarios usuario = this.usuariosDAO.findById(dto.usuarioId);
        if (usuario == null)
            throw new NotFoundError("No se encontró el usuario");

        // Crear y guardar el nuevo vehículo
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
        vehiculo.setUsuario(usuario);
        if (dto.moneda != null) {
            vehiculo.setMoneda(dto.moneda);
        }

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

        // 1) Eliminar publicación asociada (si existe)
        Publicacion pub = publicacionDAO.findByVehiculoId(vehiculoId);
        if (pub != null) {
            publicacionDAO.delete(pub);
        }

        // 2) Eliminar eventos asociados al vehículo
        //    (tu service de evento ya desasocia documentos del evento)
        if (vehiculo.getEventoVehicular() != null && !vehiculo.getEventoVehicular().isEmpty()) {
            for (EventoVehicular ev : vehiculo.getEventoVehicular()) {
                try {
                    eventoVehicularService.eliminarEvento(ev.getIdEvento());
                } catch (Exception e) {
                    throw new RuntimeException(
                            "No se pudo eliminar el evento id=" + ev.getIdEvento() + " del vehículo " + vehiculoId + ": " + e.getMessage(), e
                    );
                }
            }
            // opcional, para mantener el contexto consistente
            vehiculo.getEventoVehicular().clear();
        }

        // 3) Eliminar imágenes asociadas
        List<ImagenVehiculo> imagenes = this.getImagenVehiculos(vehiculoId);
        if (imagenes != null && !imagenes.isEmpty()) {
            for (ImagenVehiculo img : imagenes) {
                try {
                    imagenVehiculoService.eliminarImagen(img.getIdImagen()); // ajusta getter si difiere
                } catch (Exception e) {
                    // Podés elegir: o fallar toda la transacción o loguear y seguir.
                    // Aquí fallamos para mantener consistencia.
                    throw new RuntimeException("No se pudo eliminar la imagen id=" + img.getIdImagen() + ": " + e.getMessage(), e);
                }
            }
        }

        // 4) Eliminar documentos/archivos asociados
        // Si tu listarPorVehiculo devuelve DTOs:
        List<?> documentos = docVehiculoService.listarPorVehiculo(vehiculoId);
        if (documentos != null && !documentos.isEmpty()) {
            for (Object d : documentos) {
                try {
                    long documentoId;
                    if (d instanceof DocVehiculoDTO dto) {
                        documentoId = dto.getIdDocVehiculo(); // ajusta al nombre real del campo
                    } else if (d instanceof DocVehiculo ent) {
                        documentoId = ent.getIdDocVehiculo(); // ajusta al nombre real del campo
                    } else {
                        // Si fuese otro tipo, adaptá este bloque
                        throw new IllegalStateException("Tipo de documento no reconocido: " + d.getClass());
                    }
                    docVehiculoService.eliminarDocumento(documentoId);
                } catch (Exception e) {
                    throw new RuntimeException("No se pudo eliminar un documento del vehículo " + vehiculoId + ": " + e.getMessage(), e);
                }
            }
        }

        // 5) Finalmente, eliminar el vehículo
        vehiculosDAO.delete(vehiculo);
    }

}
