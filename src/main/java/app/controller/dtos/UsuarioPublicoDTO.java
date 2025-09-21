package app.controller.dtos;

import app.model.entity.Usuarios;

public class UsuarioPublicoDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;

    public UsuarioPublicoDTO(Usuarios u) {
        super();
        this.idUsuario = u.getIdUsuario();
        this.nombre = u.getNombre();
        this.apellido = u.getApellido();
    }

    public UsuarioPublicoDTO() {
        super();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

}
