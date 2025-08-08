package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddDocumentoDTO;
import app.model.dao.IDocVehiculoDAO;
import app.model.dao.IEventoVehicularDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.Vehiculos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DocVehiculoServiceImpl implements IDocVehiculoService {
    @Autowired
    private IDocVehiculoDAO docVehiculoDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Autowired
    private IEventoVehicularDAO eventoVehicularDAO;

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

    @Override
    public void saveDocumentoDesdeDTO(AddDocumentoDTO dto) {
        // Vehículo obligatorio
        Vehiculos vehiculo = vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        // Evento opcional
        EventoVehicular evento = null;
        if (dto.eventoId != null) {
            evento = eventoVehicularDAO.findById(dto.eventoId);
            if (evento == null)
                throw new NotFoundError("No se encontró el evento");

            // (Recomendado) Asegurar que el evento es del mismo vehículo
            if (evento.getVehiculo() == null ||
                    evento.getVehiculo().getIdVehiculo() != vehiculo.getIdVehiculo()) {
                throw new NotFoundError("El evento no pertenece al vehículo indicado");
            }
        }

        // Crear DocVehiculo
        DocVehiculo doc = new DocVehiculo();
        doc.setNombre(dto.nombre);
        doc.setUrlDoc(dto.urlDoc);
        doc.setNivelRiesgo(dto.nivelRiesgo);
        doc.setValidadoIA(dto.validadoIA);
        doc.setFechaSubida(LocalDate.now());
        doc.setTipoDoc(DocVehiculo.TipoDoc.valueOf(dto.tipoDoc.toUpperCase()));
        doc.setVehiculo(vehiculo);
        doc.setEventoVehicular(evento);

        docVehiculoDAO.save(doc);
    }


//    @Override
//    public void saveDocumentoDesdeDTO(AddDocumentoDTO dto) {
//        Vehiculos vehiculo = vehiculosDAO.findById(dto.vehiculoId);
//        if (vehiculo == null)
//            throw new NotFoundError("No se encontró el vehículo");
//
//        EventoVehicular evento = eventoVehicularDAO.findById(dto.eventoId);
//        if (evento == null)
//            throw new NotFoundError("No se encontró el evento");
//
//        DocVehiculo doc = new DocVehiculo();
//        doc.setNombre(dto.nombre);
//        doc.setUrlDoc(dto.urlDoc);
//        doc.setNivelRiesgo(dto.nivelRiesgo);
//        doc.setValidadoIA(dto.validadoIA);
//        doc.setFechaSubida(LocalDate.now());
//        doc.setTipoDoc(DocVehiculo.TipoDoc.valueOf(dto.tipoDoc.toUpperCase()));
//        doc.setVehiculo(vehiculo);
//        doc.setEventoVehicular(evento);
//
//        docVehiculoDAO.save(doc);
//    }

}
