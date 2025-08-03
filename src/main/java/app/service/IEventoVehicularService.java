package app.service;

import app.controller.dtos.AddEventoDTO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;

import java.util.List;

public interface IEventoVehicularService {
    public EventoVehicular findById(long id);

    public void save(EventoVehicular eventoVehicular);

    public List<DocVehiculo> getDocVehiculo(long id);

    public void saveEventoDesdeDTO(AddEventoDTO dto);
}
