package app.controller.dtos;
import app.model.entity.Usuarios;
import app.model.entity.Ventas;

import java.util.List;
import java.util.stream.Collectors;

public class UsuariosDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private List<Long> comprasRealizadas;
    private List<Long> ventasRealizadas;
    private List<Long> vehiculos;
    private List<Long> eventos;
    private List<Long> publicaciones;

    public UsuariosDTO(Usuarios usuario) {
        super();
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.comprasRealizadas = usuario.getComprasRealizadas()
                .stream()
                .map(Ventas::getIdVenta)
                .toList();
        this.ventasRealizadas = usuario.getVentasRealizadas()
                .stream()
                .map(Ventas::getIdVenta)
                .collect(Collectors.toList());
        this.vehiculos = usuario.getVehiculos()
                .stream()
                .map(v -> v.getIdVehiculo())
                .collect(Collectors.toList());
        this.eventos = usuario.getEventoVehicular()
                .stream()
                .map(e -> e.getIdEvento())
                .collect(Collectors.toList());
        this.publicaciones = usuario.getPublicaciones()
                .stream()
                .map(p -> p.getIdPublicacion())
                .collect(Collectors.toList());

    }

    public UsuariosDTO() {
        super();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public List<Long> getComprasRealizadas() {
        return comprasRealizadas;
    }

    public void setComprasRealizadas(List<Long> comprasRealizadas) {
        this.comprasRealizadas = comprasRealizadas;
    }

    public List<Long> getVentasRealizadas() {
        return ventasRealizadas;
    }

    public void setVentasRealizadas(List<Long> ventasRealizadas) {
        this.ventasRealizadas = ventasRealizadas;
    }

    public List<Long> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Long> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public List<Long> getEventos() {
        return eventos;
    }

    public void setEventos(List<Long> eventos) {
        this.eventos = eventos;
    }

    public List<Long> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Long> publicaciones) {
        this.publicaciones = publicaciones;
    }
}
