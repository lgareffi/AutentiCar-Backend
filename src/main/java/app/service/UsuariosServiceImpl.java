package app.service;


import app.Errors.NotFoundError;
import app.model.dao.IUsuariosDAO;
import app.model.entity.EventoVehicular;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import app.model.entity.Ventas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuariosServiceImpl implements IUsuariosService {
    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Override
    public Usuarios findById(long id) {
        try {
            Usuarios usuario = usuariosDAO.findById(id);
            return usuario;
        }catch (Throwable e) {
            System.out.println("Error al buscar usuario con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("El usuario no existe");
        }
    }

    @Override
    public void save(Usuarios usuario) {
        try {
            usuariosDAO.save(usuario);
        }catch (Throwable e){
            throw new Error("Error al guardar al usuario" + e.getMessage());
        }
    }

    @Override
    public Usuarios findByMail(String mail) {
        Usuarios usuario = usuariosDAO.findByMail(mail);
        if (usuario == null) {
            throw new NotFoundError("No se encontró un usuario con el mail: " + mail);
        }
        return usuario;
    }

    @Override
    public Usuarios findByDni(int dni) {
        Usuarios usuario = usuariosDAO.findByDni(dni);
        if (usuario == null) {
            throw new NotFoundError("No se encontró un usuario con el DNI: " + dni);
        }
        return usuario;
    }

    @Override
    public List<Ventas> getComprasRealizadas(long id){
        try {
            Usuarios u = this.usuariosDAO.findById(id);
            if (u == null)
                throw new NotFoundError("No se encontro al usuario");
            if (u.getComprasRealizadas().isEmpty())
                throw new Error("No se encontraron compras realizadas por el usuario");
            return u.getComprasRealizadas();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    public List<Ventas> getVentasRealizadas(long id){
        try {
            Usuarios u = this.usuariosDAO.findById(id);
            if (u == null)
                throw new NotFoundError("No se encontro al usuario");
            if (u.getVentasRealizadas().isEmpty())
                throw new Error("No se encontraron ventas realizadas por el usuario");
            return u.getVentasRealizadas();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    public List<Vehiculos> getVehiculos(long id){
        try {
            Usuarios u = this.usuariosDAO.findById(id);
            if (u == null)
                throw new NotFoundError("No se encontro al usuario");
            if (u.getVehiculos().isEmpty())
                throw new Error("No se encontraron vehiculos de este usuario");
            return u.getVehiculos();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    public List<EventoVehicular> getEventoVehicular(long id){
        try {
            Usuarios u = this.usuariosDAO.findById(id);
            if (u == null)
                throw new NotFoundError("No se encontro al usuario");
            if (u.getEventoVehicular().isEmpty())
                throw new Error("No se encontraron eventos hechos por este usuario");
            return u.getEventoVehicular();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    public void update(long id, Usuarios datos) {
        try {
            Usuarios newInfo = usuariosDAO.findById(id);
            if (newInfo != null) {
                newInfo.setPassword(datos.getPassword());
                usuariosDAO.save(newInfo);
            }else {
                throw new Error("Usuario no encontrado");
            }
        } catch (Throwable e) {
            throw new Error(e.getMessage());
        }
    }
}
