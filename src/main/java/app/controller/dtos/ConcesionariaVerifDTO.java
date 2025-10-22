package app.controller.dtos;

import app.model.entity.ConcesionarioTallerVerif;

import java.time.LocalDate;

public class ConcesionariaVerifDTO {
    private long idConcesionarioTallerVerif;
    private long usuarioId;
    private String domicilio;
    private String archivoUrl;

    public ConcesionariaVerifDTO(ConcesionarioTallerVerif cv) {
        super();
        this.idConcesionarioTallerVerif = cv.getIdConcesionarioTallerVerif();
        this.usuarioId = cv.getUsuario() != null ? cv.getUsuario().getIdUsuario() : 0;
        this.domicilio = cv.getDomicilio();
        this.archivoUrl = cv.getArchivoUrl();
    }

    public long getIdConcesionarioTallerVerif() {
        return idConcesionarioTallerVerif;
    }

    public void setIdConcesionarioTallerVerif(long idConcesionarioTallerVerif) {
        this.idConcesionarioTallerVerif = idConcesionarioTallerVerif;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
