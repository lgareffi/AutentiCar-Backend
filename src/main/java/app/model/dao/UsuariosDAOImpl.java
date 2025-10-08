package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
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

    @Override
    @Transactional
    public Usuarios findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        Usuarios usuario = currentSession.get(Usuarios.class, id);
        return usuario;
    }

    @Override
    @Transactional
    public List<Usuarios> findAll() {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Usuarios> getQuery = currentSession.createQuery("FROM Usuarios", Usuarios.class);
        List<Usuarios> list = getQuery.getResultList();
        if(list.isEmpty())
            throw new Error("No hay Usuarios cargados");
        return list;
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
        Session s = entityManager.unwrap(Session.class);
        if (usuario.getIdUsuario() == 0) {
            s.persist(usuario);
        } else {
            s.merge(usuario);
        }
    }

    @Override
    @Transactional
    public void delete(Usuarios usuario) {
        Session s = entityManager.unwrap(Session.class);
        Usuarios managed = s.contains(usuario) ? usuario : s.merge(usuario);
        s.remove(managed);
    }

    @Override
    @Transactional
    public long countPublicacionesByUsuarioId(long usuarioId) {
        Session s = entityManager.unwrap(org.hibernate.Session.class);
        return s.createQuery(
            "select count(p) from Publicacion p where p.usuario.idUsuario = :uid",
            Long.class
        )
            .setParameter("uid", usuarioId)
            .getSingleResult();
    }
}
