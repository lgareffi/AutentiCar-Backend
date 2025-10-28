package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.AddConcesionariaTallerVerifDTO;
import app.controller.dtos.ConcesionariaTallerVerifDTO;
import app.model.entity.ConcesionarioTallerVerif;
import app.service.IConcesionariaTallerVerifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concesionariaTallerVerif")
public class ConcesionariaTallerVerifController {
    @Autowired
    private IConcesionariaTallerVerifService concesionariaTallerVerifService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> getByUsuarioId(@PathVariable long usuarioId) {
        try {
            ConcesionarioTallerVerif verif = concesionariaTallerVerifService.findByUsuarioId(usuarioId);
            if (verif == null) {
                return ResponseEntity.notFound().build();
            }
            ConcesionariaTallerVerifDTO dto = new ConcesionariaTallerVerifDTO(verif);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener verificación del usuario");
        }
    }

    @PostMapping
    public ResponseEntity<?> agregarConcesinariaVerif(@RequestBody AddConcesionariaTallerVerifDTO dto) {
        try {
            concesionariaTallerVerifService.saveVerificacionDesdeDTO(dto);
            return new ResponseEntity<>("Verificación agregada correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{verificacionId}")
    public ResponseEntity<?> eliminarConcesinariaVerif(@PathVariable long concesinariaVerifId) {
        try {
            concesionariaTallerVerifService.eliminarConcesionariaTallerVerif(concesinariaVerifId);
            return ResponseEntity.ok("Verificación eliminada");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }


}
