package app.model.entity;

import app.service.CapitalizeFirstConverter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Vehiculos")
public class Vehiculos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idVehiculo;

    @Column(nullable = false,length = 150)
    private String vin;

    @Column(nullable = false,length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String marca;

    @Column(nullable = false, length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String modelo;

    @Column(nullable = false)
    private int anio;

    @Column(nullable = false)
    private int kilometraje;

    @Column(nullable = false)
    private int puertas;

    @Column(nullable = false)
    private double motor;

    @Column(nullable = false, length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String color;

    @Column(nullable = false, length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String tipoCombustible;

    @Column(nullable = false, length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String tipoTransmision;

    @Column(nullable = false, length = 40)
    private LocalDate fechaAlta;

    @Enumerated(EnumType.STRING)
    private Estado estado  = Estado.ACTIVO;

    public enum Estado {
        ACTIVO, VENDIDO, INACTIVO
    }

    @Enumerated(EnumType.STRING)
    private Vehiculos.AllowedToSee allowedToSee = Vehiculos.AllowedToSee.REGISTRADO;

    public enum AllowedToSee {
        REGISTRADO, VALIDADO
    }

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "idUsuario",nullable = false)
    Usuarios usuario;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<DocVehiculo> docVehiculo;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<EventoVehicular> eventoVehicular;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ImagenVehiculo> imagenVehiculos;

    @OneToOne(
            mappedBy = "vehiculo",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Publicacion publicacion;


    public Vehiculos() {
        super();
    }

    public Vehiculos(long idVehiculo, String vin, String marca, String modelo,
                     int kilometraje, int anio, int puertas, double motor,
                     String color, String tipoCombustible, String tipoTransmision,
                     LocalDate fechaAlta, Estado estado, AllowedToSee allowedToSee,
                     Usuarios usuario, List<DocVehiculo> docVehiculo,
                     List<EventoVehicular> eventoVehicular,
                     List<ImagenVehiculo> imagenVehiculos, Publicacion publicacion) {
        this.idVehiculo = idVehiculo;
        this.vin = vin;
        this.marca = marca;
        this.modelo = modelo;
        this.kilometraje = kilometraje;
        this.anio = anio;
        this.puertas = puertas;
        this.motor = motor;
        this.color = color;
        this.tipoCombustible = tipoCombustible;
        this.tipoTransmision = tipoTransmision;
        this.fechaAlta = fechaAlta;
        this.estado = estado;
        this.allowedToSee = allowedToSee;
        this.usuario = usuario;
        this.docVehiculo = docVehiculo;
        this.eventoVehicular = eventoVehicular;
        this.imagenVehiculos = imagenVehiculos;
        this.publicacion = publicacion;
    }

    @Override
    public String toString() {
        return "Vehiculos{" +
                "idVehiculo=" + idVehiculo +
                ", vin='" + vin + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", kilometraje=" + kilometraje +
                ", puertas=" + puertas +
                ", motor=" + motor +
                ", color='" + color + '\'' +
                ", tipoCombustible='" + tipoCombustible + '\'' +
                ", tipoTransmision='" + tipoTransmision + '\'' +
                ", fechaAlta=" + fechaAlta +
                ", estado=" + estado +
                ", allowedToSee=" + allowedToSee +
                ", usuario=" + usuario +
                ", docVehiculo=" + docVehiculo +
                ", eventoVehicular=" + eventoVehicular +
                ", imagenVehiculos=" + imagenVehiculos +
                ", publicacion=" + publicacion +
                '}';
    }

    public long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public String getTipoTransmision() {
        return tipoTransmision;
    }

    public void setTipoTransmision(String tipoTransmision) {
        this.tipoTransmision = tipoTransmision;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public List<DocVehiculo> getDocVehiculo() {
        return docVehiculo;
    }

    public void setDocVehiculo(List<DocVehiculo> docVehiculo) {
        this.docVehiculo = docVehiculo;
    }

    public List<EventoVehicular> getEventoVehicular() {
        return eventoVehicular;
    }

    public void setEventoVehicular(List<EventoVehicular> eventoVehicular) {
        this.eventoVehicular = eventoVehicular;
    }

    public int getPuertas() {
        return puertas;
    }

    public void setPuertas(int puertas) {
        this.puertas = puertas;
    }

    public double getMotor() {
        return motor;
    }

    public void setMotor(double motor) {
        this.motor = motor;
    }

    public List<ImagenVehiculo> getImagenVehiculos() {
        return imagenVehiculos;
    }

    public void setImagenVehiculos(List<ImagenVehiculo> imagenVehiculos) {
        this.imagenVehiculos = imagenVehiculos;
    }

    public AllowedToSee getAllowedToSee() {
        return allowedToSee;
    }

    public void setAllowedToSee(AllowedToSee allowedToSee) {
        this.allowedToSee = allowedToSee;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

}
