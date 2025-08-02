package app.model.dao;

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
        throw new Error("No se encontro el documento");
    }

    @Override
    @Transactional
    public void save(DocVehiculo docVehiculo) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(docVehiculo);
    }

}
