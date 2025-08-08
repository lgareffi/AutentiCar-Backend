package app.controller;


import app.Errors.NotFoundError;
import app.controller.dtos.*;
import app.model.entity.*;
import app.service.IVehiculosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/{vehiculoId}/imagenes")
    public ResponseEntity<?> getImagenVehiculos(@PathVariable long vehiculoId) {
        try {
            List<ImagenVehiculo> imagenesVehiculos = this.vehiculosService.getImagenVehiculos(vehiculoId);
            List<ImagenVehiculoDTO> imagenesDTO = imagenesVehiculos.stream()
                    .map(ImagenVehiculoDTO::new)
                    .toList();
            return new ResponseEntity<>(imagenesDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

//    @PostMapping
//    public ResponseEntity<?> agregarVehiculo(@RequestBody AddVehiculoDTO vehiculoDTO) {
//        try {
//            vehiculosService.saveVehiculoDesdeDTO(vehiculoDTO);
//            return new ResponseEntity<>("Vehículo agregado correctamente", HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

    @PostMapping
    public ResponseEntity<?> agregarVehiculo(@RequestBody AddVehiculoDTO dto) {
        try {
            Long id = vehiculosService.saveVehiculoDesdeDTO(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Vehículo agregado correctamente");
            response.put("id", id);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (NotFoundError e) {
            // usuario no encontrado u otros not found del dominio
            return new ResponseEntity<>("No se encontró el usuario", HttpStatus.NOT_FOUND);

        } catch (RuntimeException e) {
            // por ejemplo: VIN duplicado
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
