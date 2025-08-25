package app.security;

import org.springframework.security.access.AccessDeniedException;

import app.model.entity.*;
import app.model.dao.*;

public final class OwnershipGuard {
    private OwnershipGuard() {}

    /** 403 si no es dueño ni admin */
    public static void requireOwnerOrAdmin(Long ownerId) {
        Long me = SecurityUtils.currentUserId();
        if (!SecurityUtils.isAdmin() && (ownerId == null || !ownerId.equals(me))) {
            throw new AccessDeniedException("No sos dueño de este recurso");
        }
    }

    // Vehículo
    public static void requireVehiculoOwnerOrAdmin(Long vehiculoId, IVehiculosDAO vehiculosDAO) {
        Vehiculos v = vehiculosDAO.findById(vehiculoId);
        if (v == null) throw new RuntimeException("Vehículo no encontrado");
        requireOwnerOrAdmin(v.getUsuario().getIdUsuario());
    }

    // Publicación
    public static void requirePublicacionOwnerOrAdmin(Long publicacionId, IPublicacionDAO pubDAO) {
        Publicacion p = pubDAO.findById(publicacionId);
        if (p == null) throw new RuntimeException("Publicación no encontrada");
        requireOwnerOrAdmin(p.getUsuario().getIdUsuario());
    }

    // Evento
    public static void requireEventoOwnerOrAdmin(Long eventoId, IEventoVehicularDAO eventosDAO) {
        EventoVehicular e = eventosDAO.findById(eventoId);
        if (e == null) throw new RuntimeException("Evento no encontrado");
        // dueño por vehículo
        requireOwnerOrAdmin(e.getVehiculo().getUsuario().getIdUsuario());
    }

    // Documento
    public static void requireDocumentoOwnerOrAdmin(Long docId, IDocVehiculoDAO docsDAO) {
        DocVehiculo d = docsDAO.findById(docId);
        if (d == null) throw new RuntimeException("Documento no encontrado");
        requireOwnerOrAdmin(d.getVehiculo().getUsuario().getIdUsuario());
    }

    // Imagen
    public static void requireImagenOwnerOrAdmin(Long imgId, IImagenVehiculoDAO imgsDAO) {
        ImagenVehiculo img = imgsDAO.findById(imgId);
        if (img == null) throw new RuntimeException("Imagen no encontrada");
        requireOwnerOrAdmin(img.getVehiculo().getUsuario().getIdUsuario());
    }

    /** Variante: dueñ@ o taller o admin (para casos de eventos/documentos) */
    public static void requireOwnerOrTallerOrAdmin(Long ownerId) {
        Long me = SecurityUtils.currentUserId();
        if (SecurityUtils.isAdmin()) return;
        if (SecurityUtils.isTaller()) return;
        if (ownerId != null && ownerId.equals(me)) return;
        throw new AccessDeniedException("No autorizado");
    }
}
