package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.EventoVehicular;
import app.model.entity.Vehiculos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class EventoVehicularDAOImpl implements IEventoVehicularDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EventoVehicular findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        EventoVehicular eventoVehicular = currentSession.get(EventoVehicular.class, id);
        if (eventoVehicular != null)
            return eventoVehicular;
        throw new NotFoundError("No se encontro el evento");
    }

    @Override
    @Transactional
    public void save(EventoVehicular eventoVehicular) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(eventoVehicular);
    }

    @Override
    @Transactional
    public void delete(EventoVehicular evento) {
        Session s = entityManager.unwrap(Session.class);
        EventoVehicular managed = s.contains(evento) ? evento : s.merge(evento);
        s.remove(managed);
    }
}
