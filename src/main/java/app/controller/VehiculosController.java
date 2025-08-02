package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.*;
import app.model.dao.IUsuariosDAO;
import app.model.entity.*;
import app.service.IVehiculosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehiculos")
public class VehiculosController {
    @Autowired
    private IVehiculosService vehiculosService;

    @GetMapping("/{vehiculoId}")
    public ResponseEntity<?> getVehiculo(@PathVariable long vehiculoId) {
        try {
            Vehiculos vehiculo = vehiculosService.findById(vehiculoId);
            VehiculosDTO dto = new VehiculosDTO(vehiculo);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro el vehiculo con id: " + vehiculoId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getVehiculos() {
        try {
            List<Vehiculos> vehiculos = vehiculosService.findAll();
            List<VehiculosDTO> vehiculosDTO = vehiculos.stream()
                    .map(VehiculosDTO::new)     //.stream().map(VehiculosDTO::new) convierte cada entidad Vehiculos a su VehiculosDTO correspondiente usando tu constructor.
                    .collect(Collectors.toList());
            return new ResponseEntity<>(vehiculosDTO, HttpStatus.OK);

        }catch (Throwable e) {
            String msj = "No se encontraron vehiculos";
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{vehiculoId}/documentos")
    public ResponseEntity<?> getDocVehiculo(@PathVariable long vehiculoId) {
        try {
            List<DocVehiculo> docsVehiculo = this.vehiculosService.getDocVehiculo(vehiculoId);
            List<DocVehiculoDTO> docsDTO = docsVehiculo.stream()
                    .map(DocVehiculoDTO::new)
                    .toList();
            return new ResponseEntity<>(docsDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/{vehiculoId}/eventos")
    public ResponseEntity<?> getEventoVehicular(@PathVariable long vehiculoId) {
        try {
            List<EventoVehicular> eventosVehiculo = this.vehiculosService.getEventoVehicular(vehiculoId);
            List<EventoVehicularDTO> eventosDTO = eventosVehiculo.stream()
                    .map(EventoVehicularDTO::new)
                    .toList();
            return new ResponseEntity<>(eventosDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
