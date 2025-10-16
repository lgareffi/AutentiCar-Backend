package app.model.dao;


import app.model.entity.Publicacion;

import java.math.BigDecimal;
import java.util.List;


public interface IPublicacionDAO {
    public Publicacion findById(long id);

    public List<Publicacion> findAll();

    public void save(Publicacion publicacion);

    public void delete(Publicacion publicacion);

    public Publicacion findByVehiculoId(long vehiculoId);

    List<Publicacion> findActivas();

    // búsquedas
    List<Publicacion> findActivasByMarca(String marca);
    List<Publicacion> findActivasByMarcaAndModelo(String marca, String modelo);
    List<Publicacion> findActivasByColor(String color);
    List<Publicacion> findActivasByAnio(int anio);

    List<Publicacion> findActivasByMarcaModeloColor(String marca, String modelo, String color);

    List<Publicacion> searchActivasTextoLibre(String queryLibre);

    List<String> findDistinctMarcasActivas();
    List<String> findDistinctModelosActivosByMarca(String marca);
    List<String> findDistinctColoresActivos();
    List<Integer> findDistinctAniosActivos();

    List<Publicacion> findActivasByPrecioEnArs(Integer minArs, Integer maxArs, BigDecimal tasaUsdArs);
    List<Publicacion> findActivasByKilometrajeBetween(Integer minKm, Integer maxKm);

    List<Publicacion> findActivasByFiltro(
            String marca, String color, Integer anio,
            Integer minPrecioArs, Integer maxPrecioArs,
            Integer minKm, Integer maxKm,
            String queryLibre
    );


}
