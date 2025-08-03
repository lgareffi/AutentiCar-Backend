package app.controller;

import app.controller.dtos.AddVentasDTO;
import app.controller.dtos.VentasDTO;
import app.model.entity.Ventas;
import app.service.IVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ventas")
public class VentasController {
    @Autowired
    private IVentasService ventasService;

    @GetMapping("/{ventaId}")
    public ResponseEntity<?> getVenta(@PathVariable long ventaId) {
        try {
            Ventas venta = ventasService.findById(ventaId);
            VentasDTO dto = new VentasDTO(venta);
            return new ResponseEntity<>(dto, HttpStatus.OK);

        } catch (Throwable e) {
            String msj = "No se encontro la venta con id: " + ventaId;
            return new ResponseEntity<>(msj, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> agregarVenta(@RequestBody AddVentasDTO dto) {
        try {
            ventasService.saveVentaDesdeDTO(dto);
            return new ResponseEntity<>("Venta registrada correctamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
