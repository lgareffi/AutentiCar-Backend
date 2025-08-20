package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.AddConcesionariaVerifDTO;
import app.controller.dtos.ConcesionariaVerifDTO;
import app.model.entity.ConcesionariaVerif;
import app.service.IConcesionariaVerifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concesionariaVerif")
public class ConcesionariaVerifController {
    @Autowired
    private IConcesionariaVerifService concesionariaVerifService;

    @GetMapping("/{verificacionId}")
    public ResponseEntity<?> getConcesinariaVerif(@PathVariable long concesinariaVerifId) {
        try {
            ConcesionariaVerif concesionariaVerif = concesionariaVerifService.findById(concesinariaVerifId);
            ConcesionariaVerifDTO dto = new ConcesionariaVerifDTO(concesionariaVerif);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro la verificación de la concesionaria con id: " + concesinariaVerifId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> agregarConcesinariaVerif(@RequestBody AddConcesionariaVerifDTO dto) {
        try {
            concesionariaVerifService.saveVerificacionDesdeDTO(dto);
            return new ResponseEntity<>("Verificación agregada correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{verificacionId}")
    public ResponseEntity<?> eliminarConcesinariaVerif(@PathVariable long concesinariaVerifId) {
        try {
            concesionariaVerifService.eliminarConcesionariaVerif(concesinariaVerifId);
            return ResponseEntity.ok("Verificación eliminada");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @PutMapping("/{verificacionId}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable long verificacionId,
                                           @RequestParam String nuevoEstado,
                                           @RequestParam(required = false) String notas) {
        try {
            // parseo seguro del enum (PENDIENTE, VERIFICADA, RECHAZADA)
            ConcesionariaVerif.EstadoVerificacion estado =
                    ConcesionariaVerif.EstadoVerificacion.valueOf(nuevoEstado.toUpperCase());

            concesionariaVerifService.cambiarEstadoVerificacion(verificacionId, estado, notas);
            return ResponseEntity.ok("Estado de verificación actualizado a " + estado.name());
        } catch (IllegalArgumentException e) {
            // valueOf falló o validación del service (p.ej. motivo requerido en RECHAZADA)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Estado inválido: " + nuevoEstado + ". Valores: PENDIENTE, VERIFICADA, RECHAZADA");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

}
