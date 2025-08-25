package app.controller.dtos;

import app.model.entity.Publicacion;

import java.time.LocalDate;

public class AddPublicacionDTO {
    public String titulo;
    public String descripcion;
    public int precio;
    public Long usuarioId;
    public Long vehiculoId;
    public Publicacion.Moneda moneda;

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

    public Publicacion.Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Publicacion.Moneda moneda) {
        this.moneda = moneda;
    }
}
