package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddConcesionariaTallerVerifDTO;
import app.model.dao.IConcesionariaTallerVerifDAO;
import app.model.dao.IUsuariosDAO;
import app.model.entity.ConcesionarioTallerVerif;
import app.model.entity.Usuarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class ConcesionariaTallerVerifServiceImpl implements IConcesionariaTallerVerifService {
    @Autowired
    private IConcesionariaTallerVerifDAO concesionariaTallerVerifDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IConcesionariaTallerVerifDAO concesionariaVerifDAO;

    @Autowired
    private Cloudinary cloudinary;

    @Value("${app.cloudinary.folderRoot:app}")
    private String folderRoot;

    private static final long MAX_BYTES = 15L * 1024 * 1024;

    @Override
    @Transactional
    public ConcesionarioTallerVerif findById(long id) {
        try {
            ConcesionarioTallerVerif concesionarioTallerVerif = concesionariaTallerVerifDAO.findById(id);
            return concesionarioTallerVerif;
        }catch (Throwable e) {
            System.out.println("Error al buscar la verificación de concesionaria con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La verificación de concesionaria no existe");
        }
    }

    @Override
    @Transactional
    public void save(ConcesionarioTallerVerif concesionarioTallerVerif) {
        try {
            concesionariaTallerVerifDAO.save(concesionarioTallerVerif);
        }catch (Throwable e){
            throw new Error("Error al guardar la verificación" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void saveVerificacionDesdeDTO(AddConcesionariaTallerVerifDTO dto) {
        Usuarios usuario = usuariosDAO.findById(dto.usuarioId);
        if (usuario == null) {
            throw new NotFoundError("No se encontró al usuario: " + dto.usuarioId);
        }

        ConcesionarioTallerVerif cv = concesionariaTallerVerifDAO.findByUsuarioId(usuario.getIdUsuario());

        if (cv == null) {
            cv = new ConcesionarioTallerVerif();
            cv.setUsuario(usuario);
            cv.setArchivoUrl(null);
            cv.setDomicilio(dto.domicilio);

            concesionariaTallerVerifDAO.save(cv);

        // 6) Cambiar el nivel del usuario a PENDIENTE (manteniendo tu lógica)
        usuario.setNivelUsuario(Usuarios.NivelUsuario.PENDIENTE);
        usuariosDAO.save(usuario);
        }
    }

    @Override
    @Transactional
    public void eliminarConcesionariaTallerVerif(long id) {
        try {
            ConcesionarioTallerVerif cv = concesionariaTallerVerifDAO.findById(id);

            concesionariaTallerVerifDAO.delete(cv);

        } catch (NotFoundError e) {
            throw e;
        } catch (Throwable e) {
            throw new Error("Error al eliminar la verificación de concesionario/taller: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void subirArchivo(MultipartFile file, Long usuarioId) {
        Usuarios usuario = usuariosDAO.findById(usuarioId);
        if (usuario == null) throw new NotFoundError("Usuario no encontrado");

        ConcesionarioTallerVerif verif = concesionariaVerifDAO.findByUsuarioId(usuarioId);
        if (verif == null) {
            verif = new ConcesionarioTallerVerif();
            verif.setUsuario(usuario);
        }

        Map<String, String> up = uploadToCloudinary(file, usuarioId);
        verif.setArchivoUrl(up.get("secure_url"));

        concesionariaVerifDAO.save(verif);

        // Si estaba RECHAZADO, se resetea
        if (usuario.getNivelUsuario() == Usuarios.NivelUsuario.RECHAZADO) {
            usuario.setNivelUsuario(Usuarios.NivelUsuario.REGISTRADO);
        }
        usuariosDAO.save(usuario);
    }

    @Override
    @Transactional
    public void enviarValidacion(Long usuarioId, String domicilio) {
        Usuarios usuario = usuariosDAO.findById(usuarioId);
        if (usuario == null) throw new NotFoundError("Usuario no encontrado");

        ConcesionarioTallerVerif verif = concesionariaVerifDAO.findByUsuarioId(usuarioId);
        if (verif == null || verif.getArchivoUrl() == null) {
            throw new RuntimeException("Debés subir un archivo antes de enviar la validación");
        }

        verif.setDomicilio(domicilio);
        concesionariaVerifDAO.save(verif);

        usuario.setNivelUsuario(Usuarios.NivelUsuario.PENDIENTE);
        usuariosDAO.save(usuario);
    }

    @Override
    @Transactional
    public void validar(Long usuarioId) {
        Usuarios usuario = usuariosDAO.findById(usuarioId);
        if (usuario == null) throw new NotFoundError("Usuario no encontrado");

        ConcesionarioTallerVerif verif = concesionariaVerifDAO.findByUsuarioId(usuarioId);
        if (verif == null || verif.getArchivoUrl() == null) {
            throw new RuntimeException("Faltan datos para validar");
        }

        usuario.setNivelUsuario(Usuarios.NivelUsuario.VALIDADO);
        usuariosDAO.save(usuario);
    }

    @Override
    @Transactional
    public void rechazar(Long usuarioId) {
        Usuarios usuario = usuariosDAO.findById(usuarioId);
        if (usuario == null) throw new NotFoundError("Usuario no encontrado");

        usuario.setNivelUsuario(Usuarios.NivelUsuario.RECHAZADO);
        usuariosDAO.save(usuario);
    }

    @Override
    public String getArchivoUrl(Long usuarioId) {
        ConcesionarioTallerVerif verif = concesionariaVerifDAO.findByUsuarioId(usuarioId);
        return verif != null ? verif.getArchivoUrl() : null;
    }


    private Map<String, String> uploadToCloudinary(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Archivo vacío");
        if (file.getSize() > MAX_BYTES) throw new RuntimeException("Archivo supera 15MB");

        final String mime = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        final String resourceType = (mime.startsWith("image/") || "application/pdf".equals(mime)) ? "image" : "raw";

        try {
            String folder = folderRoot + "/concesionaria-taller/" + userId;
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

}
