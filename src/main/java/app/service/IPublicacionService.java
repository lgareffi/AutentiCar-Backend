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

    public List<String> findDistinctMarcasActivas();
    public List<String> findDistinctModelosActivosByMarca(String marca);
    public List<String> findDistinctColoresActivos();
    public List<Integer> findDistinctAniosActivos();

    List<Publicacion> findActivasByFiltro(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            List<String> roles,
            String queryLibre
    );

    List<Publicacion> findActivasByFiltroMisPublicaciones(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            List<String> roles,
            String queryLibre,
            Long usuarioId
    );

    List<Publicacion> findActivasByFiltroPublicacionesTaller(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            List<String> roles,
            String queryLibre,
            Long tallerId
    );
    
}
