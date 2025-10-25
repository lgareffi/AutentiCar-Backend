package app.model.dao;

import app.model.entity.DocVehiculo;
import app.model.entity.Vehiculos;

import java.util.List;

public interface IDocVehiculoDAO {
    public DocVehiculo findById(long id);

    public void save(DocVehiculo docVehiculo);

    public void delete(DocVehiculo docVehiculo);

    public List<DocVehiculo> findByVehiculo(Vehiculos vehiculo);
    
}
