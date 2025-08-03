package app.service;


import app.controller.dtos.AddVentasDTO;
import app.model.entity.Ventas;


public interface IVentasService {
    public Ventas findById(long id);

    public void save(Ventas venta);

    public void saveVentaDesdeDTO(AddVentasDTO dto);
}
