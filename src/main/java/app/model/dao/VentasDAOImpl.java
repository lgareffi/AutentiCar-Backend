package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.Ventas;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;


@Repository
public class VentasDAOImpl implements IVentasDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Ventas findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        Ventas venta = currentSession.get(Ventas.class, id);
        if (venta != null)
            return venta;
        throw new NotFoundError("No se encontro la venta");
    }

    @Override
    @Transactional
    public void save(Ventas venta) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(venta);
    }

}
