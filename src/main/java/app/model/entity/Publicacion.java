package app.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Publicacion")
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idPublicacion;

    @Column(nullable = false,length = 150)
    private String titulo;

    @Column(nullable = false,length = 300)
    private String descripcion;

    @Column(nullable = false)
    private int precio;

    @Column(nullable = false, length = 40)
    private LocalDate fechaPublicacion;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoPublicacion;

    public enum EstadoPublicacion {
        ACTIVA, VENDIDA, PAUSADA
    }

    @ManyToOne // muchas publicaciones puede hacer 1 usuario
    @JoinColumn(name = "usuarioId", referencedColumnName = "idUsuario",nullable = false)
    Usuarios usuario;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}) // 1 publicacion pertence a 1 auto
    @JoinColumn(name = "vehiculoId",referencedColumnName = "idVehiculo", nullable = true)
    Vehiculos vehiculo;

    public Publicacion() {
        super();
    }

    public Publicacion(long idPublicacion, String titulo, String descripcion, int precio,
                       LocalDate fechaPublicacion, EstadoPublicacion estadoPublicacion,
                       Usuarios usuario, Vehiculos vehiculo) {
        this.idPublicacion = idPublicacion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaPublicacion = fechaPublicacion;
        this.estadoPublicacion = estadoPublicacion;
        this.usuario = usuario;
        this.vehiculo = vehiculo;
    }

    @Override
    public String toString() {
        return "Publicacion{" +
                "idPublicacion=" + idPublicacion +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", fechaPublicacion=" + fechaPublicacion +
                ", estadoPublicacion=" + estadoPublicacion +
                ", usuario=" + usuario +
                ", vehiculo=" + vehiculo +
                '}';
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public long getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(long idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public EstadoPublicacion getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public void setEstadoPublicacion(EstadoPublicacion estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Vehiculos getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculos vehiculo) {
        this.vehiculo = vehiculo;
    }
}
