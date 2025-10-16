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

    // TRAE TODAS LAS PUBLICACIONES EXISTENTES
//    @GetMapping
//    public ResponseEntity<?> getPublicaciones() {
//        try {
//            List<Publicacion> publicaciones = publicacionService.findAll();
//            List<PublicacionDTO> publicacionDTOS = publicaciones.stream()
//                    .map(PublicacionDTO::new)
//                    .collect(Collectors.toList());
//            return new ResponseEntity<>(publicacionDTOS, HttpStatus.OK);
//        }catch (Throwable e) {
//            String msj = "No se encontraron publicaciones";
//            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
//        }
//    }

    @GetMapping
    public ResponseEntity<?> getPublicaciones() {
        try {
            List<Publicacion> publicaciones = publicacionService.getPublicacionesPublicas(); // solo ACTIVA
            List<PublicacionDTO> publicacionDTOS = publicaciones.stream()
                    .map(PublicacionDTO::new)
                    .collect(java.util.stream.Collectors.toList());

            return new ResponseEntity<>(publicacionDTOS, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontraron publicaciones";
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @PostMapping
    public ResponseEntity<?> agregarPublicacion(@RequestBody AddPublicacionDTO dto) {
        try {
            publicacionService.savePublicacionDesdeDTO(dto);
            return new ResponseEntity<>("Publicación agregada correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
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

    @GetMapping("/marca/{marca}")
    public ResponseEntity<?> findActivasByMarca(@PathVariable String marca) {
        try {
            List<Publicacion> pubs = publicacionService.findActivasByMarca(marca);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("No hay publicaciones de " + marca, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/marcaModelo")
    public ResponseEntity<?> findActivasByMarcaAndModelo(
            @RequestParam String marca,
            @RequestParam String modelo) {
        try {
            List<Publicacion> pubs = publicacionService.findActivasByMarcaAndModelo(marca, modelo);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("No hay " + marca + " " + modelo, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<?> findActivasByColor(@PathVariable String color) {
        try {
            List<Publicacion> pubs = publicacionService.findActivasByColor(color);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("No hay publicaciones color " + color, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/anio/{anio}")
    public ResponseEntity<?> findActivasByAnio(@PathVariable int anio) {
        try {
            List<Publicacion> pubs = publicacionService.findActivasByAnio(anio);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("No hay publicaciones del año " + anio, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/combo")
    public ResponseEntity<?> findActivasByMarcaModeloColor(
            @RequestParam String marca,
            @RequestParam String modelo,
            @RequestParam String color) {
        try {
            List<Publicacion> pubs = publicacionService.findActivasByMarcaModeloColor(marca, modelo, color);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("No hay " + marca + " " + modelo + " " + color, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> searchActivasTextoLibre(@RequestParam("q") String queryLibre) {
        try {
            List<Publicacion> pubs = publicacionService.searchActivasTextoLibre(queryLibre);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("Sin resultados para: " + queryLibre, HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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

//    @GetMapping("/precio")
//    public ResponseEntity<?> getActivasByPrecio(
//            @RequestParam(required = false) Integer min,
//            @RequestParam(required = false) Integer max
//    ) {
//        try {
//            List<Publicacion> list = publicacionService.findActivasByPrecioBetween(min, max);
//            List<PublicacionDTO> dtos = list.stream().map(PublicacionDTO::new).toList();
//            return dtos.isEmpty()
//                    ? new ResponseEntity<>("Sin publicaciones para ese rango de precio", HttpStatus.NOT_FOUND)
//                    : new ResponseEntity<>(dtos, HttpStatus.OK);
//        } catch (Throwable e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping("/precio")
    public ResponseEntity<?> findByPrecioArs(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) java.math.BigDecimal tasaUsdArs // opcional
    ) {
        try {
            List<Publicacion> pubs = publicacionService.findActivasByPrecioArs(min, max, tasaUsdArs);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("Sin resultados para el rango de precio", HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/kilometraje")
    public ResponseEntity<?> getActivasByKilometraje(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max
    ) {
        try {
            List<Publicacion> list = publicacionService.findActivasByKilometrajeBetween(min, max);
            List<PublicacionDTO> dtos = list.stream().map(PublicacionDTO::new).toList();
            return dtos.isEmpty()
                    ? new ResponseEntity<>("Sin publicaciones para ese rango de kilometraje", HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filtro")
    public ResponseEntity<?> filtro(
            @RequestParam(required = false) String q,
            @RequestParam(name="marca",  required = false) List<String> marcas,
            @RequestParam(name="color",  required = false) List<String> colores,
            @RequestParam(name="anio",   required = false) List<Integer> anios,
            @RequestParam(name="minPrecio", required = false) List<Integer> minPrecios,
            @RequestParam(name="maxPrecio", required = false) List<Integer> maxPrecios,
            @RequestParam(name="minKm",  required = false) List<Integer> minKms,
            @RequestParam(name="maxKm",  required = false) List<Integer> maxKms
    ) {
        var pubs = publicacionService.findActivasByFiltro(
                marcas, colores, anios, minPrecios, maxPrecios, minKms, maxKms, q
        );
        var dtos = pubs.stream().map(PublicacionDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }
}
