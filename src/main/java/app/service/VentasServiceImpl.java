package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddVentasDTO;
import app.model.dao.IPublicacionDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVentasDAO;
import app.model.entity.Publicacion;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import app.model.entity.Ventas;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VentasServiceImpl implements IVentasService{
    @Autowired
    private IVentasDAO ventasDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IPublicacionDAO publicacionDAO;

    @Override
    @Transactional
    public Ventas findById(long id) {
        try {
            Ventas venta = ventasDAO.findById(id);
            return venta;
        }catch (Throwable e) {
            System.out.println("Error al buscar la venta con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La venta no existe");
        }
    }

    @Override
    @Transactional
    public void save(Ventas venta) {
        try {
            ventasDAO.save(venta);
        }catch (Throwable e){
            throw new Error("Error al guardar la venta" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void saveVentaDesdeDTO(AddVentasDTO dto) {
        Usuarios comprador = usuariosDAO.findById(dto.compradorId);
        if (comprador == null)
            throw new NotFoundError("No se encontró el usuario comprador");

        Usuarios vendedor = usuariosDAO.findById(dto.vendedorId);
        if (vendedor == null)
            throw new NotFoundError("No se encontró el usuario vendedor");

        Publicacion publicacion = publicacionDAO.findById(dto.publicacionId);
        if (publicacion == null)
            throw new NotFoundError("No se encontró la publicacion");

        Ventas v = new Ventas();
        v.setPrecio(dto.precio);
        v.setFechaVenta(LocalDate.now());
        v.setUsuarioComprador(comprador);
        v.setUsuarioVendedor(vendedor);
        v.setPublicacion(publicacion);

        ventasDAO.save(v);
    }
}
