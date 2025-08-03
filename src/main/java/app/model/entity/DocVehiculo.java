package app.model.entity;

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
    private String nombre;

    @Column(nullable = false,length = 150)
    private String urlDoc;

    @Column(nullable = false)
    private int nivelRiesgo;

    @Column(nullable = true, length = 40)
    private boolean validadoIA = false;

    @Column(nullable = false, length = 40)
    private LocalDate fechaSubida;

    @Enumerated(EnumType.STRING)
    private TipoDoc tipoDoc;

    public enum TipoDoc {
        TITULO, CEDULA, VTV, SEGURO, INFORME, OTRO
    }

    @ManyToOne // muchos documentos pueden pertenecer a 1 vehiculo
    @JoinColumn(name = "vehiculoId", referencedColumnName = "idVehiculo",nullable = false)
    Vehiculos vehiculo;

    @ManyToOne // muchos documentos pueden pertenecer a 1 evento
    @JoinColumn(name = "eventoId", referencedColumnName = "idEvento",nullable = false)
    EventoVehicular eventoVehicular;

    public DocVehiculo() {
        super();
    }

    public DocVehiculo(long idDocVehiculo, String nombre, String urlDoc, int nivelRiesgo,
                       LocalDate fechaSubida, boolean validadoIA, TipoDoc tipoDoc,
                       Vehiculos vehiculo, EventoVehicular eventoVehicular) {
        this.idDocVehiculo = idDocVehiculo;
        this.nombre = nombre;
        this.urlDoc = urlDoc;
        this.nivelRiesgo = nivelRiesgo;
        this.fechaSubida = fechaSubida;
        this.validadoIA = validadoIA;
        this.tipoDoc = tipoDoc;
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
                ", tipoDoc=" + tipoDoc +
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
}
