package app.controller.dtos;

import app.model.entity.ImagenVehiculo;

import java.time.LocalDate;

public class ImagenVehiculoDTO {
    private long idImagen;
    private String urlImagen;
    private LocalDate fechaSubida;
    private long vehiculoId;

    public ImagenVehiculoDTO() {
        super();
    }

    public ImagenVehiculoDTO(ImagenVehiculo i) {
        super();
        this.idImagen = i.getIdImagen();
        this.urlImagen = i.getUrlImagen();
        this.fechaSubida = i.getFechaSubida();
        this.vehiculoId = i.getVehiculo().getIdVehiculo();
    }

    public long getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(long idImagen) {
        this.idImagen = idImagen;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public LocalDate getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDate fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }
}
