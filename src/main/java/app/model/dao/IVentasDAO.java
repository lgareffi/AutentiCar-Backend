package app.model.dao;

import app.model.entity.Ventas;

public interface IVentasDAO {
    public Ventas findById(long id);

    public void save(Ventas ventas);
}
