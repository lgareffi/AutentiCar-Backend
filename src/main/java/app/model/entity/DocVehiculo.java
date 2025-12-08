package app.model.entity;

import app.service.CapitalizeFirstConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "DocVehiculo")
public class DocVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idDocVehiculo;

    @Column(nullable = false,length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String nombre;

    @Column(nullable = false,length = 255)
    private String urlDoc;

    @Column(nullable = false)
    private int nivelRiesgo;

    @Column(nullable = true)
    private boolean validadoIA = false;

    @Column(nullable = false)
    private LocalDate fechaSubida;

    @Column(length = 200, nullable = true)
    private String publicId;

    @Column(length = 20, nullable = true)
    private String resourceType;

    @Column(length = 80, nullable = true)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    private TipoDoc tipoDoc;

    public enum TipoDoc {
        TITULO, CEDULA, VTV, SEGURO, INFORME, SERVICE, OTRO
    }

    @Column(nullable = false)
    private boolean estaEliminado = false;

    @ManyToOne
    @JoinColumn(name = "vehiculoId", referencedColumnName = "idVehiculo",nullable = false)
    Vehiculos vehiculo;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "eventoId", referencedColumnName = "idEvento",nullable = false)
    EventoVehicular eventoVehicular;

    public DocVehiculo() {
        super();
    }

    public DocVehiculo(long idDocVehiculo, String nombre, String urlDoc, int nivelRiesgo, boolean validadoIA,
                       LocalDate fechaSubida, String publicId, String resourceType, String mimeType, TipoDoc tipoDoc,
                       boolean estaEliminado, Vehiculos vehiculo, EventoVehicular eventoVehicular) {
        this.idDocVehiculo = idDocVehiculo;
        this.nombre = nombre;
        this.urlDoc = urlDoc;
        this.nivelRiesgo = nivelRiesgo;
        this.validadoIA = validadoIA;
        this.fechaSubida = fechaSubida;
        this.publicId = publicId;
        this.resourceType = resourceType;
        this.mimeType = mimeType;
        this.tipoDoc = tipoDoc;
        this.estaEliminado = estaEliminado;
        this.vehiculo = vehiculo;
        this.eventoVehicular = eventoVehicular;
    }

    @Override
    public String toString() {
        return "DocVehiculo{" +
                "idDocVehiculo=" + idDocVehiculo +
                ", nombre='" + nombre + '\'' +
                ", urlDoc='" + urlDoc + '\'' +
                ", nivelRiesgo=" + nivelRiesgo +
                ", validadoIA=" + validadoIA +
                ", fechaSubida=" + fechaSubida +
                ", publicId='" + publicId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", tipoDoc=" + tipoDoc +
                ", estaEliminado=" + estaEliminado +
                ", vehiculo=" + vehiculo +
                ", eventoVehicular=" + eventoVehicular +
                '}';
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


    public Vehiculos getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculos vehiculo) {
        this.vehiculo = vehiculo;
    }

    public EventoVehicular getEventoVehicular() {
        return eventoVehicular;
    }

    public void setEventoVehicular(EventoVehicular eventoVehicular) {
        this.eventoVehicular = eventoVehicular;
    }

    public TipoDoc getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(TipoDoc tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isEstaEliminado() {
        return estaEliminado;
    }

    public void setEstaEliminado(boolean estaEliminado) {
        this.estaEliminado = estaEliminado;
    }
}
