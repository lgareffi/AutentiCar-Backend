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
        // 1) Validaciones básicas
        if (dto.vehiculoId == null) {
            throw new IllegalArgumentException("vehiculoId es obligatorio");
        }
        if (dto.titulo == null || dto.titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }

        if (dto.tipoEvento == null || dto.tipoEvento.isBlank()) {
            throw new IllegalArgumentException("tipoEvento es obligatorio");
        }

        // Validar vehículo
        Vehiculos vehiculo = this.vehiculosDAO.findById(dto.vehiculoId);
        if (vehiculo == null)
            throw new NotFoundError("No se encontró el vehículo");

        Long ownerId = vehiculo.getUsuario().getIdUsuario();

        // Usuario autenticado y rol
        Long me = SecurityUtils.currentUserId();
        boolean esAdmin  = SecurityUtils.isAdmin();
        boolean esTaller = SecurityUtils.isTaller();
        boolean esUser   = app.security.SecurityUtils.isUser();

        // Determinar quién será el "registrador" del evento
        Long registradorId = esAdmin && dto.usuarioId != null ? dto.usuarioId : me;

        Usuarios registrador = this.usuariosDAO.findById(registradorId);
        if (registrador == null) {
            throw new NotFoundError("No se encontró el usuario registrador");
        }

        // Autorización de la acción
        if (esAdmin) {
            // ok sin más
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

        // Crear el evento
        EventoVehicular evento = new EventoVehicular();
        evento.setTitulo(dto.titulo);
        evento.setDescripcion(dto.descripcion);
        evento.setKilometrajeEvento(dto.kilometrajeEvento);
        evento.setFechaEvento(LocalDate.now());
        evento.setTipoEvento(EventoVehicular.TipoEvento.valueOf(dto.tipoEvento.toUpperCase()));
        evento.setUsuario(registrador);
        evento.setVehiculo(vehiculo);

        //  calcular hash y setear campos on-chain
        String vin = vehiculo.getVin(); // asegurate que Vehiculos tenga getVin()
        if (vin == null || vin.isBlank()) {
            throw new IllegalArgumentException("El vehículo no tiene VIN cargado");
        }
        String hash = EventHash.sha256Evento(evento, vin);
        evento.setHashEvento(hash);
        evento.setBlockchainRecordedAt(null);
        evento.setBlockchainTxId(null);
        evento.setBlockchainError(null);

        this.eventoVehicularDAO.save(evento);

        // Intentar registrar en blockchain (no rompas la transacción si falla)
        try {
            boolean yaExiste = blockchainService.exists(vin, hash); // opcional, útil
            if (!yaExiste) {
                var resp = blockchainService.record(vin, hash);
                if (resp != null && resp.isOk()) {
                    evento.setBlockchainRecordedAt(LocalDateTime.now());
                    // si tu /record devolviera algo como txId dentro de payload, acá lo seteás
                    // evento.setBlockchainTxId(extraerTxId(resp.getPayload()));
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

        // ===== Autorización =====
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new org.springframework.security.access.AccessDeniedException("No autenticado");
        }
        Long me = (Long) auth.getPrincipal(); // subject = id del usuario en tu token

        boolean esAdmin = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .anyMatch("ROL_ADMIN"::equals);

        Long creadorId = (evento.getUsuario() != null) ? evento.getUsuario().getIdUsuario() : null;

        // Permite: ADMIN, o (creador == usuario actual). No más chequeo de dueño del vehículo.
        boolean permitido = esAdmin || (creadorId != null && creadorId.equals(me));
        if (!permitido) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No autorizado para eliminar este evento");
        }

        // Desasociar documentos (no borrarlos)
//        List<DocVehiculo> docs = evento.getDocVehiculo();
//        if (docs != null) {
//            for (DocVehiculo d : docs) {
//                d.setEventoVehicular(null);
//                docVehiculoDAO.save(d); // o merge/persist según tu DAO
//            }
//        }
        List<DocVehiculo> docs = evento.getDocVehiculo();
        if (docs != null && !docs.isEmpty()) {
            for (DocVehiculo d : docs) {
                docVehiculoDAO.delete(d);   // <-- borra cada documento
            }
            //docs.clear(); // opcional: limpia la colección en memoria
        }

        // Ahora sí, borrar el evento
        eventoVehicularDAO.delete(evento);
    }

}
