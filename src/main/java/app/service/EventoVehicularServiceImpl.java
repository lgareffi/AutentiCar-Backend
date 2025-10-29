package app.service;

import app.Errors.NotFoundError;
import app.blockchain.EventHash;
import app.controller.dtos.AddEventoDTO;
import app.model.dao.IDocVehiculoDAO;
import app.model.dao.IEventoVehicularDAO;
import app.model.dao.IUsuariosDAO;
import app.model.dao.IVehiculosDAO;
import app.model.entity.DocVehiculo;
import app.model.entity.EventoVehicular;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import app.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.blockchain.BlockchainService;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private BlockchainService blockchainService;


    @Override
    @Transactional
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
    @Transactional
    public void save(EventoVehicular eventoVehicular) {
        try {
            eventoVehicularDAO.save(eventoVehicular);
        }catch (Throwable e){
            throw new Error("Error al guardar el evento" + e.getMessage());
        }
    }

    @Override
    @Transactional
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
    @Transactional
    public Long saveEventoDesdeDTO(AddEventoDTO dto) {
        if (dto.vehiculoId == null) {
            throw new IllegalArgumentException("vehiculoId es obligatorio");
        }
        if (dto.titulo == null || dto.titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }

        if (dto.tipoEvento == null || dto.tipoEvento.isBlank()) {
            throw new IllegalArgumentException("tipoEvento es obligatorio");
        }

        Vehiculos vehiculo = this.vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        Long ownerId = vehiculo.getUsuario().getIdUsuario();

        Long me = SecurityUtils.currentUserId();
        boolean esAdmin  = SecurityUtils.isAdmin();
        boolean esTaller = SecurityUtils.isTaller();
        boolean esUser   = app.security.SecurityUtils.isUser();

        Long registradorId = esAdmin && dto.usuarioId != null ? dto.usuarioId : me;

        Usuarios registrador = this.usuariosDAO.findById(registradorId);
        if (registrador == null) {
            throw new NotFoundError("No se encontró el usuario registrador");
        }

        if (esAdmin) {
            // ok
        } else if (esTaller) {
            if (!registradorId.equals(me)) {
                throw new AccessDeniedException("Un taller solo puede registrar eventos como sí mismo");
            }
        } else if (esUser) {
            if (!ownerId.equals(me)) {
                throw new AccessDeniedException("No podés registrar eventos sobre un vehículo ajeno");
            }
            if (!registradorId.equals(me)) {
                throw new AccessDeniedException("Un usuario solo puede registrar eventos como sí mismo");
            }
        } else {
            throw new AccessDeniedException("Rol no autorizado");
        }

        EventoVehicular evento = new EventoVehicular();
        evento.setTitulo(dto.titulo);
        evento.setDescripcion(dto.descripcion);
        evento.setKilometrajeEvento(dto.kilometrajeEvento);
        evento.setFechaEvento(LocalDate.now());
        evento.setTipoEvento(EventoVehicular.TipoEvento.valueOf(dto.tipoEvento.toUpperCase()));
        evento.setUsuario(registrador);
        evento.setVehiculo(vehiculo);
        evento.setEstaEliminado(false);

        String vin = vehiculo.getVin();
        if (vin == null || vin.isBlank()) {
            throw new IllegalArgumentException("El vehículo no tiene VIN cargado");
        }
        String hash = EventHash.sha256Evento(evento, vin);
        evento.setHashEvento(hash);
        evento.setBlockchainRecordedAt(null);
        evento.setBlockchainTxId(null);
        evento.setBlockchainError(null);

        this.eventoVehicularDAO.save(evento);

        try {
            boolean yaExiste = blockchainService.exists(vin, hash);
            if (!yaExiste) {
                var resp = blockchainService.record(vin, hash);
                if (resp != null && resp.isOk()) {
                    evento.setBlockchainRecordedAt(LocalDateTime.now());
                } else {
                    evento.setBlockchainError("record() no devolvió ok");
                }
            } else {
                evento.setBlockchainRecordedAt(LocalDateTime.now());
            }
        } catch (Exception ex) {
            evento.setBlockchainError(ex.getMessage());
        }

        this.eventoVehicularDAO.save(evento);
        return evento.getIdEvento();
    }

    @Override
    @Transactional
    public void eliminarEvento(long eventoId) {
        EventoVehicular evento = eventoVehicularDAO.findById(eventoId);
        if (evento == null) {
            throw new NotFoundError("Evento no encontrado: " + eventoId);
        }

        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }
        Long me = (Long) auth.getPrincipal();

        boolean esAdmin = app.security.SecurityUtils.isAdmin();

        Long creadorId = (evento.getUsuario() != null) ? evento.getUsuario().getIdUsuario() : null;


        boolean permitido = esAdmin || (creadorId != null && creadorId.equals(me));
        if (!permitido) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No autorizado para eliminar este evento");
        }

        List<DocVehiculo> docs = evento.getDocVehiculo();
        if (docs != null && !docs.isEmpty()) {
            for (DocVehiculo d : docs) {
                docVehiculoDAO.delete(d);
            }
        }

        eventoVehicularDAO.delete(evento);
    }

}
