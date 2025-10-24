package app.service;


import app.Errors.NotFoundError;
import app.model.dao.IConcesionariaTallerVerifDAO;
import app.model.dao.IUsuariosDAO;
import app.model.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuariosServiceImpl implements IUsuariosService {
    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IVehiculosService vehiculosService;

    @Autowired
    private IEventoVehicularService eventoVehicularService;

    @Autowired
    private IPublicacionService publicacionService;

    @Autowired
    private IConcesionariaTallerVerifDAO concesionariaVerifDAO;

    @Override
    @Transactional
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
    @Transactional
    public List<Usuarios> findAll() {
        try {
            List<Usuarios> usuarios = usuariosDAO.findAll();
            return usuarios;
        } catch (Exception e) {
            throw new NotFoundError("Error al buscar los usuarios");
        }
    }

    @Override
    @Transactional
    public void save(Usuarios usuario) {
        try {
            usuariosDAO.save(usuario);
        }catch (Throwable e){
            throw new Error("Error al guardar al usuario" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Usuarios findByMail(String mail) {
        Usuarios usuario = usuariosDAO.findByMail(mail);
        if (usuario == null) {
            throw new NotFoundError("No se encontró un usuario con el mail: " + mail);
        }
        return usuario;
    }

    @Override
    @Transactional
    public Usuarios findByDni(int dni) {
        Usuarios usuario = usuariosDAO.findByDni(dni);
        if (usuario == null) {
            throw new NotFoundError("No se encontró un usuario con el DNI: " + dni);
        }
        return usuario;
    }

    @Override
    @Transactional
    public List<Usuarios> findByNombreApellido(String search) {
        List<Usuarios> usuarios = usuariosDAO.findByNombreApellido(search);
        if (usuarios.isEmpty()) {
            throw new NotFoundError("No se encontraron usuarios que coincidan con la búsqueda: " + search);
        }
        return usuarios;
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public List<Publicacion> getPublicaciones(long id){
        try {
            Usuarios u = this.usuariosDAO.findById(id);
            if (u == null)
                throw new NotFoundError("No se encontro al usuario");
            if (u.getPublicaciones().isEmpty())
                throw new Error("No se encontraron publicaciones hechas por el usuario");
            return u.getPublicaciones();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

//    @Override
//    @Transactional
//    public void eliminarCuenta(long usuarioId) {
//        Usuarios u = usuariosDAO.findById(usuarioId);
//        if (u == null) throw new NotFoundError("Usuario no encontrado: " + usuarioId);
//
//        ConcesionariaVerif cv = concesionariaVerifDAO.findByUsuarioId(u.getIdUsuario());
//        if (cv != null) {
//            concesionariaVerifDAO.delete(cv);
//        }
//
//        // 1) Vehículos
//        if (u.getVehiculos() != null) {
//            for (Vehiculos v : u.getVehiculos()) {
//                vehiculosService.eliminarVehiculo(v.getIdVehiculo());  // ya borra imágenes/docs/pub
//            }
//            u.getVehiculos().clear();              // <<< importante
//        }
//
//        // 2) Eventos
//        if (u.getEventoVehicular() != null) {
//            for (EventoVehicular ev : u.getEventoVehicular()) {
//                eventoVehicularService.eliminarEvento(ev.getIdEvento());
//            }
//            u.getEventoVehicular().clear();        // <<< importante
//        }
//
//        // 3) Publicaciones
//        if (u.getPublicaciones() != null) {
//            for (Publicacion p : u.getPublicaciones()) {
//                publicacionService.eliminarPublicacion(p.getIdPublicacion());
//            }
//            u.getPublicaciones().clear();          // <<< importante
//        }
//
//        // 5) Sincronizá el estado del usuario sin hijos antes de borrar
//        usuariosDAO.save(u);   // hace merge del usuario con colecciones vacías
//
//        // 6) Ahora sí, eliminar el usuario
//        usuariosDAO.delete(u);
//    }

    @Override
    @Transactional
    public void eliminarCuenta(long usuarioId) {
        Usuarios u = usuariosDAO.findById(usuarioId);
        if (u == null)
            throw new NotFoundError("Usuario no encontrado: " + usuarioId);

        usuariosDAO.delete(u);
    }


    @Override
    @Transactional
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

    @Override
    @Transactional
    public long contarPublicaciones(long usuarioId) {
        return usuariosDAO.countPublicacionesByUsuarioId(usuarioId);
    }

}
