package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddVehiculoDTO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.*;
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

    @Override
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
    public void save(Vehiculos vehiculo) {
        try {
            vehiculosDAO.save(vehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar el vehiculo" + e.getMessage());
        }
    }

    @Override
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
    public void saveVehiculoDesdeDTO(AddVehiculoDTO dto) {
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

        this.vehiculosDAO.save(vehiculo);
    }



}
