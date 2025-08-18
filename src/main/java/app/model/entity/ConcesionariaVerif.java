package app.model.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ConcesionariaVerif")
public class ConcesionariaVerif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idConcesionariaVerif;

    @Column(nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false)
    private long cuit; // CUIT sin guiones

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVerificacion estado; // PENDIENTE / VERIFICADA / RECHAZADA

    @Column(nullable = true, length = 250)
    private String notas; // motivo de rechazo u observaciones (opcional)

    @Column(nullable = false, length = 40)
    private LocalDate fechaSolicitud;

    @Column(nullable = true, length = 40)
    private LocalDate fechaActualizacion;

    @OneToOne
    @JoinColumn(name = "usuarioId", referencedColumnName = "idUsuario", nullable = false, unique = true)
    private Usuarios usuario;

    public enum EstadoVerificacion {
        PENDIENTE, VERIFICADA, RECHAZADA
    }

    public ConcesionariaVerif() {
        super();
    }

    public ConcesionariaVerif(long idConcesionariaVerif, String razonSocial, long cuit,
                              EstadoVerificacion estado, String notas, LocalDate fechaSolicitud,
                              Usuarios usuario, LocalDate fechaActualizacion) {
        this.idConcesionariaVerif = idConcesionariaVerif;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.estado = estado;
        this.notas = notas;
        this.fechaSolicitud = fechaSolicitud;
        this.usuario = usuario;
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public String toString() {
        return "ConcesionariaVerif{" +
                "idConcesionariaVerif=" + idConcesionariaVerif +
                ", razonSocial='" + razonSocial + '\'' +
                ", cuit=" + cuit +
                ", estado=" + estado +
                ", notas='" + notas + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                ", fechaActualizacion=" + fechaActualizacion +
                ", usuario=" + usuario +
                '}';
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

    public EstadoVerificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVerificacion estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDate fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
}
