package app.controller.dtos;

import app.model.entity.Usuarios;

public class UsuarioPublicoDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String fotoPerfilUrl;

    public UsuarioPublicoDTO(Usuarios u) {
        super();
        this.idUsuario = u.getIdUsuario();
        this.nombre = u.getNombre();
        this.apellido = u.getApellido();
        this.email = u.getMail();
        this.telefono = u.getTelefonoCelular();
        this.fotoPerfilUrl = u.getProfilePicUrl();
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

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
}
