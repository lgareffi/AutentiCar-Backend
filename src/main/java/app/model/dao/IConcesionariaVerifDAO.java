package app.model.dao;

import app.model.entity.ConcesionariaVerif;

public interface IConcesionariaVerifDAO {
    public ConcesionariaVerif findById(long id);

    public void save(ConcesionariaVerif concesionariaVerif);

    public void delete(ConcesionariaVerif concesionariaVerif);

    public ConcesionariaVerif findByUsuarioId(long usuarioId);
}
