package app.controller.dtos;

import app.model.entity.EventoVehicular;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventoVehicularDTO {
    private long idEvento;
    private String titulo;
    private String descripcion;
    private int kilometrajeEvento;
    private boolean validadoPorTercero;
    private LocalDate fechaEvento;
    private String tipoEvento;

    private Long idUsuario;    // ID del usuario que creó el evento
    private Long idVehiculo;   // ID del vehículo relacionado

    private String hashEvento;
    private java.time.LocalDateTime blockchainRecordedAt;
    private String blockchainTxId;
    private String blockchainError;

    private List<Long> idsDocumentos; // IDs de los documentos relacionados

    public EventoVehicularDTO(EventoVehicular evento) {
        super();
        this.idEvento = evento.getIdEvento();
        this.titulo = evento.getTitulo();
        this.descripcion = evento.getDescripcion();
        this.kilometrajeEvento = evento.getKilometrajeEvento();
        this.validadoPorTercero = evento.isValidadoPorTercero();
        this.fechaEvento = evento.getFechaEvento();
        this.tipoEvento = evento.getTipoEvento().name();
        this.idUsuario = evento.getUsuario().getIdUsuario();
        this.idVehiculo = evento.getVehiculo().getIdVehiculo();
        this.hashEvento = evento.getHashEvento();
        this.blockchainRecordedAt = evento.getBlockchainRecordedAt();
        this.blockchainTxId = evento.getBlockchainTxId();
        this.blockchainError = evento.getBlockchainError();

        this.idsDocumentos = evento.getDocVehiculo() != null
                ? evento.getDocVehiculo().stream()
                .map(doc -> doc.getIdDocVehiculo())
                .collect(Collectors.toList())
                : List.of();
    }

    public EventoVehicularDTO() {
        super();
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

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public List<Long> getIdsDocumentos() {
        return idsDocumentos;
    }

    public void setIdsDocumentos(List<Long> idsDocumentos) {
        this.idsDocumentos = idsDocumentos;
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
}

