package app.model.dao;

import app.model.entity.Usuarios;

import java.util.List;


public interface IUsuariosDAO {
    public Usuarios findById(long id);

    public List<Usuarios> findAll();

    public Usuarios findByMail(String mail);

    public Usuarios findByDni(int dni);

    public void save(Usuarios usuario);

    public void delete(Usuarios usuario);

    public long countPublicacionesByUsuarioId(long usuarioId);

}
