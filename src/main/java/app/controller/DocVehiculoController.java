package app.controller;


import app.controller.dtos.DocVehiculoDTO;
import app.controller.dtos.VehiculosDTO;
import app.model.entity.DocVehiculo;
import app.service.IDocVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentos")
public class DocVehiculoController {
    @Autowired
    private IDocVehiculoService docVehiculoService;

    @GetMapping("/{documentoId}")
    public ResponseEntity<?> getDocVehiculo(@PathVariable long documentoId) {
        try {
            DocVehiculo docVehiculo = docVehiculoService.findById(documentoId);
            DocVehiculoDTO dto = new DocVehiculoDTO(docVehiculo);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro el documento con id: " + documentoId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }
}
