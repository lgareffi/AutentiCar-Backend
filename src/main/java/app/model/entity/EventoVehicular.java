package app.model.entity;

import app.service.CapitalizeFirstConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "EventoVehicular",
    uniqueConstraints = {
            @UniqueConstraint(name = "uk_ev_vehiculo_hash", columnNames = {"vehiculoId", "hashEvento"})
    }
)

public class EventoVehicular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idEvento;

    @Column(nullable = false,length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String titulo;

    @Column(nullable = false,length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String descripcion;

    @Column(nullable = false)
    private int kilometrajeEvento;

    @Column(nullable = false, length = 40)
    private LocalDate fechaEvento;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    public enum TipoEvento {
        SERVICIO, REPARACION, SINIESTRO, VTV, TRANSFERENCIA, DOCUMENTACION, OTRO
    }

    @Column(name = "hashEvento", length = 64, nullable = false)
    private String hashEvento;

    @Column(name = "bc_recorded_at")
    private LocalDateTime blockchainRecordedAt;

    @Column(name = "bc_tx_id", length = 128)
    private String blockchainTxId;

    @Column(name = "bc_error", length = 255)
    private String blockchainError;

    @Column(nullable = false)
    private boolean estaEliminado = false;

    @ManyToOne // muchos eventos pueden ser cargados por 1 usuario
    @JoinColumn(name = "usuarioId", referencedColumnName = "idUsuario",nullable = false)
    Usuarios usuario;

    @ManyToOne (fetch = FetchType.LAZY) // muchos eventos se le pueden hacer a 1 vehiculo
    @JoinColumn(name = "vehiculoId", referencedColumnName = "idVehiculo",nullable = false)
    Vehiculos vehiculo;

    // 1 evento puede tener varios documentos (ej. una reparación puede tener una factura y un informe técnico)
//    @OneToMany(mappedBy = "eventoVehicular",
//            cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @OneToMany(mappedBy = "eventoVehicular",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<DocVehiculo> docVehiculo;

    public EventoVehicular() {
        super();
    }

    public EventoVehicular(long idEvento, String titulo, String descripcion,
                           int kilometrajeEvento, LocalDate fechaEvento,
                           TipoEvento tipoEvento, String hashEvento,
                           LocalDateTime blockchainRecordedAt, String blockchainTxId,
                           String blockchainError, boolean estaEliminado, Usuarios usuario,
                           Vehiculos vehiculo, List<DocVehiculo> docVehiculo) {
        this.idEvento = idEvento;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.kilometrajeEvento = kilometrajeEvento;
        this.fechaEvento = fechaEvento;
        this.tipoEvento = tipoEvento;
        this.hashEvento = hashEvento;
        this.blockchainRecordedAt = blockchainRecordedAt;
        this.blockchainTxId = blockchainTxId;
        this.blockchainError = blockchainError;
        this.estaEliminado = estaEliminado;
        this.usuario = usuario;
        this.vehiculo = vehiculo;
        this.docVehiculo = docVehiculo;
    }

    @Override
    public String toString() {
        return "EventoVehicular{" +
                "idEvento=" + idEvento +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", kilometrajeEvento=" + kilometrajeEvento +
                ", fechaEvento=" + fechaEvento +
                ", tipoEvento=" + tipoEvento +
                ", hashEvento='" + hashEvento + '\'' +
                ", blockchainRecordedAt=" + blockchainRecordedAt +
                ", blockchainTxId='" + blockchainTxId + '\'' +
                ", blockchainError='" + blockchainError + '\'' +
                ", estaEliminado=" + estaEliminado +
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

    public String getHashEvento() {
        return hashEvento;
    }

    public void setHashEvento(String hashEvento) {
        this.hashEvento = hashEvento;
    }

    public LocalDateTime getBlockchainRecordedAt() {
        return blockchainRecordedAt;
    }

    public void setBlockchainRecordedAt(LocalDateTime blockchainRecordedAt) {
        this.blockchainRecordedAt = blockchainRecordedAt;
    }

    public String getBlockchainTxId() {
        return blockchainTxId;
    }

    public void setBlockchainTxId(String blockchainTxId) {
        this.blockchainTxId = blockchainTxId;
    }

    public String getBlockchainError() {
        return blockchainError;
    }

    public void setBlockchainError(String blockchainError) {
        this.blockchainError = blockchainError;
    }

    public boolean isEstaEliminado() {
        return estaEliminado;
    }

    public void setEstaEliminado(boolean estaEliminado) {
        this.estaEliminado = estaEliminado;
    }
}
