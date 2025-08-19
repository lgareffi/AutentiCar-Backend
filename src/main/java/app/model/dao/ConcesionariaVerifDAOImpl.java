package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.ConcesionariaVerif;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.query.Query;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcesionariaVerifDAOImpl implements IConcesionariaVerifDAO{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ConcesionariaVerif findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        ConcesionariaVerif concesionariaVerif = currentSession.get(ConcesionariaVerif.class, id);
        if (concesionariaVerif != null)
            return concesionariaVerif;
        throw new NotFoundError("No se encontro la concesionaria");
    }

    @Override
    @Transactional
    public void save(ConcesionariaVerif concesionariaVerif) {
        Session s = entityManager.unwrap(Session.class);
        if (concesionariaVerif.getIdConcesionariaVerif() == 0) {
            s.persist(concesionariaVerif); // INSERT
        } else {
            s.merge(concesionariaVerif);   // UPDATE
        }
    }

    @Override
    @Transactional
    public void delete(ConcesionariaVerif concesionariaVerif) {
        Session s = entityManager.unwrap(Session.class);
        ConcesionariaVerif managed = s.contains(concesionariaVerif) ? concesionariaVerif : s.merge(concesionariaVerif);
        s.remove(managed);
    }

    @Override
    @Transactional
    public ConcesionariaVerif findByUsuarioId(long usuarioId) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<ConcesionariaVerif> query = currentSession.createQuery(
                "FROM ConcesionariaVerif WHERE usuario.idUsuario = :usuarioId",
                ConcesionariaVerif.class
        );
        query.setParameter("usuarioId", usuarioId);
        List<ConcesionariaVerif> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

}
