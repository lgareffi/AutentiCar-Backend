package app.model.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ConcesionarioTallerVerif")
public class ConcesionarioTallerVerif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idConcesionarioTallerVerif;

    @Column(nullable = false, length = 150)
    private String domicilio;

    @Column(nullable = true, length = 500)
    private String archivoUrl;

    @OneToOne
    @JoinColumn(name = "usuarioId", referencedColumnName = "idUsuario", nullable = false, unique = true)
    private Usuarios usuario;

    public ConcesionarioTallerVerif() {
        super();
    }

    public ConcesionarioTallerVerif(long idConcesionarioTallerVerif, String domicilio, String archivoUrl, Usuarios usuario) {
        this.idConcesionarioTallerVerif = idConcesionarioTallerVerif;
        this.domicilio = domicilio;
        this.archivoUrl = archivoUrl;
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "ConcesionarioTallerVerif{" +
                "idConcesionarioTallerVerif=" + idConcesionarioTallerVerif +
                ", domicilio='" + domicilio + '\'' +
                ", archivoUrl='" + archivoUrl + '\'' +
                ", usuario=" + usuario +
                '}';
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public long getIdConcesionarioTallerVerif() {
        return idConcesionarioTallerVerif;
    }

    public void setIdConcesionarioTallerVerif(long idConcesionarioTallerVerif) {
        this.idConcesionarioTallerVerif = idConcesionarioTallerVerif;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }
}
