package app.model.dao;

import app.model.entity.ConcesionarioTallerVerif;

public interface IConcesionariaVerifDAO {
    public ConcesionarioTallerVerif findById(long id);

    public void save(ConcesionarioTallerVerif concesionarioTallerVerif);

    public void delete(ConcesionarioTallerVerif concesionarioTallerVerif);

    public ConcesionarioTallerVerif findByUsuarioId(long usuarioId);

}
