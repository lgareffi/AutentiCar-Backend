package app.controller.dtos;

import app.model.entity.EventoVehicular;

import java.time.LocalDate;
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

        this.idsDocumentos = evento.getDocVehiculo() != null
                ? evento.getDocVehiculo().stream()
                .map(doc -> doc.getIdDocVehiculo())
                .collect(Collectors.toList())
                : List.of();
    }

    public EventoVehicularDTO() {
        super();
    }
}

