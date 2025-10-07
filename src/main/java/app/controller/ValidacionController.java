package app.controller;

import app.service.IDNIUsuarioService;
import app.service.IUsuariosService;
import app.service.IVehiculosService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/usuarios/validacion")
@RequiredArgsConstructor
public class ValidacionController {

    @Autowired
    private IDNIUsuarioService dniUsuarioService;

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @PostMapping(value = "/frente", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFrente(@RequestParam("file") MultipartFile file) {
        dniUsuarioService.subirFrente(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @PostMapping(value = "/dorso", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirDorso(@RequestParam("file") MultipartFile file) {
        dniUsuarioService.subirDorso(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @GetMapping("/frente-url")
    public ResponseEntity<?> getFrenteUrl() {
        Long me = currentUserId();
        return ResponseEntity.ok(new UrlDTO(dniUsuarioService.getFrenteUrl(me)));
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @GetMapping("/dorso-url")
    public ResponseEntity<?> getDorsoUrl() {
        Long me = currentUserId();
        return ResponseEntity.ok(new UrlDTO(dniUsuarioService.getDorsoUrl(me)));
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_TALLER','ROL_CONCESIONARIO','ROL_ADMIN')")
    @PostMapping("/enviar")
    public ResponseEntity<?> enviar() {
        dniUsuarioService.enviar();
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ROL_ADMIN')")
    @GetMapping("/{id}/dni")
    public ResponseEntity<?> verDni(@PathVariable Long id) {
        String frente = dniUsuarioService.getFrenteUrl(id);
        String dorso  = dniUsuarioService.getDorsoUrl(id);
        return ResponseEntity.ok(new DniDTO(frente, dorso));
    }

    @PreAuthorize("hasAnyAuthority('ROL_ADMIN')")
    @PostMapping("/{id}/validar")
    public ResponseEntity<?> validar(@PathVariable Long id) {
        dniUsuarioService.validar(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN')")
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long id) {
        dniUsuarioService.rechazar(id);
        return ResponseEntity.ok().build();
    }

    private Long currentUserId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    @lombok.Data @lombok.AllArgsConstructor
    static class UrlDTO { private String url; }
    @lombok.Data @lombok.AllArgsConstructor
    static class DniDTO { private String frenteUrl; private String dorsoUrl; }
}

