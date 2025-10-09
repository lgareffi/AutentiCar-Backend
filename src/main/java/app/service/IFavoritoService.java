package app.service;

import app.model.entity.Publicacion;

import java.util.List;

public interface IFavoritoService {
    public void marcar(long usuarioId, long publicacionId);

    public void desmarcar(long usuarioId, long publicacionId);

    public boolean esFavorito(long usuarioId, long publicacionId);

    List<Publicacion> listarPublicacionesFavoritas(long usuarioId);
}
