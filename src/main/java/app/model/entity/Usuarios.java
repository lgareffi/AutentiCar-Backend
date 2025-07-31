package app.model.entity;

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
    private String nombre;

    @Column(nullable = true,length = 150) // si es una concesionaria o un taller, no tienen apellido
    private String apellido;

    @Column(nullable = false)
    private int dni;

    @Column(nullable = false, length = 100)
    private String mail;

    @Column(nullable = false, length = 40)
    private String password;

    @Column(nullable = false, length = 40)
    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Rol {
        PARTICULAR, TALLER, CONCESIONARIO
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL) // 1 usuario puede tener muchos autos
    List<Vehiculos> vehiculos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL) // 1 usuario puede registrar muchos eventos
    List<EventoVehicular> eventoVehicular;

    @OneToMany(mappedBy = "usuarioComprador", cascade = CascadeType.ALL) // 1 usuario puede realizar muchas comprar
    List<Ventas> comprasRealizadas;

    @OneToMany(mappedBy = "usuarioVendedor", cascade = CascadeType.ALL) // 1 usuario puede realizar muchas ventas
    List<Ventas> ventasRealizadas;


    public Usuarios() {
        super();
    }

    public Usuarios(String nombre, long idUsuario, String apellido, int dni, String mail,
                    String password, LocalDate fechaRegistro, Rol rol,
                    List<Vehiculos> vehiculos, List<EventoVehicular> eventoVehicular,
                    List<Ventas> ventasRealizadas, List<Ventas> comprasRealizadas) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.apellido = apellido;
        this.dni = dni;
        this.mail = mail;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
        this.rol = rol;
        this.vehiculos = vehiculos;
        this.eventoVehicular = eventoVehicular;
        this.ventasRealizadas = ventasRealizadas;
        this.comprasRealizadas = comprasRealizadas;
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
                ", fechaRegistro=" + fechaRegistro +
                ", rol=" + rol +
                ", vehiculos=" + vehiculos +
                ", eventoVehicular=" + eventoVehicular +
                ", comprasRealizadas=" + comprasRealizadas +
                ", ventasRealizadas=" + ventasRealizadas +
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
}
