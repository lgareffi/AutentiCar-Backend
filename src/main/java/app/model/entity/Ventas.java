package app.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Ventas")
public class Ventas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idVenta;

    @Column(nullable = false)
    private int precio;

    @Column(nullable = false, length = 40)
    private LocalDate fechaVenta;

    @ManyToOne // muchas ventas pueden pertenecer al usuario que COMPRO
    @JoinColumn(name = "compradorId", referencedColumnName = "idUsuario",nullable = false)
    Usuarios usuarioComprador;

    @ManyToOne // muchas ventas pueden pertenecer al usuario que VENDIO
    @JoinColumn(name = "vendedorId", referencedColumnName = "idUsuario",nullable = false)
    Usuarios usuarioVendedor;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "publicacion",referencedColumnName = "idPublicacion", nullable = true)
    Publicacion publicacion;

    public Ventas() {
        super();
    }

    public Ventas(int precio, long idVenta, LocalDate fechaVenta, Usuarios usuarioComprador,
                  Usuarios usuarioVendedor, Publicacion publicacion) {
        this.precio = precio;
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.usuarioComprador = usuarioComprador;
        this.usuarioVendedor = usuarioVendedor;
        this.publicacion = publicacion;
    }

    @Override
    public String toString() {
        return "Ventas{" +
                "idVenta=" + idVenta +
                ", precio=" + precio +
                ", fechaVenta=" + fechaVenta +
                ", usuarioComprador=" + usuarioComprador +
                ", usuarioVendedor=" + usuarioVendedor +
                ", publicacion=" + publicacion +
                '}';
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(long idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Usuarios getUsuarioComprador() {
        return usuarioComprador;
    }

    public void setUsuarioComprador(Usuarios usuarioComprador) {
        this.usuarioComprador = usuarioComprador;
    }

    public Usuarios getUsuarioVendedor() {
        return usuarioVendedor;
    }

    public void setUsuarioVendedor(Usuarios usuarioVendedor) {
        this.usuarioVendedor = usuarioVendedor;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

}
