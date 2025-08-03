package app.service;

import app.controller.dtos.AddDocumentoDTO;
import app.model.entity.DocVehiculo;

public interface IDocVehiculoService {
    public DocVehiculo findById(long id);

    public void save(DocVehiculo docVehiculo);

    public void saveDocumentoDesdeDTO(AddDocumentoDTO dto);

}
