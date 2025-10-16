package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.DocVehiculo;
import app.model.entity.Publicacion;
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
    public List<Publicacion> findActivasByMarca(String marca) {
        var q = s().createQuery(
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa AND lower(v.marca) = :marca " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        q.setParameter("marca", marca.toLowerCase());
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByMarcaAndModelo(String marca, String modelo) {
        var q = s().createQuery(
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa " +
                        "AND lower(v.marca) = :marca AND lower(v.modelo) = :modelo " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        q.setParameter("marca", marca.toLowerCase());
        q.setParameter("modelo", modelo.toLowerCase());
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByColor(String color) {
        var q = s().createQuery(
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa AND lower(v.color) = :color " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        q.setParameter("color", color.toLowerCase());
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByAnio(int anio) {
        var q = s().createQuery(
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa AND v.anio = :anio " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        q.setParameter("anio", anio);
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByMarcaModeloColor(String marca, String modelo, String color) {
        var q = s().createQuery(
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa " +
                        "AND lower(v.marca) = :marca AND lower(v.modelo) = :modelo AND lower(v.color) = :color " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        q.setParameter("marca", marca.toLowerCase());
        q.setParameter("modelo", modelo.toLowerCase());
        q.setParameter("color", color.toLowerCase());
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> searchActivasTextoLibre(String queryLibre) {
        List<String> tokens = normalizarTokens(queryLibre);
        StringBuilder sb = new StringBuilder(
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa "
        );
        for (int i = 0; i < tokens.size(); i++) {
            sb.append("AND (")
                    .append("lower(v.marca) LIKE :t").append(i).append(" OR ")
                    .append("lower(v.modelo) LIKE :t").append(i).append(" OR ")
                    .append("lower(v.color) LIKE :t").append(i).append(" OR ")
                    .append("lower(p.titulo) LIKE :t").append(i).append(" OR ")
                    .append("lower(p.descripcion) LIKE :t").append(i)
                    .append(") ");
        }
        sb.append("ORDER BY p.fechaPublicacion DESC");

        var q = s().createQuery(sb.toString(), Publicacion.class);
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        for (int i = 0; i < tokens.size(); i++) q.setParameter("t"+i, "%"+tokens.get(i)+"%");
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
    public List<Publicacion> findActivasByPrecioEnArs(Integer minArs, Integer maxArs, java.math.BigDecimal tasaUsdArs) {
        var s = entityManager.unwrap(org.hibernate.Session.class);

        // Limites (si son null, uso valores extremos)
        long min = (minArs != null) ? minArs : 0L;
        long max = (maxArs != null) ? maxArs : Long.MAX_VALUE;

        // HQL: PESOS directo; DOLARES * tasa
        var q = s.createQuery(
                "SELECT p FROM Publicacion p " +
                        "WHERE p.estadoPublicacion = app.model.entity.Publicacion.EstadoPublicacion.ACTIVA " +
                        "AND ( " +
                        "  (p.moneda = app.model.entity.Publicacion.Moneda.PESOS   AND p.precio BETWEEN :min AND :max) " +
                        "  OR " +
                        "  (p.moneda = app.model.entity.Publicacion.Moneda.DOLARES AND (p.precio * :tasa) BETWEEN :min AND :max) " +
                        ") " +
                        "ORDER BY p.fechaPublicacion DESC",
                Publicacion.class
        );
        q.setParameter("min", min);
        q.setParameter("max", max);
        q.setParameter("tasa", tasaUsdArs); // BigDecimal ok en HQL para multiplicar

        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByKilometrajeBetween(Integer minKm, Integer maxKm) {
        String base =
                "SELECT p FROM Publicacion p JOIN p.vehiculo v " +
                        "WHERE p.estadoPublicacion = :activa ";

        if (minKm != null) base += "AND v.kilometraje >= :minKm ";
        if (maxKm != null) base += "AND v.kilometraje <= :maxKm ";

        base += "ORDER BY p.fechaPublicacion DESC";

        var q = s().createQuery(base, Publicacion.class);
        q.setParameter("activa", Publicacion.EstadoPublicacion.ACTIVA);
        if (minKm != null) q.setParameter("minKm", minKm);
        if (maxKm != null) q.setParameter("maxKm", maxKm);

        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Publicacion> findActivasByFiltro(
            String marca, String color, Integer anio,
            Integer minPrecioArs, Integer maxPrecioArs,
            Integer minKm, Integer maxKm,
            String queryLibre
    ) {
        var s = s(); // Session
        var sb = new StringBuilder();
        sb.append("""
       SELECT p FROM Publicacion p
       JOIN p.vehiculo v
       WHERE p.estadoPublicacion = app.model.entity.Publicacion.EstadoPublicacion.ACTIVA
    """);

        // Precio normalizado a ARS: CASE (si DOLARES => precio*tasa)
        if (minPrecioArs != null) {
            sb.append("""
          AND (
            CASE WHEN p.moneda = app.model.entity.Publicacion.Moneda.DOLARES
                 THEN (p.precio * :tasa)
                 ELSE p.precio
            END
          ) >= :minArs
        """);
        }
        if (maxPrecioArs != null) {
            sb.append("""
          AND (
            CASE WHEN p.moneda = app.model.entity.Publicacion.Moneda.DOLARES
                 THEN (p.precio * :tasa)
                 ELSE p.precio
            END
          ) <= :maxArs
        """);
        }

        if (marca != null) sb.append(" AND LOWER(v.marca) = :marca");
        if (color != null) sb.append(" AND LOWER(v.color) = :color");
        if (anio  != null) sb.append(" AND v.anio = :anio");
        if (minKm != null) sb.append(" AND v.kilometraje >= :minKm");
        if (maxKm != null) sb.append(" AND v.kilometraje <= :maxKm");

        if (queryLibre != null) {
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

        if (minPrecioArs != null) q.setParameter("minArs", minPrecioArs);
        if (maxPrecioArs != null) q.setParameter("maxArs", maxPrecioArs);
        if (minPrecioArs != null || maxPrecioArs != null) {
            q.setParameter("tasa", tasaUsdArs);
        }

        if (marca != null) q.setParameter("marca", marca.toLowerCase());
        if (color != null) q.setParameter("color", color.toLowerCase());
        if (anio  != null) q.setParameter("anio", anio);
        if (minKm != null) q.setParameter("minKm", minKm);
        if (maxKm != null) q.setParameter("maxKm", maxKm);
        if (queryLibre != null) q.setParameter("ql", "%" + queryLibre.toLowerCase() + "%");

        return q.getResultList();
    }

}
