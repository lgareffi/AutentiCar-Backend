package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddConcesionariaVerifDTO;
import app.model.dao.IConcesionariaVerifDAO;
import app.model.dao.IUsuariosDAO;
import app.model.entity.ConcesionariaVerif;
import app.model.entity.Usuarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConcesionariaVerifServiceImpl implements IConcesionariaVerifService{
    @Autowired
    private IConcesionariaVerifDAO concesionariaVerifDAO;

    @Autowired
    private IUsuariosDAO usuariosDAO;

    @Override
    @Transactional
    public ConcesionariaVerif findById(long id) {
        try {
            ConcesionariaVerif concesionariaVerif = concesionariaVerifDAO.findById(id);
            return concesionariaVerif;
        }catch (Throwable e) {
            System.out.println("Error al buscar la verificación de concesionaria con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La verificación de concesionaria no existe");
        }
    }

    @Override
    @Transactional
    public void save(ConcesionariaVerif concesionariaVerif) {
        try {
            concesionariaVerifDAO.save(concesionariaVerif);
        }catch (Throwable e){
            throw new Error("Error al guardar la verificación" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void saveVerificacionDesdeDTO(AddConcesionariaVerifDTO dto) {
        // 1) Usuario
        Usuarios usuario = usuariosDAO.findById(dto.usuarioId);
        if (usuario == null) {
            throw new NotFoundError("No se encontró al usuario");
        }

        // 2) Evitar duplicados (la relación es 1:1 por usuario)
        ConcesionariaVerif existente = concesionariaVerifDAO.findByUsuarioId(usuario.getIdUsuario());
        if (existente != null) {
            throw new IllegalStateException("El usuario ya tiene una solicitud de verificación de concesionaria");
        }

        ConcesionariaVerif cv = new ConcesionariaVerif();
        cv.setUsuario(usuario);
        cv.setRazonSocial(dto.razonSocial);
        cv.setCuit(dto.cuit);
        cv.setNotas(dto.notas);
        cv.setFechaSolicitud(LocalDate.now());
        cv.setFechaActualizacion(LocalDate.now());
        cv.setEstado(ConcesionariaVerif.EstadoVerificacion.PENDIENTE);

        try {
            concesionariaVerifDAO.save(cv);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new IllegalStateException("El usuario ya tiene una solicitud de verificación de concesionaria", ex);
        }
    }

    @Override
    @Transactional
    public void eliminarConcesionariaVerif(long concesionariaId) {
        ConcesionariaVerif concesionariaVerif = concesionariaVerifDAO.findById(concesionariaId);
        if (concesionariaVerif == null) {
            throw new NotFoundError("Verificación de concesionaria no encontrada: " + concesionariaId);
        }
        concesionariaVerifDAO.delete(concesionariaVerif);
    }

    @Override
    @Transactional
    public void cambiarEstadoVerificacion(long verificacionId,
                                          ConcesionariaVerif.EstadoVerificacion nuevoEstado,
                                          String notas) {
//         TODO: cuando implementes roles, validar que quien invoca sea ADMIN

        ConcesionariaVerif cv = concesionariaVerifDAO.findById(verificacionId);
        if (cv == null) {
            throw new NotFoundError("Verificación no encontrada: " + verificacionId);
        }

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Debe indicar el nuevo estado de verificación");
        }

        // Actualizo estado, notas y fecha
        cv.setEstado(nuevoEstado);
        if (notas != null && !notas.isBlank()) {
            cv.setNotas(notas);
        }
        cv.setFechaActualizacion(LocalDate.now());

        concesionariaVerifDAO.save(cv);
    }

}
