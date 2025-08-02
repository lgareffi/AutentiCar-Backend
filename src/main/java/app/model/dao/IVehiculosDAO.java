package app.model.dao;


import app.model.entity.Vehiculos;

import java.util.List;

public interface IVehiculosDAO {
    public Vehiculos findById(long id);

    public void save(Vehiculos vehiculo);

    public List<Vehiculos> findAll();

    public Vehiculos findByVin(String vin);

}
