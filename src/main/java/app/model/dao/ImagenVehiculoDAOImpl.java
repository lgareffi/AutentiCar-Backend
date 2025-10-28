package app.model.dao;

import app.Errors.NotFoundError;
import app.model.entity.ImagenVehiculo;
import app.model.entity.Vehiculos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImagenVehiculoDAOImpl implements IImagenVehiculoDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ImagenVehiculo findById(long id){
        Session currentSession = entityManager.unwrap(Session.class);
        ImagenVehiculo imagenVehiculo = currentSession.get(ImagenVehiculo.class, id);
        if (imagenVehiculo != null)
            return imagenVehiculo;
        throw new NotFoundError("No se encontro la imágen");
    }

    @Override
    @Transactional
    public void save(ImagenVehiculo imagenVehiculo) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.persist(imagenVehiculo);
    }

    @Override
    @Transactional
    public ImagenVehiculo findByUrl(String urlImagen) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<ImagenVehiculo> query = currentSession.createQuery("FROM ImagenVehiculo WHERE urlImagen = :urlImagen", ImagenVehiculo.class);
        query.setParameter("urlImagen", urlImagen);
        List<ImagenVehiculo> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    @Transactional
    public long countByVehiculo(Vehiculos vehiculo) {
        Session s = entityManager.unwrap(Session.class);
        Long count = s.createQuery(
                "SELECT COUNT(i) FROM ImagenVehiculo i WHERE i.vehiculo = :vehiculo",
                Long.class
        ).setParameter("vehiculo", vehiculo).getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    @Transactional
    public List<ImagenVehiculo> findByVehiculo(Vehiculos vehiculo) {
        Session s = entityManager.unwrap(Session.class);
        return s.createQuery(
                "FROM ImagenVehiculo i WHERE i.vehiculo = :vehiculo ORDER BY i.idImagen DESC",
                ImagenVehiculo.class
        ).setParameter("vehiculo", vehiculo).getResultList();
    }

    @Override
    @Transactional
    public void delete(ImagenVehiculo imagenVehiculo) {
        Session s = entityManager.unwrap(Session.class);
        ImagenVehiculo managed = s.contains(imagenVehiculo) ? imagenVehiculo : s.merge(imagenVehiculo);
        s.remove(managed);
    }

}
