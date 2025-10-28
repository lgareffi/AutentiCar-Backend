package app.service;

import app.model.dao.IUsuariosDAO;
import app.model.entity.Usuarios;
import app.security.OwnershipGuard;
import app.security.SecurityUtils;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class DNIUsuarioServiceImpl implements IDNIUsuarioService{

    @Autowired
    private  IUsuariosDAO usuariosDAO;

    @Autowired
    private Cloudinary cloudinary;

    @Value("${app.cloudinary.folderRoot:app}")
    private String folderRoot;

    private static final long MAX_BYTES = 15L * 1024 * 1024;

    @Override
    public void subirFrente(MultipartFile file) {
        Long me = SecurityUtils.currentUserId();
        Usuarios u = mustGetUser(me);
        Map<String, String> up = uploadToCloudinary(file, me);
        u.setDniFrenteUrl(up.get("secure_url"));

        if (u.getNivelUsuario() == Usuarios.NivelUsuario.RECHAZADO) {
            u.setNivelUsuario(Usuarios.NivelUsuario.REGISTRADO);
        }
        usuariosDAO.save(u);
    }

    @Override
    public String getFrenteUrl(Long usuarioIdSolicitado) {
        Long me = SecurityUtils.currentUserId();
        OwnershipGuard.requireOwnerOrAdmin(usuarioIdSolicitado);
        Usuarios u = mustGetUser(usuarioIdSolicitado);
        return u.getDniFrenteUrl();
    }

    @Override
    public String getDorsoUrl(Long usuarioIdSolicitado) {
        Long me = SecurityUtils.currentUserId();
        OwnershipGuard.requireOwnerOrAdmin(usuarioIdSolicitado);
        Usuarios u = mustGetUser(usuarioIdSolicitado);
        return u.getDniDorsoUrl();
    }


    @Override
    public void subirDorso(MultipartFile file) {
        Long me = SecurityUtils.currentUserId();
        Usuarios u = mustGetUser(me);
        Map<String, String> up = uploadToCloudinary(file, me);
        u.setDniDorsoUrl(up.get("secure_url"));
        if (u.getNivelUsuario() == Usuarios.NivelUsuario.RECHAZADO) {
            u.setNivelUsuario(Usuarios.NivelUsuario.REGISTRADO);
        }
        usuariosDAO.save(u);
    }

    @Override
    public void enviar() {
        Long me = SecurityUtils.currentUserId();
        Usuarios u = mustGetUser(me);
        if (u.getDniFrenteUrl() == null || u.getDniDorsoUrl() == null) {
            throw new RuntimeException("Debés subir frente y dorso antes de enviar");
        }
        if (u.getNivelUsuario() == Usuarios.NivelUsuario.VALIDADO) {
            throw new RuntimeException("Ya estás validado");
        }
        u.setNivelUsuario(Usuarios.NivelUsuario.PENDIENTE);
        usuariosDAO.save(u);
    }

    @Override
    public void validar(Long usuarioId) {
        if (!SecurityUtils.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Solo ADMIN");
        }
        Usuarios u = mustGetUser(usuarioId);
        if (u.getDniFrenteUrl() == null || u.getDniDorsoUrl() == null) {
            throw new RuntimeException("Faltan imágenes para validar");
        }

        u.setNivelUsuario(Usuarios.NivelUsuario.VALIDADO);
        usuariosDAO.save(u);
    }

    @Override
    public void rechazar(Long usuarioId) {
        if (!SecurityUtils.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Solo ADMIN");
        }
        Usuarios u = mustGetUser(usuarioId);
        u.setNivelUsuario(Usuarios.NivelUsuario.RECHAZADO);
        usuariosDAO.save(u);
    }

    private Map<String, String> uploadToCloudinary(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Archivo vacío");
        if (file.getSize() > MAX_BYTES) throw new RuntimeException("Archivo supera 15MB");

        final String mime = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        final String resourceType = (mime.startsWith("image/") || "application/pdf".equals(mime)) ? "image" : "raw";

        try {
            String folder = folderRoot + "/dni/" + userId;
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

            Map<String, String> out = new java.util.HashMap<>();
            out.put("secure_url", secureUrl);
            out.put("public_id", publicId);
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo archivo: " + e.getMessage(), e);
        }
    }

    private Usuarios mustGetUser(Long id) {
        Usuarios u = usuariosDAO.findById(id);
        if (u == null) throw new app.Errors.NotFoundError("No se encontró el usuario: " + id);
        return u;
    }

}
