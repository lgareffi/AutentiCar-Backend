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
    public List<Usuarios> findByNombreApellido(String search) {
        Session currentSession = entityManager.unwrap(Session.class);
        String queryStr = "FROM Usuarios WHERE LOWER(nombre) LIKE :search OR LOWER(apellido) LIKE :search";
        Query<Usuarios> query = currentSession.createQuery(queryStr, Usuarios.class);
        query.setParameter("search", "%" + search.toLowerCase() + "%");
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    @Transactional()
    public List<Usuarios> findTalleresBySearch(String search) {
        Session currentSession = entityManager.unwrap(Session.class);

        String queryStr = """
        FROM Usuarios 
        WHERE rol = 'TALLER'
        AND (
            LOWER(nombre) LIKE :search 
            OR LOWER(apellido) LIKE :search 
            OR LOWER(mail) LIKE :search 
            OR LOWER(telefonoCelular) LIKE :search
        )
    """;

        Query<Usuarios> query = currentSession.createQuery(queryStr, Usuarios.class);
        query.setParameter("search", "%" + search.toLowerCase() + "%");
        query.setMaxResults(5);

        return query.getResultList();
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

    @Override
    @Transactional
    public void agregarTallerAsignado(Long usuarioId, Long tallerId) {
        Session currentSession = entityManager.unwrap(Session.class);

        Usuarios usuario = currentSession.get(Usuarios.class, usuarioId);
        if (usuario == null) {
            throw new NotFoundError("No se encontró el usuario con ID " + usuarioId);
        }
        if (usuario.getTalleresAsignados().contains(tallerId)) {
            throw new IllegalStateException("El taller ya está asignado a este usuario.");
        }

        usuario.getTalleresAsignados().add(tallerId);
        currentSession.merge(usuario);
    }

    @Override
    @Transactional
    public void eliminarTallerAsignado(Long usuarioId, Long tallerId) {
        Session currentSession = entityManager.unwrap(Session.class);

        Usuarios usuario = currentSession.get(Usuarios.class, usuarioId);
        if (usuario == null) {
            throw new NotFoundError("No se encontró el usuario con ID " + usuarioId);
        }
        if (!usuario.getTalleresAsignados().contains(tallerId)) {
            throw new IllegalStateException("El taller no está asignado a este usuario.");
        }

        usuario.getTalleresAsignados().remove(tallerId);
        currentSession.merge(usuario);
    }

    @Override
    @Transactional()
    public List<Usuarios> findTalleresAsignadosDeUsuario(Long usuarioId) {
        Session currentSession = entityManager.unwrap(Session.class);

        String hql = """
        FROM Usuarios v
        WHERE v.rol = 'TALLER'
        AND v.idUsuario IN (
            SELECT t
            FROM Usuarios u JOIN u.talleresAsignados t
            WHERE u.idUsuario = :usuarioId
        )
    """;

        Query<Usuarios> query = currentSession.createQuery(hql, Usuarios.class);
        query.setParameter("usuarioId", usuarioId);

        return query.getResultList();
    }
    
}
