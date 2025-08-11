package app.model.dao;

import app.model.entity.ImagenVehiculo;
import app.model.entity.Vehiculos;

import java.util.List;

public interface IImagenVehiculoDAO {
    public ImagenVehiculo findById(long id);

    public void save(ImagenVehiculo imagenVehiculo);

    public ImagenVehiculo findByUrl(String urlImagen);

    public long countByVehiculo(Vehiculos vehiculo);

    public List<ImagenVehiculo> findByVehiculo(Vehiculos vehiculo);
}
