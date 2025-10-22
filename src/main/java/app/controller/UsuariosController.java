package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.*;
import app.model.dao.IUsuariosDAO;
import app.model.entity.*;
import app.service.IImagenVehiculoService;
import app.service.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
public class UsuariosController {
    @Autowired
    private IUsuariosService usuariosService;
    @Autowired
    private IUsuariosDAO usuariosDAO;
    @Autowired
    private IImagenVehiculoService imagenVehiculoService;

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> getUsuario(@PathVariable long usuarioId) {
        try {
            Usuarios usuario = usuariosService.findById(usuarioId);
            UsuariosDTO dto = new UsuariosDTO(usuario);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro al usuario con id: " + usuarioId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_ADMIN')")
    @GetMapping("/usuarios")
    public ResponseEntity<?> getUsuarios() {
        try {
            List<Usuarios> usuarios = usuariosService.findAll();
            List<UsuariosDTO> usuariosDTO = usuarios.stream()
                    .map(UsuariosDTO::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(usuariosDTO, HttpStatus.OK);

        }catch (Throwable e) {
            String msj = "No se encontraron usuarios";
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @GetMapping("/usuarios/{usuarioId}/comprasRealizadas")
    public ResponseEntity<?> getComprasRealizadas(@PathVariable long usuarioId) {
        try {
            List<Ventas> comprasRealizadas = this.usuariosService.getComprasRealizadas(usuarioId);
            List<VentasDTO> comprasDTO = comprasRealizadas.stream()
                    .map(VentasDTO::new) // llama al constructor VentasDTO(Ventas v)
                    .toList();
            return new ResponseEntity<>(comprasDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @GetMapping("/usuarios/{usuarioId}/ventasRealizadas")
    public ResponseEntity<?> getVentasRealizadas(@PathVariable long usuarioId) {
        try {
            List<Ventas> ventasRealizadas = this.usuariosService.getVentasRealizadas(usuarioId);
            List<VentasDTO> ventasDTO = ventasRealizadas.stream()
                    .map(VentasDTO::new) // llama al constructor VentasDTO(Ventas v)
                    .toList();
            return new ResponseEntity<>(ventasDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @GetMapping("/usuarios/{usuarioId}/vehiculos")
    public ResponseEntity<?> getVehiculos(@PathVariable long usuarioId) {
        try {
            List<Vehiculos> vehiculos = this.usuariosService.getVehiculos(usuarioId);
            List<VehiculosDTO> vehiculosDTO = vehiculos.stream()
                    .map(VehiculosDTO::new)
                    .toList();
            return new ResponseEntity<>(vehiculosDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @GetMapping("/usuarios/{usuarioId}/eventos")
    public ResponseEntity<?> getEventoVehicular(@PathVariable long usuarioId) {
        try {
            List<EventoVehicular> eventosVehicular = this.usuariosService.getEventoVehicular(usuarioId);
            List<EventoVehicularDTO> eventosDTO = eventosVehicular.stream()
                    .map(EventoVehicularDTO::new)
                    .toList();
            return new ResponseEntity<>(eventosDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @GetMapping("/usuarios/{usuarioId}/publicaciones")
    public ResponseEntity<?> getPublicaciones(@PathVariable long usuarioId) {
        try {
            List<Publicacion> publicaciones = this.usuariosService.getPublicaciones(usuarioId);
            List<PublicacionDTO> publicacionDTO = publicaciones.stream()
                    .map(PublicacionDTO::new)
                    .toList();
            return new ResponseEntity<>(publicacionDTO, HttpStatus.OK);
        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> update(@PathVariable long usuarioId, @RequestBody Usuarios datos) {
        try {
            usuariosService.update(usuarioId, datos);
            return new ResponseEntity<>("Información actualizada correctamente", HttpStatus.OK);

        } catch (Throwable e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuarios datos) {
        try {
            String mail = datos.getMail();
            String password = datos.getPassword();

            Usuarios existe = usuariosService.findByMail(mail);

            if (existe.getPassword().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Fuiste loggeado exitosamente");
                response.put("id", existe.getIdUsuario());

                return new ResponseEntity<>(response, HttpStatus.OK);
//                return new ResponseEntity<>("Fuiste loggeado exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
            }

        } catch (NotFoundError e) { // o escribio mal el mail o no esta registrado
            // Captura específicamente el caso de "mail no encontrado"
            return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PostMapping("/register")
//    public ResponseEntity<?> registerUsuario(@RequestBody Usuarios datos) {
//        try {
//            // Verificar si ya existe usuario con ese mail
//            Usuarios existenteMail = usuariosDAO.findByMail(datos.getMail());
//            if (existenteMail != null) {
//                return new ResponseEntity<>("Ya existe una cuenta registrada con ese correo", HttpStatus.CONFLICT);
//            }
//
//            // Verificar si ya existe usuario con ese DNI
//            Usuarios existenteDni = usuariosDAO.findByDni(datos.getDni());
//            if (existenteDni != null) {
//                return new ResponseEntity<>("Ya existe una cuenta registrada con ese DNI", HttpStatus.CONFLICT);
//            }
//
//            if (datos.getRol() == null) {
//                return new ResponseEntity<>("Debe seleccionar un rol válido", HttpStatus.BAD_REQUEST);
//            }
//
//            // Setear fecha de registro
//            datos.setFechaRegistro(LocalDate.now());
//
//            // Setear flag según rol seleccionado
//            datos.setEsConcesionariaTaller(datos.getRol() == Usuarios.Rol.CONCESIONARIO);
//
//            // Guardar usuario nuevo
//            usuariosService.save(datos);
//
//            //return new ResponseEntity<>("Usuario registrado con éxito", HttpStatus.CREATED);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(java.util.Map.of(
//                            "mensaje", "Usuario registrado con éxito",
//                            "usuario", new UsuariosDTO(datos)
//                    ));
//
//        } catch (Exception e) {
//            return new ResponseEntity<>("Error al registrar usuario: " + e.getMessage(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_ADMIN')")
    @DeleteMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable long usuarioId) {
        try {
            usuariosService.eliminarCuenta(usuarioId);
            return ResponseEntity.ok("Cuenta eliminada");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
        }
    }

    @GetMapping("/usuarios/publico/{usuarioId}")
    public ResponseEntity<?> getUsuarioPublico(@PathVariable long usuarioId) {
        try {
            var u = usuariosService.findById(usuarioId);
            return ResponseEntity.ok(new UsuarioPublicoDTO(u));
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener usuario público");
        }
    }

    @GetMapping("/usuarios/{id}/publicaciones/count")
    public ResponseEntity<?> contarPublicacionesUsuario(@PathVariable long id) {
        long count = usuariosService.contarPublicaciones(id);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    @GetMapping("/usuarios/{usuarioId}/fotoPerfil")
    public ResponseEntity<?> getFotoPerfil(@PathVariable long usuarioId) {
        try {
            Usuarios usuario = usuariosService.findById(usuarioId);
            if (usuario == null || usuario.getProfilePicUrl() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no tiene foto de perfil");
            }
            return ResponseEntity.ok(usuario.getProfilePicUrl());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la foto de perfil");
        }
    }

    @PostMapping("/usuarios/{usuarioId}/fotoPerfil")
    public ResponseEntity<?> subirFotoPerfil(
            @PathVariable long usuarioId,
            @RequestParam("file") MultipartFile file) {
        try {
            String url = imagenVehiculoService.subirImagenPerfil(usuarioId, file);
            return ResponseEntity.ok(url);
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al subir la foto de perfil");
        }
    }

    @DeleteMapping("/usuarios/{usuarioId}/fotoPerfil")
    public ResponseEntity<?> eliminarFotoPerfil(@PathVariable long usuarioId) {
        try {
            imagenVehiculoService.eliminarImagenPerfil(usuarioId);
            return ResponseEntity.ok("Foto de perfil eliminada correctamente");
        } catch (NotFoundError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al eliminar la foto de perfil");
        }
    }

}
