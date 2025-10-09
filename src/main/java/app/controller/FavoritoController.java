package app.controller;

import app.Errors.NotFoundError;
import app.controller.dtos.PublicacionDTO;
import app.model.entity.Publicacion;
import app.security.SecurityUtils;
import app.service.IFavoritoService;
import app.service.IPublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios/{usuarioId}/favoritos")
public class FavoritoController {
    @Autowired
    private IFavoritoService favoritoService;

    @Autowired
    private IPublicacionService publicacionService;

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN', 'ROL_TALLER','ROL_CONCESIONARIO')")
    @PostMapping("/{publicacionId}")
    public ResponseEntity<?> marcar(
            @PathVariable long usuarioId,
            @PathVariable long publicacionId
    ) {
        try {
            SecurityUtils.requireAdminOrSelf(usuarioId);

            favoritoService.marcar(usuarioId, publicacionId);
            return ResponseEntity.ok().build();
        } catch (NotFoundError nf) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(nf.getMessage());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN', 'ROL_TALLER','ROL_CONCESIONARIO')")
    @DeleteMapping("/{publicacionId}")
    public ResponseEntity<?> desmarcar(
            @PathVariable long usuarioId,
            @PathVariable long publicacionId
    ) {
        try {
            SecurityUtils.requireAdminOrSelf(usuarioId);

            favoritoService.desmarcar(usuarioId, publicacionId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundError nf) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(nf.getMessage());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ROL_USER','ROL_ADMIN', 'ROL_TALLER','ROL_CONCESIONARIO')")
    @GetMapping
    public ResponseEntity<?> listar(@PathVariable long usuarioId) {
        try {
            SecurityUtils.requireAdminOrSelf(usuarioId);

            List<Publicacion> pubs = favoritoService.listarPublicacionesFavoritas(usuarioId);
            List<PublicacionDTO> dtos = pubs.stream().map(PublicacionDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/check/{publicacionId}")
    public ResponseEntity<?> check(
            @PathVariable long usuarioId,
            @PathVariable long publicacionId
    ) {
        try {
            SecurityUtils.requireAdminOrSelf(usuarioId);

            boolean fav = favoritoService.esFavorito(usuarioId, publicacionId);
            return ResponseEntity.ok(Map.of("favorito", fav));
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
