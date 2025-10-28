package app.service;

import app.model.entity.*;

import java.util.List;

public interface IUsuariosService {
    public Usuarios findById(long id);

    public List<Usuarios> findAll();

    public void save(Usuarios usuario);

    public Usuarios findByMail(String mail);

    public Usuarios findByDni(int dni);

    public List<Usuarios> findByNombreApellido(String search);

    public List<Vehiculos> getVehiculos(long id);

    public List<EventoVehicular> getEventoVehicular(long id);

    public List<Publicacion> getPublicaciones(long id);

    public void eliminarCuenta(long usuarioId);

    public void update(long id, Usuarios datos); // para cambiar la contraseña (probar si funciona)

    public long contarPublicaciones(long usuarioId);

}
