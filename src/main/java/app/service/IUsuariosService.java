package app.service;

import app.model.entity.EventoVehicular;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import app.model.entity.Ventas;

import java.util.List;

public interface IUsuariosService {
    public Usuarios findById(long id);

    public void save(Usuarios usuario);

    public Usuarios findByMail(String mail);

    public Usuarios findByDni(int dni);

    public List<Ventas> getComprasRealizadas(long id);

    public List<Ventas> getVentasRealizadas(long id);

    public List<Vehiculos> getVehiculos(long id);

    public List<EventoVehicular> getEventoVehicular(long id);

    public void update(long id, Usuarios datos); // para cambiar la contraseña (probar si funciona)
}
