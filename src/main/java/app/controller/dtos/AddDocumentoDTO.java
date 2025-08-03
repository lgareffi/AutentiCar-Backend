package app.controller.dtos;

import java.time.LocalDate;

public class AddDocumentoDTO {
    public String nombre;
    public String urlDoc;
    public int nivelRiesgo;
    public boolean validadoIA;
    public LocalDate fechaSubida;
    public String tipoDoc; // debe coincidir con los valores del enum (TITULO, CEDULA, etc.)
    public long vehiculoId;
    public long eventoId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlDoc() {
        return urlDoc;
    }

    public void setUrlDoc(String urlDoc) {
        this.urlDoc = urlDoc;
    }

    public int getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(int nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public boolean isValidadoIA() {
        return validadoIA;
    }

    public void setValidadoIA(boolean validadoIA) {
        this.validadoIA = validadoIA;
    }

    public LocalDate getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDate fechaSubida) {
        this.fechaSubida = fechaSubida;
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

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }
}
