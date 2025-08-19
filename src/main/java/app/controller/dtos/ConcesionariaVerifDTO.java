package app.controller.dtos;

import app.model.entity.ConcesionariaVerif;

import java.time.LocalDate;

public class ConcesionariaVerifDTO {
    private long idConcesionariaVerif;
    private String razonSocial;
    private long cuit;
    private String estado;
    private String notas;
    private LocalDate fechaSolicitud;
    private LocalDate fechaActualizacion;
    private long usuarioId;

    public ConcesionariaVerifDTO(ConcesionariaVerif cv) {
        super();
        this.idConcesionariaVerif = cv.getIdConcesionariaVerif();
        this.razonSocial = cv.getRazonSocial();
        this.cuit = cv.getCuit();
        this.estado = cv.getEstado().toString();
        this.notas = cv.getNotas();
        this.fechaSolicitud = cv.getFechaSolicitud();
        this.fechaActualizacion = cv.getFechaActualizacion();
        this.usuarioId = cv.getUsuario() != null ? cv.getUsuario().getIdUsuario() : 0;
    }

    public long getIdConcesionariaVerif() {
        return idConcesionariaVerif;
    }

    public void setIdConcesionariaVerif(long idConcesionariaVerif) {
        this.idConcesionariaVerif = idConcesionariaVerif;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public long getCuit() {
        return cuit;
    }

    public void setCuit(long cuit) {
        this.cuit = cuit;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDate fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
