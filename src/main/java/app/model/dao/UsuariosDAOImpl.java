package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.Usuarios;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

@Repository
public class UsuariosDAOImpl implements IUsuariosDAO {
    @PersistenceContext
    private EntityManager entityManager;

//    @Override
//    @Transactional
//    public Usuarios findById(long id){
//        Session currentSession = entityManager.unwrap(Session.class);
//        Usuarios usuario = currentSession.get(Usuarios.class, id);
//        if (usuario != null)
//            return usuario;
//        throw new NotFoundError("No se encontro al usuario");
//    }
    @Override
    @Transactional
    public Usuarios findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        Usuarios usuario = currentSession.get(Usuarios.class, id);
        return usuario;
    }

    @Override
    @Transactional
    public Usuarios findByMail(String mail) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Usuarios> query = currentSession.createQuery("FROM Usuarios WHERE mail = :mail", Usuarios.class);
        query.setParameter("mail", mail);
        List<Usuarios> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    @Transactional
    public Usuarios findByDni(int dni) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Usuarios> query = currentSession.createQuery("FROM Usuarios WHERE dni = :dni", Usuarios.class);
        query.setParameter("dni", dni);
        List<Usuarios> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }


    @Override
    @Transactional
    public void save(Usuarios usuario) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(usuario);
    }
}
