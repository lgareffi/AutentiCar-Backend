package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.UsuariosDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.config.JwtTokenProvider;
import app.model.entity.Usuarios;
import app.service.IUsuariosService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUsuariosService usuariosService;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    public AuthController(IUsuariosService usuariosService, PasswordEncoder encoder, JwtTokenProvider jwt) {
        this.usuariosService = usuariosService;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public static class AuthSuccessResponse {
        private final boolean ok = true;
        private String mensaje;
        private UsuariosDTO usuario;
        private String token;

        public AuthSuccessResponse(String mensaje, UsuariosDTO usuario, String token) {
            this.mensaje = mensaje;
            this.usuario = usuario;
            this.token = token;
        }
        public boolean isOk() { return ok; }
        public String getMensaje() { return mensaje; }
        public UsuariosDTO getUsuario() { return usuario; }
        public String getToken() { return token; }
    }

    public static class ErrorResponse {
        private final boolean ok = false;
        private String mensaje;
        public ErrorResponse(String mensaje) { this.mensaje = mensaje; }
        public boolean isOk() { return ok; }
        public String getMensaje() { return mensaje; }
    }

    // ====== REQUEST DTOs ======
    public static class LoginRequest {
        private String mail;
        private String password;
        public String getMail() { return mail; }
        public void setMail(String mail) { this.mail = mail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String nombre;
        private String apellido;        // opcional
        private int dni;                // obligatorio
        private String mail;            // obligatorio
        private String password;        // obligatorio
        private String telefonoCelular; // obligatorio
        private Usuarios.Rol rol;       // obligatorio (PARTICULAR / CONCESIONARIO / TALLER)

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public int getDni() { return dni; }
        public void setDni(int dni) { this.dni = dni; }
        public String getMail() { return mail; }
        public void setMail(String mail) { this.mail = mail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getTelefonoCelular() { return telefonoCelular; }
        public void setTelefonoCelular(String telefonoCelular) { this.telefonoCelular = telefonoCelular; }
        public Usuarios.Rol getRol() { return rol; }
        public void setRol(Usuarios.Rol rol) { this.rol = rol; }
    }

    // ====== LOGIN ======
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            if (req.getMail() == null || req.getPassword() == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Mail y contraseña son obligatorios"));
            }

            Usuarios u;
            try {
                u = usuariosService.findByMail(req.getMail()); // lanza NotFoundError si no existe
            } catch (NotFoundError nf) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Credenciales inválidas"));
            }

            boolean ok = encoder.matches(req.getPassword(), u.getPassword());
            // Fallback temporal si aún tenés contraseñas planas en DB (quitá esto cuando migres todo a BCrypt):
            if (!ok) ok = req.getPassword().equals(u.getPassword());
            if (!ok) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Credenciales inválidas"));
            }

            String token = jwt.generarToken(u);
            return ResponseEntity.ok(new AuthSuccessResponse(
                    "Login exitoso",
                    new UsuariosDTO(u),
                    token
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    // ====== REGISTER ======
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            // Validaciones básicas
            if (req.getMail() == null || req.getMail().isBlank())
                return ResponseEntity.badRequest().body(new ErrorResponse("El mail es obligatorio"));
            if (req.getPassword() == null || req.getPassword().isBlank())
                return ResponseEntity.badRequest().body(new ErrorResponse("La contraseña es obligatoria"));
            if (req.getRol() == null)
                return ResponseEntity.badRequest().body(new ErrorResponse("Debe seleccionar un rol válido"));
            if (req.getDni() <= 0)
                return ResponseEntity.badRequest().body(new ErrorResponse("El DNI debe ser un número válido"));
            if (req.getTelefonoCelular() == null || req.getTelefonoCelular().isBlank())
                return ResponseEntity.badRequest().body(new ErrorResponse("El teléfono celular es obligatorio"));
            // (Opcional) validación simple de formato:
            // if (!req.getTelefonoCelular().matches("^[+\\d][\\d\\s\\-().]{6,}$"))
            //     return ResponseEntity.badRequest().body(new ErrorResponse("Formato de teléfono inválido"));

            // Duplicados
            boolean emailEnUso;
            try {
                emailEnUso = (usuariosService.findByMail(req.getMail()) != null);
            } catch (NotFoundError nf) {
                emailEnUso = false; // no existe -> OK para crear
            }
            if (emailEnUso) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Ya existe una cuenta registrada con ese correo"));
            }

            // ---- Duplicados por DNI (si tu service.findByDni también lanza NotFoundError)
            boolean dniEnUso;
            try {
                dniEnUso = (usuariosService.findByDni(req.getDni()) != null);
            } catch (NotFoundError nf) {
                dniEnUso = false;
            }
            if (dniEnUso) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Ya existe una cuenta registrada con ese DNI"));
            }

            String telefono = req.getTelefonoCelular().replaceAll("\\D", ""); // eliminar espacios, guiones, etc.
            if (!telefono.startsWith("549")) {
                telefono = "549" + telefono;
            }

            // Crear y guardar
            Usuarios u = new Usuarios();
            u.setNombre(req.getNombre());
            u.setApellido(req.getApellido());
            u.setDni(req.getDni());
            u.setMail(req.getMail());
            u.setPassword(encoder.encode(req.getPassword())); // BCrypt
            u.setFechaRegistro(LocalDate.now());
            u.setRol(req.getRol());
            u.setEsConcesionariaTaller(req.getRol() == Usuarios.Rol.CONCESIONARIO);
            u.setTelefonoCelular(telefono);
            u.setNivelUsuario(Usuarios.NivelUsuario.REGISTRADO);
            u.setDniFrenteUrl(null);
            u.setDniDorsoUrl(null);
            u.setQuiereOferta(false);
            usuariosService.save(u);

            String token = jwt.generarToken(u);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthSuccessResponse(
                            "Usuario registrado con éxito",
                            new UsuariosDTO(u),
                            token
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Error al registrar usuario: " + e.getMessage()));
        }
    }
}