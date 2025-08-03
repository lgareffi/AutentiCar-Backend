package app.controller;


import app.controller.dtos.AddDocumentoDTO;
import app.controller.dtos.DocVehiculoDTO;
import app.controller.dtos.VehiculosDTO;
import app.model.entity.DocVehiculo;
import app.service.IDocVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<?> agregarDocumento(@RequestBody AddDocumentoDTO dto) {
        try {
            docVehiculoService.saveDocumentoDesdeDTO(dto);
            return new ResponseEntity<>("Documento agregado correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
