package app.controller.dtos;

import app.model.entity.DocVehiculo;
import app.model.entity.Vehiculos;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DocVehiculoDTO {
    private long idDocVehiculo;
    private String nombre;
    private String urlDoc;
    private int nivelRiesgo;
    private boolean validadoIA;
    private LocalDate fechaSubida;
    private String tipoDoc;

    private Long idVehiculo;
    private Long idEventoVehicular;
    private String mimeType;

    public DocVehiculoDTO(DocVehiculo doc) {
        super();
        this.idDocVehiculo = doc.getIdDocVehiculo();
        this.nombre = doc.getNombre();
        this.urlDoc = doc.getUrlDoc();
        this.nivelRiesgo = doc.getNivelRiesgo();
        this.validadoIA = doc.isValidadoIA();
        this.fechaSubida = doc.getFechaSubida();
        this.tipoDoc = doc.getTipoDoc().name();
        this.idVehiculo = doc.getVehiculo().getIdVehiculo();
        this.mimeType = doc.getMimeType();
        this.idEventoVehicular = (doc.getEventoVehicular() != null)
                ? doc.getEventoVehicular().getIdEvento()
                : null;
    }

    public DocVehiculoDTO() {
        super();
    }

    public long getIdDocVehiculo() {
        return idDocVehiculo;
    }

    public void setIdDocVehiculo(long idDocVehiculo) {
        this.idDocVehiculo = idDocVehiculo;
    }

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

    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Long getIdEventoVehicular() {
        return idEventoVehicular;
    }

    public void setIdEventoVehicular(Long idEventoVehicular) {
        this.idEventoVehicular = idEventoVehicular;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}

