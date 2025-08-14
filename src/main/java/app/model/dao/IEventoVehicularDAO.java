package app.model.dao;

import app.model.entity.EventoVehicular;

public interface IEventoVehicularDAO {
    public EventoVehicular findById(long id);

    public void save(EventoVehicular eventoVehicular);

    public void delete(EventoVehicular evento);
}
