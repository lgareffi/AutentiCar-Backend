package app.model.dao;


import app.model.entity.Vehiculos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.query.Query;

@Repository
public class VehiculosDAOImpl  implements IVehiculosDAO{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Vehiculos findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        Vehiculos vehiculo = currentSession.get(Vehiculos.class, id);
        if (vehiculo != null)
            return vehiculo;
        throw new Error("No se encontro el vehiculo");
    }

    @Override
    @Transactional
    public void save(Vehiculos vehiculo) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(vehiculo);
    }

    @Override
    @Transactional
    public List<Vehiculos> findAll() {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Vehiculos> getQuery = currentSession.createQuery("FROM Vehiculos", Vehiculos.class);
        List<Vehiculos> list = getQuery.getResultList();
        if(list.isEmpty())
            throw new Error("No hay Vehiculos cargados");
        return list;
    }

}
