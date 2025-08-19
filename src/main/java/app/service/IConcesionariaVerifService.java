package app.service;

import app.controller.dtos.AddConcesionariaVerifDTO;
import app.model.entity.ConcesionariaVerif;

public interface IConcesionariaVerifService {
    public ConcesionariaVerif findById(long id);

    public void save(ConcesionariaVerif concesionariaVerif);

    public void saveVerificacionDesdeDTO(AddConcesionariaVerifDTO dto);

    public void eliminarConcesionariaVerif(long concesionariaId);

    public void cambiarEstadoVerificacion (long verificacionId,
                                           ConcesionariaVerif.EstadoVerificacion nuevoEstado,
                                           String notas);
}
