package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.DocVehiculo;
import app.model.entity.Publicacion;
import app.model.entity.Vehiculos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class PublicacionDAOImpl implements IPublicacionDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Publicacion findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        Publicacion publicacion = currentSession.get(Publicacion.class, id);
        if (publicacion != null)
            return publicacion;
        throw new NotFoundError("No se encontro la publicación");
    }

    @Override
    @Transactional
    public List<Publicacion> findAll() {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Publicacion> getQuery = currentSession.createQuery("FROM Publicacion", Publicacion.class);
        List<Publicacion> list = getQuery.getResultList();
        if(list.isEmpty())
            throw new Error("No hay publicaciones cargadas");
        return list;
    }


    @Override
    @Transactional
    public void save(Publicacion publicacion) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(publicacion);
    }

    @Override
    @Transactional
    public void delete(Publicacion publicacion) {
        Session s = entityManager.unwrap(Session.class);
        Publicacion managed = s.contains(publicacion) ? publicacion : s.merge(publicacion);
        s.remove(managed);
    }

    @Override
    @Transactional
    public Publicacion findByVehiculoId(long vehiculoId) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Publicacion> query = currentSession.createQuery(
                "FROM Publicacion WHERE vehiculo.idVehiculo = :vehiculoId",
                Publicacion.class
        );
        query.setParameter("vehiculoId", vehiculoId);
        List<Publicacion> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

//    @Override
//    @Transactional
//    public int updateEstado(long idPublicacion, Publicacion.EstadoPublicacion nuevoEstado) {
//        Session s = entityManager.unwrap(Session.class);
//        Query<?> q = s.createQuery(
//                "UPDATE Publicacion p SET p.estadoPublicacion = :estado WHERE p.idPublicacion = :id"
//        );
//        q.setParameter("estado", nuevoEstado);
//        q.setParameter("id", idPublicacion);
//        return q.executeUpdate(); // devuelve filas afectadas (0 o 1)
//    }

}
