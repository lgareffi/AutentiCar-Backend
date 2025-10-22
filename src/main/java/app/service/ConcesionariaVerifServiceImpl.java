package app.service;

import app.Errors.NotFoundError;
import app.controller.dtos.AddConcesionariaVerifDTO;
import app.model.dao.IConcesionariaVerifDAO;
import app.model.dao.IUsuariosDAO;
import app.model.entity.ConcesionarioTallerVerif;
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
    public ConcesionarioTallerVerif findById(long id) {
        try {
            ConcesionarioTallerVerif concesionarioTallerVerif = concesionariaVerifDAO.findById(id);
            return concesionarioTallerVerif;
        }catch (Throwable e) {
            System.out.println("Error al buscar la verificación de concesionaria con ID: " + id + " - " + e.getMessage());
            throw new NotFoundError("La verificación de concesionaria no existe");
        }
    }

    @Override
    @Transactional
    public void save(ConcesionarioTallerVerif concesionarioTallerVerif) {
        try {
            concesionariaVerifDAO.save(concesionarioTallerVerif);
        }catch (Throwable e){
            throw new Error("Error al guardar la verificación" + e.getMessage());
        }
    }

//    @Override
//    @Transactional
//    public void saveVerificacionDesdeDTO(AddConcesionariaVerifDTO dto) {
//        // 1) Usuario
//        Usuarios usuario = usuariosDAO.findById(dto.usuarioId);
//        if (usuario == null) {
//            throw new NotFoundError("No se encontró al usuario: " + dto.usuarioId);
//        }
//
//        // 2) Buscar existente por usuario (1:1)
//        ConcesionarioTallerVerif cv = concesionariaVerifDAO.findByUsuarioId(usuario.getIdUsuario());
//
//        if (cv == null) {
//            // Crear nueva
//            cv = new ConcesionarioTallerVerif();
//            cv.setUsuario(usuario);
//            cv.setArchivoUrl();
//            cv.setDomicilio();
//
//        // 4) Persistir (sin try/catch innecesario; si hay error de integridad, que se vea la causa real)
//        concesionariaVerifDAO.save(cv);
//    }

//    @Override
//    @Transactional
//    public void saveVerificacionDesdeDTO(AddConcesionariaVerifDTO dto) {
//        // 1) Usuario
//        Usuarios usuario = usuariosDAO.findById(dto.usuarioId);
//        if (usuario == null) {
//            throw new NotFoundError("No se encontró al usuario");
//        }
//
//        // 2) Evitar duplicados (la relación es 1:1 por usuario)
//        ConcesionariaVerif existente = concesionariaVerifDAO.findByUsuarioId(usuario.getIdUsuario());
//        if (existente != null) {
//            throw new IllegalStateException("El usuario ya tiene una solicitud de verificación de concesionaria");
//        }
//
//        ConcesionariaVerif cv = new ConcesionariaVerif();
//        cv.setUsuario(usuario);
//        cv.setRazonSocial(dto.razonSocial);
//        cv.setCuit(dto.cuit);
//        cv.setNotas(dto.notas);
//        cv.setFechaSolicitud(LocalDate.now());
//        cv.setFechaActualizacion(LocalDate.now());
//        cv.setEstado(ConcesionariaVerif.EstadoVerificacion.PENDIENTE);
//
//        try {
//            concesionariaVerifDAO.save(cv);
//        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
//            throw new IllegalStateException("El usuario ya tiene una solicitud de verificación de concesionaria", ex);
//        }
//    }


}
