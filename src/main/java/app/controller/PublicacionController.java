package app.controller;


import app.controller.dtos.AddPublicacionDTO;
import app.controller.dtos.PublicacionDTO;
import app.controller.dtos.VehiculosDTO;
import app.model.entity.Publicacion;
import app.model.entity.Vehiculos;
import app.service.IPublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {
    @Autowired
    private IPublicacionService publicacionService;

    @GetMapping("/{publicacionId}")
    public ResponseEntity<?> getPublicacion(@PathVariable long publicacionId) {
        try {
            Publicacion publicacion = publicacionService.findById(publicacionId);
            PublicacionDTO dto = new PublicacionDTO(publicacion);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro la publicación con id: " + publicacionId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getPublicaciones() {
        try {
            List<Publicacion> publicaciones = publicacionService.findAll();
            List<PublicacionDTO> publicacionDTOS = publicaciones.stream()
                    .map(PublicacionDTO::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(publicacionDTOS, HttpStatus.OK);
        }catch (Throwable e) {
            String msj = "No se encontraron publicaciones";
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> agregarPublicacion(@RequestBody AddPublicacionDTO dto) {
        try {
            publicacionService.savePublicacionDesdeDTO(dto);
            return new ResponseEntity<>("Publicación agregada correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
