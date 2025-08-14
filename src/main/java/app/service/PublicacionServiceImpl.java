package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddPublicacionDTO;
import app.model.dao.IPublicacionDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PublicacionServiceImpl implements IPublicacionService{
    @Autowired
    private IPublicacionDAO publicacionDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Override
    public Publicacion findById(long id) {
        try {
            Publicacion publicacion = publicacionDAO.findById(id);
            return publicacion;
        }catch (Throwable e) {
            System.out.println("Error al buscar la publicacion con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La publicacion no existe");
        }
    }

    @Override
    public List<Publicacion> findAll() {
        try {
            List<Publicacion> publicaciones = publicacionDAO.findAll();
            if (!publicaciones.isEmpty()) {
                return publicaciones;
            }
        } catch (Throwable e) {
            throw new NotFoundError("Error al buscar las publicaciones");
        }
        return null;
    }

    @Override
    public void save(Publicacion publicacion) {
        try {
            publicacionDAO.save(publicacion);
        }catch (Throwable e){
            throw new Error("Error al guardar la publicacion" + e.getMessage());
        }
    }

    @Override
    public void savePublicacionDesdeDTO(AddPublicacionDTO dto) {
        Usuarios usuario = usuariosDAO.findById(dto.usuarioId);
        if (usuario == null)
            throw new NotFoundError("No se encontró al usuario");

        Vehiculos vehiculo = vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        Publicacion post = new Publicacion();
        post.setTitulo(dto.titulo);
        post.setDescripcion(dto.descripcion);
        post.setPrecio(dto.precio);
        post.setFechaPublicacion(LocalDate.now());
        post.setEstadoPublicacion(Publicacion.EstadoPublicacion.ACTIVA);
        post.setUsuario(usuario);
        post.setVehiculo(vehiculo);

        publicacionDAO.save(post);
    }

    @Override
    public void eliminarPublicacion(long publicacionId) {
        Publicacion pub = publicacionDAO.findById(publicacionId);
        if (pub == null) {
            throw new NotFoundError("Publicación no encontrada: " + publicacionId);
        }
        publicacionDAO.delete(pub);
    }

    @Override
    public void alternarEstado(long publicacionId) {
        Publicacion pub = publicacionDAO.findById(publicacionId);
        if (pub == null) throw new NotFoundError("Publicación no encontrada: " + publicacionId);

        // Regla: si está VENDIDA, no se permite alternar (podés cambiar esta regla si querés)
        if (pub.getEstadoPublicacion() == Publicacion.EstadoPublicacion.VENDIDA) {
            throw new RuntimeException("No se puede cambiar el estado de una publicación vendida.");
        }

        Publicacion.EstadoPublicacion nuevo =
                (pub.getEstadoPublicacion() == Publicacion.EstadoPublicacion.ACTIVA)
                        ? Publicacion.EstadoPublicacion.PAUSADA
                        : Publicacion.EstadoPublicacion.ACTIVA;

        pub.setEstadoPublicacion(nuevo);
        publicacionDAO.save(pub);
    }

}
