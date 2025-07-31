package app.model.dao;

import app.model.entity.DocVehiculo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class DocVehiculoDAOImpl implements IDocVehiculoDAO {
    @PersistenceContext
    private EntityManager entityManager;

    //    @Override
//    @Transactional
}
