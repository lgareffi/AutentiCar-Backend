package app.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Favorito",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "publicacion_id"}))
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idFavorito;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "idUsuario")
    private Usuarios usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "publicacion_id", referencedColumnName = "idPublicacion")
    private Publicacion publicacion;

    @Column(nullable = false)
    private LocalDateTime creadoEn;

    public Favorito() {}

    public Favorito(Usuarios usuario, Publicacion publicacion) {
        this.usuario = usuario;
        this.publicacion = publicacion;
    }

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Favorito{" +
                "idFavorito=" + idFavorito +
                ", usuario=" + usuario +
                ", publicacion=" + publicacion +
                ", creadoEn=" + creadoEn +
                '}';
    }

    public long getIdFavorito() {
        return idFavorito;
    }

    public void setIdFavorito(long idFavorito) {
        this.idFavorito = idFavorito;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
