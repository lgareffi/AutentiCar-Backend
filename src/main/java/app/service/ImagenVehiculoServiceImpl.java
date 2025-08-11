package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddImagenDTO;
import app.controller.dtos.ImagenVehiculoDTO;
import app.model.dao.IImagenVehiculoDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.Vehiculos;
import app.model.entity.ImagenVehiculo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

    private final Cloudinary cloudinary;
    @Value("${cloudinary.folder-root}")
    private String folderRoot;

    public ImagenVehiculoServiceImpl(Cloudinary cloudinary,
                                     IVehiculosDAO vehiculosDAO,
                                     IImagenVehiculoDAO imagenDAO) {
        this.cloudinary = cloudinary;
        this.vehiculosDAO = vehiculosDAO;
        this.imagenVehiculoDAO = imagenDAO;
    }

    @Override
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
    public void save(ImagenVehiculo imagenVehiculo) {
        try {
            imagenVehiculoDAO.save(imagenVehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar la imágen" + e.getMessage());
        }
    }

    @Override
    public ImagenVehiculoDTO subirImagen(long vehiculoId, MultipartFile file) {
        Vehiculos vehiculo = vehiculosDAO.findById(vehiculoId);
        if (vehiculo == null) throw new NotFoundError("No se encontró el vehículo");

        long cantidad = imagenVehiculoDAO.countByVehiculo(vehiculo);
        if (cantidad >= 30) throw new RuntimeException("El vehículo ya tiene 30 imágenes");

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
            if (secureUrl == null) throw new RuntimeException("No se obtuvo URL de Cloudinary");

            ImagenVehiculo img = new ImagenVehiculo();
            img.setUrlImagen(secureUrl);
            img.setFechaSubida(LocalDate.now());
            img.setVehiculo(vehiculo);
            imagenVehiculoDAO.save(img);

            return new ImagenVehiculoDTO(img);

        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ImagenVehiculoDTO> subirMultiples(long vehiculoId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) throw new RuntimeException("No se enviaron archivos");
        List<ImagenVehiculoDTO> out = new ArrayList<>();
        for (MultipartFile f : files) {
            out.add(subirImagen(vehiculoId, f));
        }
        return out;
    }

    @Override
    public void eliminarImagen(long imagenId) {
        ImagenVehiculo img = imagenVehiculoDAO.findById(imagenId); // tu DAO ya lanza NotFound si no existe
        // Si más adelante almacenás publicId, acá podés borrar también de Cloudinary con destroy(publicId)
        // cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        // Por ahora, sólo en BD:
        // necesitás agregar delete en tu DAO si no existe
        // entityManager.remove(img);
        throw new UnsupportedOperationException("Implementar delete en DAO si querés borrar");
    }

//    @Override
//    public void saveImagenDesdeDTO(AddImagenDTO dto) {
//        // Buscar vehículo
//        Vehiculos vehiculo = this.vehiculosDAO.findById(dto.getVehiculoId());
//        if (vehiculo == null)
//            throw new NotFoundError("No se encontró el vehículo con ID: " + dto.getVehiculoId());
//
//        // Crear imagen
//        ImagenVehiculo imagen = new ImagenVehiculo();
//        imagen.setUrlImagen(dto.getUrlImagen());
//        imagen.setFechaSubida(LocalDate.now());
//        imagen.setVehiculo(vehiculo);
//
//        this.imagenVehiculoDAO.save(imagen);
//    }

}
