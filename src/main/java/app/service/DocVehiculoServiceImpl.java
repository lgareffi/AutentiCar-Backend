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
                                         Integer nivelRiesgo,
                                         Boolean validadoIA,
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

        // validaciones básicas
        long maxBytes = 15L * 1024 * 1024; // 15MB
        if (file.getSize() > maxBytes) throw new RuntimeException("Archivo supera 15MB");

        final String mime = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        // PDF e imágenes como "image" para permitir thumbnails; otros como "raw"
        final String resourceType = (mime.startsWith("image/") || "application/pdf".equals(mime)) ? "image" : "raw";

        // 4) Si vino eventoId, validar que el evento exista y pertenezca al mismo vehículo
        EventoVehicular evento = null;
        if (eventoId != null) {
            evento = eventoVehicularDAO.findById(eventoId);
            if (evento == null) {
                throw new NotFoundError("No se encontró el evento");
            }
            if (evento.getVehiculo() == null || evento.getVehiculo().getIdVehiculo() != vehiculoId) {
                throw new RuntimeException("El evento no pertenece al vehículo indicado");
            }
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
            d.setNombre(nombre != null && !nombre.isBlank() ? nombre : file.getOriginalFilename());
            d.setTipoDoc(tipoDoc != null ? DocVehiculo.TipoDoc.valueOf(tipoDoc.toUpperCase()) : DocVehiculo.TipoDoc.OTRO);
            d.setNivelRiesgo(nivelRiesgo != null ? nivelRiesgo : 0);
            d.setValidadoIA(Boolean.TRUE.equals(validadoIA));
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
            return java.util.Collections.emptyList(); // ← 200 [] en el controller
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

        // validar: dueño del vehículo o admin
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

}
