package app.controller.dtos;

import app.model.entity.Ventas;
import java.time.LocalDate;

public class VentasDTO {
    private long idVenta;
    private int precio;
    private LocalDate fechaVenta;

    private long idComprador;
    private long idVendedor;
    private Long idPublicacion;

    public VentasDTO(Ventas venta) {
        super();
        this.idVenta = venta.getIdVenta();
        this.precio = venta.getPrecio();
        this.fechaVenta = venta.getFechaVenta();

        this.idComprador = venta.getUsuarioComprador().getIdUsuario();
        this.idVendedor = venta.getUsuarioVendedor().getIdUsuario();


        this.idPublicacion = (venta.getPublicacion() != null)
                ? venta.getPublicacion().getIdPublicacion()
                : null;
    }

    public VentasDTO() {
        super();
    }

    public long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(long idVenta) {
        this.idVenta = idVenta;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public long getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(long idComprador) {
        this.idComprador = idComprador;
    }

    public long getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(long idVendedor) {
        this.idVendedor = idVendedor;
    }

    public Long getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(Long idPublicacion) {
        this.idPublicacion = idPublicacion;
    }
}
