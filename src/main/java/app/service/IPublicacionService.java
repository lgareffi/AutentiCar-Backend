package app.service;


import app.controller.dtos.AddPublicacionDTO;
import app.model.entity.Publicacion;

import java.util.List;

public interface IPublicacionService {
    public Publicacion findById(long id);

    public List<Publicacion> findAll();

    public List<Publicacion> getPublicacionesPublicas();

    public void save(Publicacion publicacion);

    public void savePublicacionDesdeDTO(AddPublicacionDTO dto);

    public void eliminarPublicacion(long publicacionId);

    public void alternarEstado(long publicacionId);

    public List<Publicacion> findActivasByMarca(String marca);
    public List<Publicacion> findActivasByMarcaAndModelo(String marca, String modelo);
    public List<Publicacion> findActivasByColor(String color);
    public List<Publicacion> findActivasByAnio(int anio);
    public List<Publicacion> findActivasByMarcaModeloColor(String marca, String modelo, String color);

    public List<Publicacion> searchActivasTextoLibre(String queryLibre);

    public List<String> findDistinctMarcasActivas();
    public List<String> findDistinctModelosActivosByMarca(String marca);
    public List<String> findDistinctColoresActivos();
    public List<Integer> findDistinctAniosActivos();

    //public List<Publicacion> findActivasByPrecioBetween(Integer min, Integer max);
    public List<Publicacion> findActivasByKilometrajeBetween(Integer minKm, Integer maxKm);
    public List<Publicacion> findActivasByPrecioArs(Integer minArs, Integer maxArs, java.math.BigDecimal tasaUsdArs);

    public List<Publicacion> findActivasByFiltro(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            String queryLibre
    );
    
}
