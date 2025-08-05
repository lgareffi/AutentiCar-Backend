package app.model.dao;

import app.model.entity.ImagenVehiculo;

public interface IImagenVehiculoDAO {
    public ImagenVehiculo findById(long id);

    public void save(ImagenVehiculo imagenVehiculo);

    public ImagenVehiculo findByUrl(String urlImagen);
}
