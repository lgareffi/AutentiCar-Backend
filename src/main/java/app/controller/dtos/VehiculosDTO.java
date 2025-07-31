package app.controller.dtos;

import app.model.entity.Vehiculos;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class VehiculosDTO {
    private long idVehiculo;
    private String vin;
    private String marca;
    private String modelo;
    private int anio;
    private int kilometraje;
    private int puertas;
    private double motor;
    private String color;
    private String tipoCombustible;
    private String tipoTransmision;
    private LocalDate fechaAlta;
    private String estado;
    private Long idUsuario; // Solo el ID del propietario

    private List<Long> idsDocumentos;
    private List<Long> idsEventos;

    public VehiculosDTO(Vehiculos v) {
        super();
        this.idVehiculo = v.getIdVehiculo();
        this.vin = v.getVin();
        this.marca = v.getMarca();
        this.modelo = v.getModelo();
        this.anio = v.getAnio();
        this.kilometraje = v.getKilometraje();
        this.puertas = v.getPuertas();
        this.motor = v.getMotor();
        this.color = v.getColor();
        this.tipoCombustible = v.getTipoCombustible();
        this.tipoTransmision = v.getTipoTransmision();
        this.fechaAlta = v.getFechaAlta();
        this.estado = v.getEstado().name();
        this.idUsuario = v.getUsuario().getIdUsuario();


        this.idsDocumentos = v.getDocVehiculo() != null
                ? v.getDocVehiculo().stream().map(d -> d.getIdDocVehiculo()).collect(Collectors.toList())
                : List.of();

        this.idsEventos = v.getEventoVehicular() != null
                ? v.getEventoVehicular().stream().map(e -> e.getIdEvento()).collect(Collectors.toList())
                : List.of();
    }

    public VehiculosDTO() {
        super();
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<Long> getIdsDocumentos() {
        return idsDocumentos;
    }

    public void setIdsDocumentos(List<Long> idsDocumentos) {
        this.idsDocumentos = idsDocumentos;
    }

    public List<Long> getIdsEventos() {
        return idsEventos;
    }

    public void setIdsEventos(List<Long> idsEventos) {
        this.idsEventos = idsEventos;
    }
}

