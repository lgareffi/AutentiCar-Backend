package app.controller.dtos;

import app.model.entity.Publicacion;

import java.time.LocalDate;

public class PublicacionDTO {
    private long idPublicacion;
    private String titulo;
    private String descripcion;
    private int precio;
    private LocalDate fechaPublicacion;
    private String estadoPublicacion;
    private long usuarioId;
    private long vehiculoId;

    public PublicacionDTO(Publicacion pub) {
        super();
        this.idPublicacion = pub.getIdPublicacion();
        this.titulo = pub.getTitulo();
        this.descripcion = pub.getDescripcion();
        this.precio = pub.getPrecio();
        this.fechaPublicacion = pub.getFechaPublicacion();
        this.estadoPublicacion = pub.getEstadoPublicacion().toString();
        this.usuarioId = pub.getUsuario().getIdUsuario();
        this.vehiculoId = pub.getVehiculo() != null ? pub.getVehiculo().getIdVehiculo() : 0;
    }

    public long getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(long idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getEstadoPublicacion() {
        return estadoPublicacion;
    }

    public void setEstadoPublicacion(String estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }
}
