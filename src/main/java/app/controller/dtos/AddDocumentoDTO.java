package app.controller.dtos;

import java.time.LocalDate;

public class AddDocumentoDTO {
    public String nombre;
    public String tipoDoc;
    public Long vehiculoId;
    public Long eventoId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
}
