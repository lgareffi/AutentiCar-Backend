package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.DocVehiculo;
import app.model.entity.Vehiculos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class DocVehiculoDAOImpl implements IDocVehiculoDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DocVehiculo findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        DocVehiculo docVehiculo = currentSession.get(DocVehiculo.class, id);
        if (docVehiculo != null)
            return docVehiculo;
        throw new NotFoundError("No se encontro el documento");
    }

    @Override
    @Transactional
    public void save(DocVehiculo docVehiculo) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(docVehiculo);
    }

    @Override
    @Transactional
    public void delete(DocVehiculo docVehiculo) {
        Session s = entityManager.unwrap(Session.class);
        DocVehiculo managed = s.contains(docVehiculo) ? docVehiculo : s.merge(docVehiculo);
        s.remove(managed);
    }

    @Override
    @Transactional
    public List<DocVehiculo> findByVehiculo(Vehiculos vehiculo) {
        Session s = entityManager.unwrap(Session.class);
        return s.createQuery(
                "FROM DocVehiculo d WHERE d.vehiculo = :vehiculo ORDER BY d.idDocVehiculo DESC",
                DocVehiculo.class
        ).setParameter("vehiculo", vehiculo).getResultList();
    }

}
