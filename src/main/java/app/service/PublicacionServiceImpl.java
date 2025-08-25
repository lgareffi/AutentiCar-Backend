package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddPublicacionDTO;
import app.model.dao.IPublicacionDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.*;
import app.security.SecurityUtils;
import jakarta.transaction.Transactional;
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void save(Publicacion publicacion) {
        try {
            publicacionDAO.save(publicacion);
        }catch (Throwable e){
            throw new Error("Error al guardar la publicacion" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void savePublicacionDesdeDTO(AddPublicacionDTO dto) {
        // (Opcional) validación defensiva
        if (dto.vehiculoId == null) {
            throw new IllegalArgumentException("vehiculoId es obligatorio");
        }

        Vehiculos vehiculo = vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        Long ownerId = vehiculo.getUsuario().getIdUsuario();

        // Autorización: dueño o admin
        SecurityUtils.requireAdminOrSelf(ownerId);

        // Crear publicación SIEMPRE con el dueño del vehículo
        Publicacion post = new Publicacion();
        post.setTitulo(dto.titulo);
        post.setDescripcion(dto.descripcion);
        post.setPrecio(dto.precio);
        post.setFechaPublicacion(LocalDate.now());
        post.setEstadoPublicacion(Publicacion.EstadoPublicacion.ACTIVA);
        post.setUsuario(vehiculo.getUsuario());
        post.setVehiculo(vehiculo);
        if (dto.moneda != null) {
            post.setMoneda(dto.moneda);
        }

        publicacionDAO.save(post);
    }

    @Override
    @Transactional
    public void eliminarPublicacion(long publicacionId) {
        Publicacion pub = publicacionDAO.findById(publicacionId);
        if (pub == null) {
            throw new NotFoundError("Publicación no encontrada: " + publicacionId);
        }
        publicacionDAO.delete(pub);
    }

    @Override
    @Transactional
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
