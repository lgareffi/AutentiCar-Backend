package app.model.dao;


import app.Errors.NotFoundError;
import app.model.entity.Favorito;
import app.model.entity.Publicacion;
import app.model.entity.Usuarios;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class FavoritoDAOImpl implements IFavoritoDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private Session s() { return entityManager.unwrap(Session.class); }

    @Override
    @Transactional
    public void save(Favorito favorito) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(favorito);
    }

    @Override
    @Transactional
    public void delete(Favorito favorito) {
        Session session = s();
        Favorito managedFav = session.contains(favorito) ? favorito : session.merge(favorito);
        session.remove(managedFav);
    }

    @Override
    @Transactional
    public Favorito findByUsuarioAndPublicacion(long usuarioId, long publicacionId) {
        var q = s().createQuery(
                "FROM Favorito f " +
                        "WHERE f.usuario.idUsuario = :u AND f.publicacion.idPublicacion = :p",
                Favorito.class
        );
        q.setParameter("u", usuarioId);
        q.setParameter("p", publicacionId);
        List<Favorito> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional
    public List<Publicacion> findAllPublicacionesByUsuario(long usuarioId) {
        var q = s().createQuery(
                "SELECT p FROM Favorito f " +
                        "JOIN f.publicacion p " +
                        "JOIN p.vehiculo v " +
                        "WHERE f.usuario.idUsuario = :u AND p.estadoPublicacion = app.model.entity.Publicacion.EstadoPublicacion.ACTIVA " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("u", usuarioId);
        return q.getResultList();
    }

}
