package app.service;

import app.controller.dtos.AddImagenDTO;
import app.model.entity.ImagenVehiculo;

public interface IImagenVehiculoService {
    public ImagenVehiculo findById(long id);

    public void save(ImagenVehiculo imagenVehiculo);

    public void saveImagenDesdeDTO(AddImagenDTO dto);

//    public ImagenVehiculo findByUrl(String urlImagen);
}
