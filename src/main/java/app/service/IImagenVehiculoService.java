package app.service;

import app.controller.dtos.AddImagenDTO;
import app.controller.dtos.ImagenVehiculoDTO;
import app.model.entity.ImagenVehiculo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImagenVehiculoService {
    public ImagenVehiculo findById(long id);

    public void save(ImagenVehiculo imagenVehiculo);

//    public void saveImagenDesdeDTO(AddImagenDTO dto);

    public ImagenVehiculoDTO subirImagen(long vehiculoId, MultipartFile file);

    public List<ImagenVehiculoDTO> subirMultiples(long vehiculoId, List<MultipartFile> files);

    public void eliminarImagen(long imagenId);

//    public ImagenVehiculo findByUrl(String urlImagen);
}
