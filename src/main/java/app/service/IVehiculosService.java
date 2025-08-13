package app.service;

import app.controller.dtos.AddVehiculoDTO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.ImagenVehiculo;
import app.model.entity.Vehiculos;

import java.util.List;

public interface IVehiculosService {
    public Vehiculos findById(long id);

    public void save(Vehiculos vehiculo);

    public List<Vehiculos> findAll();

    public List<DocVehiculo> getDocVehiculo(long id);

    public List<EventoVehicular> getEventoVehicular(long id);

    public List<ImagenVehiculo> getImagenVehiculos(long id);

    public Long saveVehiculoDesdeDTO(AddVehiculoDTO dto);

    public void eliminarVehiculo(long vehiculoId);
}
