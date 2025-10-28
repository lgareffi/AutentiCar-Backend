package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.DocVehiculoDTO;
import app.model.dao.IDocVehiculoDAO;
import app.model.dao.IEventoVehicularDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.Vehiculos;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.List;

@Service
public class DocVehiculoServiceImpl implements IDocVehiculoService {
    @Autowired
    private IDocVehiculoDAO docVehiculoDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Autowired
    private IEventoVehicularDAO eventoVehicularDAO;

    @Autowired
    private AiClient aiClient;

    private final Cloudinary cloudinary;
    @Value("${cloudinary.folder-root}")
    private String folderRoot;

    @Autowired
    public DocVehiculoServiceImpl(Cloudinary cloudinary,
                                  IVehiculosDAO vehiculosDAO,
                                  IDocVehiculoDAO docDAO) {
        this.cloudinary = cloudinary;
        this.vehiculosDAO = vehiculosDAO;
        this.docVehiculoDAO = docDAO;
    }

    @Override
    @Transactional
    public DocVehiculo findById(long id) {
        try {
            DocVehiculo docVehiculo = docVehiculoDAO.findById(id);
            return docVehiculo;
        }catch (Throwable e) {
            System.out.println("Error al buscar el documento con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("El documento no existe");
        }
    }

    @Override
    @Transactional
    public void save(DocVehiculo docVehiculo) {
        try {
            docVehiculoDAO.save(docVehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar el documento" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DocVehiculoDTO subirDocumento(long vehiculoId,
                                         MultipartFile file,
                                         String nombre,
                                         String tipoDoc,
                                         Long eventoId) {
        Vehiculos vehiculo = vehiculosDAO.findById(vehiculoId);
        if (vehiculo == null) {
            throw new NotFoundError("No se encontró el vehículo");
        }

        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }

        Long me = (Long) auth.getPrincipal();

        boolean esAdmin  = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .anyMatch("ROL_ADMIN"::equals);
        boolean esTaller = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .anyMatch("ROL_TALLER"::equals);

        Long ownerId = vehiculo.getUsuario().getIdUsuario();

        if (!(esAdmin || esTaller || ownerId.equals(me))) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No estás autorizado para subir documentos a este vehículo");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }

        long maxBytes = 15L * 1024 * 1024; // 15MB
        if (file.getSize() > maxBytes) throw new RuntimeException("Archivo supera 15MB");

        final String mime = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        final String resourceType = (mime.startsWith("image/") || "application/pdf".equals(mime)) ? "image" : "raw";

        if (eventoId == null) {
            throw new RuntimeException("Debe indicar el eventoId para asociar el documento");
        }
        EventoVehicular evento = eventoVehicularDAO.findById(eventoId);
        if (evento == null) throw new NotFoundError("No se encontró el evento");
        if (evento.getVehiculo() == null || evento.getVehiculo().getIdVehiculo() != vehiculoId) {
            throw new RuntimeException("El evento no pertenece al vehículo indicado");
        }

        int riesgoFinal = 0;
        boolean validadoIAFinal = false;

        try {
            AiResponse ai = aiClient.analyze(file);
            if (ai != null && ai.getRiskScore() != null) {
                double score = Math.max(0, Math.min(100, ai.getRiskScore()));
                riesgoFinal = (int) Math.round(score);
                validadoIAFinal = true;
            }
        } catch (Exception ex) {
            System.err.println("AI offline o error: " + ex.getMessage());
        }

        try {
            String folder = folderRoot + "/docs/" + vehiculoId;
            Map<?, ?> upload = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", resourceType,
                            "overwrite", false,
                            "unique_filename", true,
                            "use_filename", true
                    )
            );
            String secureUrl = (String) upload.get("secure_url");
            String publicId = (String) upload.get("public_id");
            if (secureUrl == null || publicId == null)
                throw new RuntimeException("Cloudinary no devolvió URL/publicId");

            DocVehiculo d = new DocVehiculo();
            d.setVehiculo(vehiculo);
            d.setEventoVehicular(evento);
            d.setNombre(nombre != null && !nombre.isBlank() ? nombre : file.getOriginalFilename());
            d.setTipoDoc(tipoDoc != null ? DocVehiculo.TipoDoc.valueOf(tipoDoc.toUpperCase()) : DocVehiculo.TipoDoc.OTRO);
            d.setNivelRiesgo(riesgoFinal);
            d.setValidadoIA(validadoIAFinal);
            d.setFechaSubida(LocalDate.now());

            d.setUrlDoc(secureUrl);
            d.setPublicId(publicId);
            d.setResourceType(resourceType);
            d.setMimeType(mime);

            docVehiculoDAO.save(d);
            return new DocVehiculoDTO(d);
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo documento: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<DocVehiculoDTO> listarPorVehiculo(long vehiculoId) {
        Vehiculos v = vehiculosDAO.findById(vehiculoId);
        if (v == null) throw new NotFoundError("No se encontró el vehículo: " + vehiculoId);

        List<DocVehiculo> docs = docVehiculoDAO.findByVehiculo(v);
        if (docs == null || docs.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return docs.stream().map(DocVehiculoDTO::new).toList();
    }

    @Override
    @Transactional
    public void eliminarDocumento(long documentoId) {
        DocVehiculo d = docVehiculoDAO.findById(documentoId);
        if (d == null){
            throw new NotFoundError("Documento no encontrado");
        }

        Long ownerId = d.getVehiculo().getUsuario().getIdUsuario();
        app.security.OwnershipGuard.requireOwnerOrAdmin(ownerId);

        try {
            if (d.getPublicId() != null) {
                cloudinary.uploader().destroy(
                        d.getPublicId(),
                        ObjectUtils.asMap("resource_type", d.getResourceType() != null ? d.getResourceType() : "raw")
                );
            }
            docVehiculoDAO.delete(d);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar el documento: " + e.getMessage(), e);
        }
    }

    public AiMlFullResponse obtenerAnalisisMlPorDocId(long docId) {
        DocVehiculo doc = docVehiculoDAO.findById(docId);
        if (doc == null) throw new NotFoundError("Documento no encontrado");

        String url = doc.getUrlDoc();
        if (url == null || url.isBlank()) throw new RuntimeException("Documento sin URL");

        AiClient.DownloadedFile dl = aiClient.download(url);

        String dbMime = doc.getMimeType();
        String baseName = (doc.getNombre() != null && !doc.getNombre().isBlank())
                ? doc.getNombre()
                : "documento";

        String ext;
        if ("application/pdf".equalsIgnoreCase(dbMime)) {
            ext = ".pdf";
        } else if (dbMime != null && dbMime.startsWith("image/")) {
            String e = dbMime.substring("image/".length());
            if ("jpeg".equalsIgnoreCase(e)) e = "jpg";
            ext = "." + e;
        } else {
            ext = inferExtFromUrl(url);
            if (ext == null) ext = "";
        }

        String filename = baseName.endsWith(ext) ? baseName : (baseName + ext);

        String contentTypeForMl = (dbMime != null) ? dbMime : dl.contentType;
        return aiClient.analyzeBytes(dl.bytes, filename, contentTypeForMl);
    }

    private String inferExtFromUrl(String url) {
        String lower = url.toLowerCase(Locale.ROOT);
        if (lower.contains(".pdf")) return ".pdf";
        if (lower.contains(".jpg") || lower.contains(".jpeg")) return ".jpg";
        if (lower.contains(".png")) return ".png";
        if (lower.contains(".webp")) return ".webp";
        return null;
    }

}
