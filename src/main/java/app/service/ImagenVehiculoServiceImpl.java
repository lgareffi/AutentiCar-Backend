package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddImagenDTO;
import app.controller.dtos.ImagenVehiculoDTO;
import app.model.dao.IImagenVehiculoDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import app.model.entity.ImagenVehiculo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImagenVehiculoServiceImpl implements IImagenVehiculoService {
    @Autowired
    private IImagenVehiculoDAO imagenVehiculoDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    private final Cloudinary cloudinary;
    @Value("${cloudinary.folder-root}")
    private String folderRoot;

    public ImagenVehiculoServiceImpl(Cloudinary cloudinary,
                                     IVehiculosDAO vehiculosDAO,
                                     IImagenVehiculoDAO imagenDAO,
                                     IUsuariosDAO usuariosDAO) {
        this.cloudinary = cloudinary;
        this.vehiculosDAO = vehiculosDAO;
        this.imagenVehiculoDAO = imagenDAO;
        this.usuariosDAO = usuariosDAO;
    }

    @Override
    @Transactional
    public ImagenVehiculo findById(long id) {
        try {
            ImagenVehiculo imagenVehiculo = imagenVehiculoDAO.findById(id);
            return imagenVehiculo;
        }catch (Throwable e) {
            System.out.println("Error al buscar la imágen del vehiculo con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La imágen no existe");
        }
    }

    @Override
    @Transactional
    public void save(ImagenVehiculo imagenVehiculo) {
        try {
            imagenVehiculoDAO.save(imagenVehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar la imágen" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ImagenVehiculoDTO subirImagen(long vehiculoId, MultipartFile file) {
        Vehiculos vehiculo = vehiculosDAO.findById(vehiculoId);
        if (vehiculo == null) throw new NotFoundError("No se encontró el vehículo");

        if (file == null || file.isEmpty()) throw new RuntimeException("Archivo vacío");
        if (file.getSize() > 10 * 1024 * 1024) throw new RuntimeException("La imagen excede 10MB");

        try {
            String folder = folderRoot + "/vehiculos/" + vehiculoId;
            Map<?, ?> upload = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image",
                            "overwrite", false,
                            "unique_filename", true,
                            "use_filename", true
                    )
            );

            String secureUrl = (String) upload.get("secure_url");
            String publicId  = (String) upload.get("public_id");
            if (secureUrl == null || publicId == null) throw new RuntimeException("Error al obtener datos de Cloudinary");

            ImagenVehiculo img = new ImagenVehiculo();
            img.setUrlImagen(secureUrl);
            img.setPublicId(publicId);
            img.setFechaSubida(LocalDate.now());
            img.setVehiculo(vehiculo);
            imagenVehiculoDAO.save(img);

            return new ImagenVehiculoDTO(img);

        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<ImagenVehiculoDTO> subirMultiples(long vehiculoId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        Vehiculos vehiculo = vehiculosDAO.findById(vehiculoId);
        if (vehiculo == null){
            throw new NotFoundError("Vehículo no encontrado");
        }

        Long ownerId = vehiculo.getUsuario().getIdUsuario();
        app.security.OwnershipGuard.requireOwnerOrAdmin(ownerId);

        long actuales = imagenVehiculoDAO.countByVehiculo(vehiculo);
        if (actuales + files.size() > 30) {
            throw new RuntimeException("El vehículo superaría el máximo de 30 imágenes");
        }

        List<ImagenVehiculoDTO> out = new ArrayList<>();
        for (MultipartFile f : files) {
            out.add(subirImagen(vehiculoId, f));
        }
        return out;
    }

    @Override
    @Transactional
    public void eliminarImagen(long imagenId) {
        ImagenVehiculo img = imagenVehiculoDAO.findById(imagenId);

        Long ownerId = img.getVehiculo().getUsuario().getIdUsuario();
        app.security.OwnershipGuard.requireOwnerOrAdmin(ownerId);

        try {
            cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.emptyMap());
            imagenVehiculoDAO.delete(img);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar la imagen: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public String subirImagenPerfil(long usuarioId, MultipartFile file) {
        Usuarios usuario = usuariosDAO.findById(usuarioId);
        if (usuario == null)
            throw new NotFoundError("No se encontró el usuario con ID " + usuarioId);

        if (file == null || file.isEmpty())
            throw new RuntimeException("Archivo vacío");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new RuntimeException("La imagen excede 10MB");

        try {
            String folder = folderRoot + "/usuarios/perfiles";

            String publicId = folder + "/" + usuarioId;

            Map<?, ?> upload = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", folder,
                            "resource_type", "image",
                            "overwrite", true,
                            "invalidate", true,
                            "unique_filename", false,
                            "use_filename", true
                    )
            );

            String secureUrl = (String) upload.get("secure_url");
            if (secureUrl == null)
                throw new RuntimeException("Error al obtener URL segura de Cloudinary");

            usuario.setProfilePicUrl(secureUrl);
            usuariosDAO.save(usuario);

            return secureUrl;

        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen de perfil: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void eliminarImagenPerfil(long usuarioId) {
        Usuarios usuario = usuariosDAO.findById(usuarioId);
        if (usuario == null)
            throw new NotFoundError("Usuario no encontrado");

        try {
            String publicId = folderRoot + "/usuarios/perfiles/" + usuarioId;
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            usuario.setProfilePicUrl(null);
            usuariosDAO.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando imagen de perfil: " + e.getMessage(), e);
        }
    }

}
