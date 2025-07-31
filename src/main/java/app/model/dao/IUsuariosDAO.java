package app.model.dao;

import app.model.entity.Usuarios;


public interface IUsuariosDAO {
    public Usuarios findById(long id);

    public Usuarios findByMail(String mail);

    public Usuarios findByDni(int dni);

    public void save(Usuarios usuario);

}
