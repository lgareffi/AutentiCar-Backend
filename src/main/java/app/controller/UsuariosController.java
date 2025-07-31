package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.UsuariosDTO;
import app.controller.dtos.VehiculosDTO;
import app.controller.dtos.VentasDTO;
import app.model.dao.IUsuariosDAO;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import app.model.entity.Ventas;
import app.service.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("")
public class UsuariosController {
    @Autowired
    private IUsuariosService usuariosService;
    @Autowired
    private IUsuariosDAO usuariosDAO;

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> getVecino(@PathVariable long usuarioId) {
        try {
            Usuarios usuario = usuariosService.findById(usuarioId);
            UsuariosDTO dto = new UsuariosDTO(usuario);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro al usuario con id: " + usuarioId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

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
                return new ResponseEntity<>("Fuiste loggeado exitosamente", HttpStatus.OK);
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


    @PostMapping("/register")
    public ResponseEntity<?> registerUsuario(@RequestBody Usuarios datos) {
        try {
            // Verificar si ya existe usuario con ese mail
            Usuarios existenteMail = usuariosDAO.findByMail(datos.getMail());
            if (existenteMail != null) {
                return new ResponseEntity<>("Ya existe una cuenta registrada con ese correo", HttpStatus.CONFLICT);
            }

            // Verificar si ya existe usuario con ese DNI
            Usuarios existenteDni = usuariosDAO.findByDni(datos.getDni());
            if (existenteDni != null) {
                return new ResponseEntity<>("Ya existe una cuenta registrada con ese DNI", HttpStatus.CONFLICT);
            }

            // Setear fecha de registro
            datos.setFechaRegistro(LocalDate.now());

            // Guardar usuario nuevo
            usuariosService.save(datos);

            return new ResponseEntity<>("Usuario registrado con éxito", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar usuario: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
