package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddImagenDTO;
import app.model.dao.IImagenVehiculoDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.Vehiculos;
import app.model.entity.ImagenVehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ImagenVehiculoServiceImpl implements IImagenVehiculoService {
    @Autowired
    private IImagenVehiculoDAO imagenVehiculoDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

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
    public void saveImagenDesdeDTO(AddImagenDTO dto) {
        // Buscar vehículo
        Vehiculos vehiculo = this.vehiculosDAO.findById(dto.getVehiculoId());
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo con ID: " + dto.getVehiculoId());

        // Crear imagen
        ImagenVehiculo imagen = new ImagenVehiculo();
        imagen.setUrlImagen(dto.getUrlImagen());
        imagen.setFechaSubida(LocalDate.now());
        imagen.setVehiculo(vehiculo);

        this.imagenVehiculoDAO.save(imagen);
    }

}
