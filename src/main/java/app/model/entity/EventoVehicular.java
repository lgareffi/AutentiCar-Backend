package app.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "EventoVehicular")
public class EventoVehicular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idEvento;

    @Column(nullable = false,length = 150)
    private String titulo;

    @Column(nullable = false,length = 150)
    private String descripcion;

    @Column(nullable = false)
    private int kilometrajeEvento;

    @Column(nullable = true, length = 40)
    private boolean validadoPorTercero = false;

    @Column(nullable = false, length = 40)
    private LocalDate fechaEvento;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    public enum TipoEvento {
        SERVICIO, REPARACION, SINIESTRO, VTV, TRANSFERENCIA, OTRO
    }

    @ManyToOne // muchos eventos pueden ser cargados por 1 usuario
    @JoinColumn(name = "usuarioId", referencedColumnName = "idUsuario",nullable = false)
    Usuarios usuario;

    @ManyToOne // muchos eventos se le pueden hacer a 1 vehiculo
    @JoinColumn(name = "vehiculoId", referencedColumnName = "idVehiculo",nullable = false)
    Vehiculos vehiculo;

    // 1 evento puede tener varios documentos (ej. una reparación puede tener una factura y un informe técnico)
    @OneToMany(mappedBy = "eventoVehicular",
            cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    List<DocVehiculo> docVehiculo;

    public EventoVehicular() {
        super();
    }

    public EventoVehicular(long idEvento, String titulo, String descripcion, int kilometrajeEvento,
                           boolean validadoPorTercero, LocalDate fechaEvento,
                           TipoEvento tipoEvento, Vehiculos vehiculo,
                           List<DocVehiculo> docVehiculo, Usuarios usuario) {
        this.idEvento = idEvento;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.kilometrajeEvento = kilometrajeEvento;
        this.validadoPorTercero = validadoPorTercero;
        this.fechaEvento = fechaEvento;
        this.tipoEvento = tipoEvento;
        this.vehiculo = vehiculo;
        this.docVehiculo = docVehiculo;
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "EventoVehicular{" +
                "idEvento=" + idEvento +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", kilometrajeEvento=" + kilometrajeEvento +
                ", validadoPorTercero=" + validadoPorTercero +
                ", fechaEvento=" + fechaEvento +
                ", tipoEvento=" + tipoEvento +
                ", usuario=" + usuario +
                ", vehiculo=" + vehiculo +
                ", docVehiculo=" + docVehiculo +
                '}';
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

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

    public boolean isValidadoPorTercero() {
        return validadoPorTercero;
    }

    public void setValidadoPorTercero(boolean validadoPorTercero) {
        this.validadoPorTercero = validadoPorTercero;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDate fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Vehiculos getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculos vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<DocVehiculo> getDocVehiculo() {
        return docVehiculo;
    }

    public void setDocVehiculo(List<DocVehiculo> docVehiculo) {
        this.docVehiculo = docVehiculo;
    }
}
