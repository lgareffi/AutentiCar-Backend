package app.model.dao;


import app.model.entity.Publicacion;

import java.util.List;


public interface IPublicacionDAO {
    public Publicacion findById(long id);

    public List<Publicacion> findAll();

    public void save(Publicacion publicacion);

    public void delete(Publicacion publicacion);

    public Publicacion findByVehiculoId(long vehiculoId);

    List<Publicacion> findActivas();

}
