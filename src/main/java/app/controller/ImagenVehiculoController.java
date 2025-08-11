package app.controller;

import app.controller.dtos.AddImagenDTO;
import app.controller.dtos.ImagenVehiculoDTO;
import app.model.entity.ImagenVehiculo;
import app.service.IImagenVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/imagenVehiculo")
public class ImagenVehiculoController {
    @Autowired
    private IImagenVehiculoService imagenVehiculoService;

    @GetMapping("/{imagenId}")
    public ResponseEntity<?> getImagen(@PathVariable long imagenId) {
        try {
            ImagenVehiculo imagenVehiculo = imagenVehiculoService.findById(imagenId);
            ImagenVehiculoDTO dto = new ImagenVehiculoDTO(imagenVehiculo);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro la imágen con id: " + imagenId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

//    @PostMapping
//    public ResponseEntity<?> agregarImagen(@RequestBody AddImagenDTO imagenDTO) {
//        try {
//            imagenVehiculoService.saveImagenDesdeDTO(imagenDTO);
//            return new ResponseEntity<>("Imágen agregada correctamente", HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

}
