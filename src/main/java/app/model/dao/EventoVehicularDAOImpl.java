package app.model.dao;

import app.model.entity.EventoVehicular;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class EventoVehicularDAOImpl implements IEventoVehicularDAO {
    @PersistenceContext
    private EntityManager entityManager;

    //    @Override
//    @Transactional
}
