package app.service;


import app.controller.dtos.AddPublicacionDTO;
import app.model.entity.Publicacion;

import java.util.List;

public interface IPublicacionService {
    public Publicacion findById(long id);

    public List<Publicacion> findAll();

    public void save(Publicacion publicacion);

    public void savePublicacionDesdeDTO(AddPublicacionDTO dto);
}
