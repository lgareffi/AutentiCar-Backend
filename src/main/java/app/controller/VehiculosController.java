package app.controller;


import app.Errors.NotFoundError;
import app.controller.dtos.*;
import app.model.entity.*;
import app.service.IDocVehiculoService;
import app.service.IImagenVehiculoService;
import app.service.IVehiculosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehiculos")
public class VehiculosController {
    @Autowired
    private IVehiculosService vehiculosService;

    @Autowired
    private IImagenVehiculoService imagenVehiculoService;

    @Autowired
    private IDocVehiculoService docVehiculoService;

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

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @DeleteMapping("/{vehiculoId}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable long vehiculoId) {
        try {
            vehiculosService.eliminarVehiculo(vehiculoId);
            return ResponseEntity.ok("Vehículo eliminado");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @PostMapping( value = "/{vehiculoId}/imagenes",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirImagenes(
            @PathVariable long vehiculoId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            List<ImagenVehiculoDTO> dtos = imagenVehiculoService.subirMultiples(vehiculoId, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
        } catch (app.Errors.NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @GetMapping("/{vehiculoId}/imagenes")
    public ResponseEntity<?> getImagenVehiculos(@PathVariable long vehiculoId) {
        try {
            List<ImagenVehiculo> imagenesVehiculos = this.vehiculosService.getImagenVehiculos(vehiculoId);

            List<ImagenVehiculoDTO> imagenesDTO = imagenesVehiculos.stream()
                    .map(ImagenVehiculoDTO::new)
                    .toList();

            return ResponseEntity.ok(imagenesDTO); // 200 [] si está vacío
        } catch (app.Errors.NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @DeleteMapping("/imagenes/{imagenId}")
    public ResponseEntity<?> eliminarImagen(@PathVariable long imagenId) {
        try {
            imagenVehiculoService.eliminarImagen(imagenId);
            return ResponseEntity.ok("Imagen eliminada");
        } catch (app.Errors.NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @PostMapping(value = "/{vehiculoId}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirDocumento(@PathVariable long vehiculoId,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam("nombre") String nombre,
                                            @RequestParam("tipoDoc") String tipoDoc,
                                            @RequestParam(value = "nivelRiesgo", required = false) Integer nivelRiesgo,
                                            @RequestParam(value = "validadoIA", required = false) Boolean validadoIA,
                                            @RequestParam(value = "eventoId", required = false) Long eventoId) {
        try {
            DocVehiculoDTO dto = docVehiculoService.subirDocumento(vehiculoId, file, nombre, tipoDoc, nivelRiesgo, validadoIA, eventoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (app.Errors.NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @GetMapping("/{vehiculoId}/documentos")
    public ResponseEntity<?> listarDocumentos(@PathVariable long vehiculoId) {
        try {
            return ResponseEntity.ok(docVehiculoService.listarPorVehiculo(vehiculoId));
        } catch (app.Errors.NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @DeleteMapping("/documentos/{documentoId}")
    public ResponseEntity<?> eliminarDocumento(@PathVariable long documentoId) {
        try {
            docVehiculoService.eliminarDocumento(documentoId);
            return ResponseEntity.ok("Documento eliminado");
        } catch (app.Errors.NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

}
