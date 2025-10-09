package app.model.dao;

import app.model.entity.Favorito;
import app.model.entity.Publicacion;
import java.util.List;

public interface IFavoritoDAO {
    public Favorito findByUsuarioAndPublicacion(long usuarioId, long publicacionId);

    public void delete(Favorito favorito);

    public void save(Favorito favorito);

    List<Publicacion> findAllPublicacionesByUsuario(long usuarioId);
}
