package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.DocVehiculo;
import app.model.entity.Publicacion;
import app.model.entity.Usuarios;
import app.model.entity.Vehiculos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class PublicacionDAOImpl implements IPublicacionDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.precio.usd_ars:1400}")
    private java.math.BigDecimal tasaUsdArs;

    private Session s() { return entityManager.unwrap(Session.class); }

    @Override
    @Transactional
    public Publicacion findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        Publicacion publicacion = currentSession.get(Publicacion.class, id);
        if (publicacion != null)
            return publicacion;
        throw new NotFoundError("No se encontro la publicación");
    }

    @Override
    @Transactional
    public List<Publicacion> findAll() {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Publicacion> getQuery = currentSession.createQuery("FROM Publicacion", Publicacion.class);
        List<Publicacion> list = getQuery.getResultList();
        if(list.isEmpty())
            throw new Error("No hay publicaciones cargadas");
        return list;
    }


    @Override
    @Transactional
    public void save(Publicacion publicacion) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(publicacion);
    }

    @Override
    @Transactional
    public void delete(Publicacion publicacion) {
        Session s = entityManager.unwrap(Session.class);
        Publicacion managed = s.contains(publicacion) ? publicacion : s.merge(publicacion);
        s.remove(managed);
    }

    @Override
    @Transactional
    public Publicacion findByVehiculoId(long vehiculoId) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Publicacion> query = currentSession.createQuery(
                "FROM Publicacion WHERE vehiculo.idVehiculo = :vehiculoId",
                Publicacion.class
        );
        query.setParameter("vehiculoId", vehiculoId);
        List<Publicacion> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    @Transactional()
    public List<Publicacion> findByTallerAsignado(Long tallerId) {
        Session currentSession = entityManager.unwrap(Session.class);

        String hql = """
        SELECT p FROM Publicacion p
        WHERE p.usuario.idUsuario IN (
            SELECT u.idUsuario
            FROM Usuarios u JOIN u.talleresAsignados t
            WHERE t = :tallerId
        )
    """;

        Query<Publicacion> query = currentSession.createQuery(hql, Publicacion.class);
        query.setParameter("tallerId", tallerId);

        return query.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivas() {
        var session = entityManager.unwrap(org.hibernate.Session.class);
        var q = session.createQuery(
                "FROM Publicacion p " +
                        "WHERE p.estadoPublicacion = :estado " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("estado", Publicacion.EstadoPublicacion.ACTIVA);
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<String> findDistinctMarcasActivas() {
        var q = s().createQuery(
                "SELECT DISTINCT v.marca " +
                        "FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa " +
                        "ORDER BY v.marca",
                String.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<String> findDistinctModelosActivosByMarca(String marca) {
        var q = s().createQuery(
                "SELECT DISTINCT v.modelo " +
                        "FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa " +
                        "AND lower(v.marca) = :marca " +
                        "ORDER BY v.modelo",
                String.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        q.setParameter("marca", marca.toLowerCase());
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<String> findDistinctColoresActivos() {
        var q = s().createQuery(
                "SELECT DISTINCT v.color " +
                        "FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa " +
                        "ORDER BY v.color",
                String.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Integer> findDistinctAniosActivos() {
        var q = s().createQuery(
                "SELECT DISTINCT v.anio " +
                        "FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = app.model.entity.Publicacion.EstadoPublicacion.ACTIVA " +
                        "ORDER BY v.anio DESC",
                Integer.class
        );
        return q.getResultList();
    }

    private List<String> normalizarTokens(String q) {
        if (q == null) return List.of();
        return Arrays.stream(q.toLowerCase().trim().split("\\s+"))
                .filter(t -> !t.isBlank()).toList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByFiltro(
            List<String> marcas,
            List<String> colores,
            List<Integer> anios,
            List<Integer> minPrecioArs,
            List<Integer> maxPrecioArs,
            List<Integer> minKm,
            List<Integer> maxKm,
            List<Usuarios.Rol> roles,
            String queryLibre,
            Long usuarioId,
            Long tallerId
    ) {
        var s = s();

        StringBuilder sb = new StringBuilder("""
            SELECT p FROM Publicacion p
            JOIN p.vehiculo v
            JOIN p.usuario u
            WHERE p.estadoPublicacion = app.model.entity.Publicacion.EstadoPublicacion.ACTIVA
        """);

        if (marcas != null && !marcas.isEmpty()) sb.append(" AND LOWER(v.marca) IN (:marcas) ");
        if (colores != null && !colores.isEmpty()) sb.append(" AND LOWER(v.color) IN (:colores) ");
        if (anios != null && !anios.isEmpty()) sb.append(" AND v.anio IN (:anios) ");
        if (roles != null && !roles.isEmpty()) sb.append(" AND u.rol IN (:roles) ");

        if (usuarioId != null) {
            sb.append(" AND u.id = :usuarioId ");
        }
        if (tallerId != null) {
            sb.append(" AND :tallerId MEMBER OF u.talleresAsignados ");
        }

        String precioArsExpr = """
        (CASE WHEN p.moneda = app.model.entity.Publicacion.Moneda.DOLARES
              THEN (p.precio * :tasa)
              ELSE p.precio END)
    """;

        int priceClauses = 0;
        if ((minPrecioArs != null && !minPrecioArs.isEmpty()) ||
                (maxPrecioArs != null && !maxPrecioArs.isEmpty())) {

            sb.append(" AND (");
            int n = Math.max(
                    (minPrecioArs != null ? minPrecioArs.size() : 0),
                    (maxPrecioArs != null ? maxPrecioArs.size() : 0)
            );
            for (int i = 0; i < n; i++) {
                Integer min = (minPrecioArs != null && i < minPrecioArs.size()) ? minPrecioArs.get(i) : null;
                Integer max = (maxPrecioArs != null && i < maxPrecioArs.size()) ? maxPrecioArs.get(i) : null;
                if (min == null && max == null) continue;

                if (priceClauses++ > 0) sb.append(" OR ");
                if (min != null && max != null) {
                    sb.append(precioArsExpr).append(" BETWEEN :pMin").append(i).append(" AND :pMax").append(i);
                } else if (min != null) {
                    sb.append(precioArsExpr).append(" >= :pMin").append(i);
                } else {
                    sb.append(precioArsExpr).append(" <= :pMax").append(i);
                }
            }

            if (priceClauses == 0) {
                int idx = sb.lastIndexOf(" AND (");
                if (idx >= 0) sb.delete(idx, sb.length());
            } else {
                sb.append(") ");
            }
        }

        int kmClauses = 0;
        if ((minKm != null && !minKm.isEmpty()) || (maxKm != null && !maxKm.isEmpty())) {
            sb.append(" AND (");
            int n = Math.max(
                    (minKm != null ? minKm.size() : 0),
                    (maxKm != null ? maxKm.size() : 0)
            );
            for (int i = 0; i < n; i++) {
                Integer min = (minKm != null && i < minKm.size()) ? minKm.get(i) : null;
                Integer max = (maxKm != null && i < maxKm.size()) ? maxKm.get(i) : null;
                if (min == null && max == null) continue;

                if (kmClauses++ > 0) sb.append(" OR ");
                if (min != null && max != null) {
                    sb.append(" v.kilometraje BETWEEN :kMin").append(i).append(" AND :kMax").append(i);
                } else if (min != null) {
                    sb.append(" v.kilometraje >= :kMin").append(i);
                } else {
                    sb.append(" v.kilometraje <= :kMax").append(i);
                }
            }
            if (kmClauses == 0) {
                int idx = sb.lastIndexOf(" AND (");
                if (idx >= 0) sb.delete(idx, sb.length());
            } else {
                sb.append(") ");
            }
        }

        if (queryLibre != null && !queryLibre.isBlank()) {
            sb.append("""
           AND (
                LOWER(v.marca)  LIKE :ql
             OR LOWER(v.modelo) LIKE :ql
             OR LOWER(v.color)  LIKE :ql
             OR LOWER(p.titulo) LIKE :ql
             OR LOWER(p.descripcion) LIKE :ql
           )
        """);
        }

        sb.append(" ORDER BY p.fechaPublicacion DESC");

        var q = s.createQuery(sb.toString(), Publicacion.class);

        if (marcas != null && !marcas.isEmpty())
            q.setParameterList("marcas", marcas.stream().map(String::toLowerCase).toList());
        if (colores != null && !colores.isEmpty())
            q.setParameterList("colores", colores.stream().map(String::toLowerCase).toList());
        if (anios != null && !anios.isEmpty())
            q.setParameterList("anios", anios);
        if (roles != null && !roles.isEmpty())
            q.setParameterList("roles", roles);

        if (usuarioId != null)
            q.setParameter("usuarioId", usuarioId);
        if (tallerId != null)
            q.setParameter("tallerId", tallerId);

        if (priceClauses > 0) q.setParameter("tasa", tasaUsdArs);

        if (priceClauses > 0) {
            int n = Math.max(
                    (minPrecioArs != null ? minPrecioArs.size() : 0),
                    (maxPrecioArs != null ? maxPrecioArs.size() : 0)
            );
            for (int i = 0; i < n; i++) {
                Integer min = (minPrecioArs != null && i < minPrecioArs.size()) ? minPrecioArs.get(i) : null;
                Integer max = (maxPrecioArs != null && i < maxPrecioArs.size()) ? maxPrecioArs.get(i) : null;
                if (min != null) q.setParameter("pMin" + i, min);
                if (max != null) q.setParameter("pMax" + i, max);
            }
        }

        if (kmClauses > 0) {
            int n = Math.max(
                    (minKm != null ? minKm.size() : 0),
                    (maxKm != null ? maxKm.size() : 0)
            );
            for (int i = 0; i < n; i++) {
                Integer min = (minKm != null && i < minKm.size()) ? minKm.get(i) : null;
                Integer max = (maxKm != null && i < maxKm.size()) ? maxKm.get(i) : null;
                if (min != null) q.setParameter("kMin" + i, min);
                if (max != null) q.setParameter("kMax" + i, max);
            }
        }

        if (queryLibre != null && !queryLibre.isBlank())
            q.setParameter("ql", "%" + queryLibre.toLowerCase() + "%");

        return q.getResultList();
    }

}
