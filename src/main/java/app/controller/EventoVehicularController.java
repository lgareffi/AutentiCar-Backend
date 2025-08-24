package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.AddEventoDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @PostMapping
    public ResponseEntity<?> agregarEvento(@RequestBody AddEventoDTO eventoDTO) {
        try {
            eventoVehicularService.saveEventoDesdeDTO(eventoDTO);
            return new ResponseEntity<>("Evento agregado correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @DeleteMapping("/{eventoId}")
    public ResponseEntity<?> eliminarEvento(@PathVariable long eventoId) {
        try {
            eventoVehicularService.eliminarEvento(eventoId);
            return ResponseEntity.ok("Evento eliminado");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

}

