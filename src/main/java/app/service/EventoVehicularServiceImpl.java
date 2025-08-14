package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddEventoDTO;
import app.model.dao.IDocVehiculoDAO;
import app.model.dao.IEventoVehicularDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoVehicularServiceImpl implements IEventoVehicularService{
    @Autowired
    private IEventoVehicularDAO eventoVehicularDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Autowired
    private IVehiculosDAO vehiculosDAO;

    @Autowired
    private IDocVehiculoDAO docVehiculoDAO;


    @Override
    public EventoVehicular findById(long id) {
        try {
            EventoVehicular eventoVehicular = eventoVehicularDAO.findById(id);
            return eventoVehicular;
        }catch (Throwable e) {
            System.out.println("Error al buscar el evento con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("El evento no existe");
        }
    }

    @Override
    public void save(EventoVehicular eventoVehicular) {
        try {
            eventoVehicularDAO.save(eventoVehicular);
        }catch (Throwable e){
            throw new Error("Error al guardar el evento" + e.getMessage());
        }
    }

    @Override
    public List<DocVehiculo> getDocVehiculo(long id){
        try {
            EventoVehicular e = this.eventoVehicularDAO.findById(id);
            if (e == null)
                throw new NotFoundError("No se encontro el evento");
            return e.getDocVehiculo() != null ? e.getDocVehiculo() : java.util.Collections.emptyList();
        } catch(Throwable e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    public void saveEventoDesdeDTO(AddEventoDTO dto) {
        // Validar usuario
        Usuarios usuario = this.usuariosDAO.findById(dto.usuarioId);
        if (usuario == null)
            throw new NotFoundError("No se encontró el usuario");

        // Validar vehículo
        Vehiculos vehiculo = this.vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        // Crear el evento
        EventoVehicular evento = new EventoVehicular();
        evento.setTitulo(dto.titulo);
        evento.setDescripcion(dto.descripcion);
        evento.setKilometrajeEvento(dto.kilometrajeEvento);
        evento.setValidadoPorTercero(dto.validadoPorTercero);
        evento.setFechaEvento(LocalDate.now());
        evento.setTipoEvento(EventoVehicular.TipoEvento.valueOf(dto.tipoEvento.toUpperCase()));
        evento.setUsuario(usuario);
        evento.setVehiculo(vehiculo);

        this.eventoVehicularDAO.save(evento);
    }

    @Override
    public void eliminarEvento(long eventoId) {
        EventoVehicular evento = eventoVehicularDAO.findById(eventoId);
        if (evento == null) throw new NotFoundError("Evento no encontrado: " + eventoId);

        // Desasociar documentos (no borrarlos)
        List<DocVehiculo> docs = evento.getDocVehiculo();
        if (docs != null) {
            for (DocVehiculo d : docs) {
                d.setEventoVehicular(null);
                docVehiculoDAO.save(d); // o merge/persist según tu DAO
            }
        }

        // Ahora sí, borrar el evento
        eventoVehicularDAO.delete(evento);
    }

}
