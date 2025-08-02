package app.service;

import app.Errors.NotFoundError;
import app.model.dao.IDocVehiculoDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.DocVehiculo;
import app.model.entity.Vehiculos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocVehiculoServiceImpl implements IDocVehiculoService {
    @Autowired
    private IDocVehiculoDAO docVehiculoDAO;

    @Override
    public DocVehiculo findById(long id) {
        try {
            DocVehiculo docVehiculo = docVehiculoDAO.findById(id);
            return docVehiculo;
        }catch (Throwable e) {
            System.out.println("Error al buscar el documento con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("El documento no existe");
        }
    }

    @Override
    public void save(DocVehiculo docVehiculo) {
        try {
            docVehiculoDAO.save(docVehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar el documento" + e.getMessage());
        }
    }
}
