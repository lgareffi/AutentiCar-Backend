package app.model.entity;

import app.service.CapitalizeFirstConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Usuarios")
public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long idUsuario;

    @Column(nullable = false,length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String nombre;

    @Column(nullable = true,length = 150)
    @Convert(converter = CapitalizeFirstConverter.class)
    private String apellido;

    @Column(nullable = false)
    private int dni;

    @Column(nullable = false, length = 100)
    private String mail;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String telefonoCelular;

    @Column(nullable = false, length = 40)
    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private boolean esConcesionariaTaller;

    @Column(nullable = false)
    private boolean quiereOferta = false;

    @Column(nullable = true, length = 500)
    private String dniFrenteUrl;

    @Column(nullable = true, length = 500)
    private String dniDorsoUrl;

    @Column(nullable = true, length = 500)
    private String profilePicUrl;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Rol {
        PARTICULAR, TALLER, CONCESIONARIO, ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelUsuario nivelUsuario = NivelUsuario.REGISTRADO;

    public enum NivelUsuario {
        REGISTRADO, RECHAZADO, VALIDADO, PENDIENTE
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Vehiculos> vehiculos = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    List<EventoVehicular> eventoVehicular = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "usuarioComprador", cascade = CascadeType.ALL)
    List<Ventas> comprasRealizadas = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "usuarioVendedor", cascade = CascadeType.ALL)
    List<Ventas> ventasRealizadas = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    List<Publicacion> publicaciones = new java.util.ArrayList<>();


    public Usuarios() {
        super();
    }

    public Usuarios(String nombre, long idUsuario, int dni, String apellido, String mail, String password, String telefonoCelular,
                    LocalDate fechaRegistro, boolean esConcesionariaTaller, boolean quiereOferta, String dniFrenteUrl,
                    String dniDorsoUrl, String profilePicUrl, Rol rol, NivelUsuario nivelUsuario, List<Vehiculos> vehiculos,
                    List<EventoVehicular> eventoVehicular, List<Ventas> comprasRealizadas, List<Ventas> ventasRealizadas,
                    List<Publicacion> publicaciones) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.dni = dni;
        this.apellido = apellido;
        this.mail = mail;
        this.password = password;
        this.telefonoCelular = telefonoCelular;
        this.fechaRegistro = fechaRegistro;
        this.esConcesionariaTaller = esConcesionariaTaller;
        this.quiereOferta = quiereOferta;
        this.dniFrenteUrl = dniFrenteUrl;
        this.dniDorsoUrl = dniDorsoUrl;
        this.profilePicUrl = profilePicUrl;
        this.rol = rol;
        this.nivelUsuario = nivelUsuario;
        this.vehiculos = vehiculos;
        this.eventoVehicular = eventoVehicular;
        this.comprasRealizadas = comprasRealizadas;
        this.ventasRealizadas = ventasRealizadas;
        this.publicaciones = publicaciones;
    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni=" + dni +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", telefonoCelular='" + telefonoCelular + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", esConcesionariaTaller=" + esConcesionariaTaller +
                ", quiereOferta=" + quiereOferta +
                ", dniFrenteUrl='" + dniFrenteUrl + '\'' +
                ", dniDorsoUrl='" + dniDorsoUrl + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", rol=" + rol +
                ", nivelUsuario=" + nivelUsuario +
                ", vehiculos=" + vehiculos +
                ", eventoVehicular=" + eventoVehicular +
                ", comprasRealizadas=" + comprasRealizadas +
                ", ventasRealizadas=" + ventasRealizadas +
                ", publicaciones=" + publicaciones +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Vehiculos> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculos> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public List<EventoVehicular> getEventoVehicular() {
        return eventoVehicular;
    }

    public void setEventoVehicular(List<EventoVehicular> eventoVehicular) {
        this.eventoVehicular = eventoVehicular;
    }

    public List<Ventas> getComprasRealizadas() {
        return comprasRealizadas;
    }

    public void setComprasRealizadas(List<Ventas> comprasRealizadas) {
        this.comprasRealizadas = comprasRealizadas;
    }

    public List<Ventas> getVentasRealizadas() {
        return ventasRealizadas;
    }

    public void setVentasRealizadas(List<Ventas> ventasRealizadas) {
        this.ventasRealizadas = ventasRealizadas;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public boolean isEsConcesionariaTaller() {
        return esConcesionariaTaller;
    }

    public void setEsConcesionariaTaller(boolean esConcesionariaTaller) {
        this.esConcesionariaTaller = esConcesionariaTaller;
    }

    public String getTelefonoCelular() {
        return telefonoCelular;
    }

    public void setTelefonoCelular(String telefonoCelular) {
        this.telefonoCelular = telefonoCelular;
    }

    public String getDniFrenteUrl() {
        return dniFrenteUrl;
    }

    public void setDniFrenteUrl(String dniFrenteUrl) {
        this.dniFrenteUrl = dniFrenteUrl;
    }

    public String getDniDorsoUrl() {
        return dniDorsoUrl;
    }

    public void setDniDorsoUrl(String dniDorsoUrl) {
        this.dniDorsoUrl = dniDorsoUrl;
    }

    public NivelUsuario getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(NivelUsuario nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public boolean isQuiereOferta() {
        return quiereOferta;
    }

    public void setQuiereOferta(boolean quiereOferta) {
        this.quiereOferta = quiereOferta;
    }
}
