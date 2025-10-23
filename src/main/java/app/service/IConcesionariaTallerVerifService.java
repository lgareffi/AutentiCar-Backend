package app.service;

import app.controller.dtos.AddConcesionariaTallerVerifDTO;
import app.model.entity.ConcesionarioTallerVerif;
import org.springframework.web.multipart.MultipartFile;

public interface IConcesionariaTallerVerifService {
    public ConcesionarioTallerVerif findById(long id);

    public void save(ConcesionarioTallerVerif concesionarioTallerVerif);

    public void saveVerificacionDesdeDTO(AddConcesionariaTallerVerifDTO dto);

    public void eliminarConcesionariaTallerVerif(long id);

    public ConcesionarioTallerVerif findByUsuarioId(long usuarioId);

    void subirArchivo(MultipartFile file, Long usuarioId);
    void enviarValidacion(Long usuarioId, String domicilio);
    void validar(Long usuarioId);
    void rechazar(Long usuarioId);
    String getArchivoUrl(Long usuarioId);

}
