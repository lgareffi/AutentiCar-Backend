package app.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Imagen")
public class ImagenVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idImagen;

    @Column(nullable = false,length = 255)
    private String urlImagen;

    @Column(nullable = false)
    private LocalDate fechaSubida;

    @Column(nullable = false, length = 200)
    private String publicId;

    @ManyToOne // muchas imágenes pueden pertenecer a 1 vehiculo
    @JoinColumn(name = "vehiculoId", referencedColumnName = "idVehiculo",nullable = false)
    Vehiculos vehiculo;

    public ImagenVehiculo() {
        super();
    }

    public ImagenVehiculo(long idImagen, String urlImagen, LocalDate fechaSubida, String publicId,
                          Vehiculos vehiculo) {
        this.idImagen = idImagen;
        this.urlImagen = urlImagen;
        this.fechaSubida = fechaSubida;
        this.publicId = publicId;
        this.vehiculo = vehiculo;
    }

    @Override
    public String toString() {
        return "ImagenVehiculo{" +
                "idImagen=" + idImagen +
                ", urlImagen='" + urlImagen + '\'' +
                ", fechaSubida=" + fechaSubida +
                ", publicId='" + publicId + '\'' +
                ", vehiculo=" + vehiculo +
                '}';
    }

    public long getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(long idImagen) {
        this.idImagen = idImagen;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
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

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    
}
