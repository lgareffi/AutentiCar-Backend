package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.AddEventoDTO;
import app.controller.dtos.DocVehiculoDTO;
import app.controller.dtos.EventoVehicularDTO;
import app.controller.dtos.VehiculosDTO;
import app.model.dao.IEventoVehicularDAO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.Vehiculos;
import app.service.IEventoVehicularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos")
public class EventoVehicularController {
    @Autowired
    private IEventoVehicularService eventoVehicularService;

    @Autowired
    private IEventoVehicularDAO eventoVehicularDAO;

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
            Long id = eventoVehicularService.saveEventoDesdeDTO(eventoDTO);
            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Evento agregado correctamente");
            body.put("id", id);
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
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

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @PutMapping("/{eventoId}/eliminarLogico")
    public ResponseEntity<?> eliminarEventoLogico(@PathVariable long eventoId) {
        try {
            EventoVehicular eventoVehicular = eventoVehicularService.findById(eventoId);

            if (eventoVehicular.isEstaEliminado()) {
                return ResponseEntity.badRequest()
                        .body("El evento ya se encuentra marcado como eliminado");
            }

            eventoVehicular.setEstaEliminado(true);
            eventoVehicularDAO.save(eventoVehicular);

            return ResponseEntity.ok("Evento marcado como eliminado correctamente");

        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al marcar el evento como eliminado");
        }
    }

}

