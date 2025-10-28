package app.controller.dtos;

import java.time.LocalDate;

public class AddEventoDTO {
    public String titulo;
    public String descripcion;
    public int kilometrajeEvento;
    public String tipoEvento;
    public Long usuarioId;
    public Long vehiculoId;

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

    public int getKilometrajeEvento() {
        return kilometrajeEvento;
    }

    public void setKilometrajeEvento(int kilometrajeEvento) {
        this.kilometrajeEvento = kilometrajeEvento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
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
