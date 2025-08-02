package app.controller;

import app.controller.dtos.DocVehiculoDTO;
import app.controller.dtos.EventoVehicularDTO;
import app.controller.dtos.VehiculosDTO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.Vehiculos;
import app.service.IEventoVehicularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoVehicularController {
    @Autowired
    private IEventoVehicularService eventoVehicularService;

    @GetMapping("/{eventoId}")
    public ResponseEntity<?> getEvento(@PathVariable long eventoId) {
        try {
            EventoVehicular eventoVehicular = eventoVehicularService.findById(eventoId);
            EventoVehicularDTO dto = new EventoVehicularDTO(eventoVehicular);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Throwable e) {
            String msj = "No se encontro el evento con id: " + eventoId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{eventoId}/documentos")
    public ResponseEntity<?> getDocVehiculo(@PathVariable long eventoId) {
        try {
            List<DocVehiculo> docsVehiculo = this.eventoVehicularService.getDocVehiculo(eventoId);
            List<DocVehiculoDTO> docsDTO = docsVehiculo.stream()
                    .map(DocVehiculoDTO::new)
                    .toList();
            return new ResponseEntity<>(docsDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
