package app.service;

import app.controller.dtos.AddConcesionariaVerifDTO;
import app.model.entity.ConcesionarioTallerVerif;

public interface IConcesionariaVerifService {
    public ConcesionarioTallerVerif findById(long id);

    public void save(ConcesionarioTallerVerif concesionarioTallerVerif);

//    public void saveVerificacionDesdeDTO(AddConcesionariaVerifDTO dto);
//
//    public void eliminarConcesionariaVerif(long concesionariaId);

}
