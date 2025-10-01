package app.service;

import app.controller.dtos.AddDocumentoDTO;
import app.controller.dtos.DocVehiculoDTO;
import app.model.entity.DocVehiculo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocVehiculoService {
    public DocVehiculo findById(long id);

    public void save(DocVehiculo docVehiculo);

    //public void saveDocumentoDesdeDTO(AddDocumentoDTO dto);

    public DocVehiculoDTO subirDocumento(long vehiculoId,
                                  MultipartFile file,
                                  String nombre,
                                  String tipoDoc,
                                  Long eventoId);

    public List<DocVehiculoDTO> listarPorVehiculo(long vehiculoId);

    public void eliminarDocumento(long documentoId);

}
