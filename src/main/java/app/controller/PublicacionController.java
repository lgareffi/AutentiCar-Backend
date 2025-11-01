package app.controller;


import app.Errors.NotFoundError;
import app.controller.dtos.AddPublicacionDTO;
import app.controller.dtos.PublicacionDTO;
import app.controller.dtos.VehiculosDTO;
import app.model.entity.Publicacion;
import app.model.entity.Vehiculos;
import app.service.IPublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            List<Publicacion> publicaciones = publicacionService.getPublicacionesPublicas();
            List<PublicacionDTO> publicacionDTOS = publicaciones.stream()
                    .map(PublicacionDTO::new)
                    .collect(java.util.stream.Collectors.toList());

            return new ResponseEntity<>(publicacionDTOS, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontraron publicaciones";
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN','ROL_CONCESIONARIO')")
    @PostMapping
    public ResponseEntity<?> agregarPublicacion(@RequestBody AddPublicacionDTO dto) {
        try {
            publicacionService.savePublicacionDesdeDTO(dto);
            return new ResponseEntity<>("Publicación agregada correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN','ROL_CONCESIONARIO')")
    @DeleteMapping("/{publicacionId}")
    public ResponseEntity<?> eliminarPublicacion(@PathVariable long publicacionId) {
        try {
            publicacionService.eliminarPublicacion(publicacionId);
            return ResponseEntity.ok("Publicación eliminada");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN','ROL_CONCESIONARIO')")
    @PutMapping("/{publicacionId}/estado")
    public ResponseEntity<?> alternarEstado(@PathVariable long publicacionId) {
        try {
            publicacionService.alternarEstado(publicacionId);
            return ResponseEntity.ok("Publicación actualizada");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @GetMapping("/filtros/marcas")
    public ResponseEntity<?> findDistinctMarcasActivas() {
        try {
            List<String> marcas = publicacionService.findDistinctMarcasActivas();
            return marcas.isEmpty()
                    ? new ResponseEntity<>("Sin marcas activas", HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(marcas, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filtros/modelos")
    public ResponseEntity<?> findDistinctModelosActivosByMarca(@RequestParam String marca) {
        try {
            List<String> modelos = publicacionService.findDistinctModelosActivosByMarca(marca);
            return modelos.isEmpty()
                    ? new ResponseEntity<>("Sin modelos activos para " + marca, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(modelos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filtros/colores")
    public ResponseEntity<?> findDistinctColoresActivos() {
        try {
            List<String> colores = publicacionService.findDistinctColoresActivos();
            return colores.isEmpty()
                    ? new ResponseEntity<>("Sin colores activos", HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(colores, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filtros/anios")
    public ResponseEntity<?> findDistinctAniosActivos() {
        try {
            List<Integer> anios = publicacionService.findDistinctAniosActivos();
            return anios.isEmpty()
                    ? new ResponseEntity<>("Sin años activos", HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(anios, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filtro")
    public ResponseEntity<?> filtro(
            @RequestParam(required = false) String q,
            @RequestParam(name="marca", required = false) List<String> marcas,
            @RequestParam(name="color", required = false) List<String> colores,
            @RequestParam(name="anio", required = false) List<Integer> anios,
            @RequestParam(name="minPrecio", required = false) List<Integer> minPrecios,
            @RequestParam(name="maxPrecio", required = false) List<Integer> maxPrecios,
            @RequestParam(name="minKm", required = false) List<Integer> minKms,
            @RequestParam(name="maxKm", required = false) List<Integer> maxKms,
            @RequestParam(name="rol", required = false) List<String> rolSingular,
            @RequestParam(name="roles", required = false) List<String> rolPlural
    ) {
        List<String> roles = new java.util.ArrayList<>();
        if (rolSingular != null) roles.addAll(rolSingular);
        if (rolPlural != null) roles.addAll(rolPlural);

        var pubs = publicacionService.findActivasByFiltro(
                marcas, colores, anios, minPrecios, maxPrecios, minKms, maxKms, roles, q
        );
        var dtos = pubs.stream().map(PublicacionDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/filtro/misPublicaciones")
    public ResponseEntity<?> filtroMisPublicaciones(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) String q,
            @RequestParam(name="marca", required = false) List<String> marcas,
            @RequestParam(name="color", required = false) List<String> colores,
            @RequestParam(name="anio", required = false) List<Integer> anios,
            @RequestParam(name="minPrecio", required = false) List<Integer> minPrecios,
            @RequestParam(name="maxPrecio", required = false) List<Integer> maxPrecios,
            @RequestParam(name="minKm", required = false) List<Integer> minKms,
            @RequestParam(name="maxKm", required = false) List<Integer> maxKms,
            @RequestParam(name="rol", required = false) List<String> roles
    ) {
        var pubs = publicacionService.findActivasByFiltroMisPublicaciones(
                marcas, colores, anios, minPrecios, maxPrecios, minKms, maxKms, roles, q, usuarioId
        );
        var dtos = pubs.stream().map(PublicacionDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/filtro/publicacionesTaller")
    public ResponseEntity<?> filtroPublicacionesTaller(
            @RequestParam Long tallerId,
            @RequestParam(required = false) String q,
            @RequestParam(name="marca", required = false) List<String> marcas,
            @RequestParam(name="color", required = false) List<String> colores,
            @RequestParam(name="anio", required = false) List<Integer> anios,
            @RequestParam(name="minPrecio", required = false) List<Integer> minPrecios,
            @RequestParam(name="maxPrecio", required = false) List<Integer> maxPrecios,
            @RequestParam(name="minKm", required = false) List<Integer> minKms,
            @RequestParam(name="maxKm", required = false) List<Integer> maxKms,
            @RequestParam(name="rol", required = false) List<String> roles
    ) {
        var pubs = publicacionService.findActivasByFiltroPublicacionesTaller(
                marcas, colores, anios, minPrecios, maxPrecios, minKms, maxKms, roles, q, tallerId
        );
        var dtos = pubs.stream().map(PublicacionDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

}
