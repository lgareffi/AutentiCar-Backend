package app.model.dao;


import app.model.entity.Publicacion;
import app.model.entity.Usuarios;

import java.math.BigDecimal;
import java.util.List;


public interface IPublicacionDAO {
    public Publicacion findById(long id);

    public List<Publicacion> findAll();

    public void save(Publicacion publicacion);

    public void delete(Publicacion publicacion);

    public Publicacion findByVehiculoId(long vehiculoId);

    List<Publicacion> findByTallerAsignado(Long tallerId);

    List<Publicacion> findActivas();

    List<String> findDistinctMarcasActivas();
    List<String> findDistinctModelosActivosByMarca(String marca);
    List<String> findDistinctColoresActivos();
    List<Integer> findDistinctAniosActivos();

    List<Publicacion> findActivasByFiltro(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            List<Usuarios.Rol> roles,
            String queryLibre,
            Long usuarioId,
            Long tallerId
    );

}
