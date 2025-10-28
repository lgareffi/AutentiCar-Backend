package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.ConcesionarioTallerVerif;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.query.Query;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcesionariaTallerVerifDAOImpl implements IConcesionariaTallerVerifDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ConcesionarioTallerVerif findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        ConcesionarioTallerVerif concesionarioTallerVerif = currentSession.get(ConcesionarioTallerVerif.class, id);
        if (concesionarioTallerVerif != null)
            return concesionarioTallerVerif;
        throw new NotFoundError("No se encontro");
    }

    @Override
    @Transactional
    public void save(ConcesionarioTallerVerif concesionarioTallerVerif) {
        Session s = entityManager.unwrap(Session.class);
        if (concesionarioTallerVerif.getIdConcesionarioTallerVerif() == 0) {
            s.persist(concesionarioTallerVerif);
        } else {
            s.merge(concesionarioTallerVerif);
        }
    }

    @Override
    @Transactional
    public void delete(ConcesionarioTallerVerif concesionarioTallerVerif) {
        Session s = entityManager.unwrap(Session.class);
        ConcesionarioTallerVerif managed = s.contains(concesionarioTallerVerif) ? concesionarioTallerVerif : s.merge(concesionarioTallerVerif);
        s.remove(managed);
    }

    @Override
    @Transactional
    public ConcesionarioTallerVerif findByUsuarioId(long usuarioId) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<ConcesionarioTallerVerif> query = currentSession.createQuery(
                "FROM ConcesionarioTallerVerif WHERE usuario.idUsuario = :usuarioId",
                ConcesionarioTallerVerif.class
        );
        query.setParameter("usuarioId", usuarioId);
        List<ConcesionarioTallerVerif> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

}
