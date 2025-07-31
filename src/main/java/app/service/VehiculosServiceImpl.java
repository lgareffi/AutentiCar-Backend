package app.service;

import app.Errors.NotFoundError;
import app.model.dao.IVehiculosDAO;
import app.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehiculosServiceImpl implements IVehiculosService{
    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Override
    public Vehiculos findById(long id) {
        try {
            Vehiculos vehiculo = vehiculosDAO.findById(id);
            return vehiculo;
        }catch (Throwable e) {
            System.out.println("Error al buscar el vehiculo con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("El vehiculo no existe");
        }
    }

    @Override
    public void save(Vehiculos vehiculo) {
        try {
            vehiculosDAO.save(vehiculo);
        }catch (Throwable e){
            throw new Error("Error al guardar el vehiculo" + e.getMessage());
        }
    }

    @Override
    public List<Vehiculos> findAll() {
        try {
            List<Vehiculos> vehiculos = vehiculosDAO.findAll();
            if (!vehiculos.isEmpty()) {
                return vehiculos;
            }
        } catch (Throwable e) {
            throw new Error("Error al buscar los vehiculos");
        }
        return null;
    }

    @Override
    public List<DocVehiculo> getDocsVehiculo(long id){
        try {
            Vehiculos v = this.vehiculosDAO.findById(id);
            if (v == null)
                throw new NotFoundError("No se encontro el vehiculo");
            if (v.getDocVehiculo().isEmpty())
                throw new Error("No se encontraron documentos del auto");
            return v.getDocVehiculo();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    public List<EventoVehicular> getEventosVehiculo(long id){
        try {
            Vehiculos v = this.vehiculosDAO.findById(id);
            if (v == null)
                throw new NotFoundError("No se encontro el vehiculo");
            if (v.getEventoVehicular().isEmpty())
                throw new Error("No se encontraron eventos hechos al auto");
            return v.getEventoVehicular();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }


}
