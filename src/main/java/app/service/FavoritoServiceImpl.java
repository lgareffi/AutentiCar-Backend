package app.service;

import app.model.dao.IFavoritoDAO;
import app.model.dao.IPublicacionDAO;
import app.model.dao.IUsuariosDAO;
import app.model.entity.Favorito;
import app.model.entity.Publicacion;
import app.model.entity.Usuarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoritoServiceImpl implements IFavoritoService{
    @Autowired
    private IPublicacionDAO publicacionDAO;

    @Autowired
    private IFavoritoDAO favoritoDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Override
    @Transactional
    public void marcar(long usuarioId, long publicacionId) {
        Favorito existente = favoritoDAO.findByUsuarioAndPublicacion(usuarioId, publicacionId);
        if (existente != null) return;

        Usuarios usuario = usuariosDAO.findById(usuarioId);
        Publicacion publicacion = publicacionDAO.findById(publicacionId);

        Favorito f = new Favorito(usuario, publicacion);
        favoritoDAO.save(f);
    }

    @Override
    @Transactional
    public void desmarcar(long usuarioId, long publicacionId) {
        Favorito existente = favoritoDAO.findByUsuarioAndPublicacion(usuarioId, publicacionId);
        if (existente == null) return;

        favoritoDAO.delete(existente);
    }

    @Override
    @Transactional
    public boolean esFavorito(long usuarioId, long publicacionId) {
        return favoritoDAO.findByUsuarioAndPublicacion(usuarioId, publicacionId) != null;
    }

    @Override
    @Transactional
    public List<Publicacion> listarPublicacionesFavoritas(long usuarioId) {
        return favoritoDAO.findAllPublicacionesByUsuario(usuarioId);
    }
    
}
