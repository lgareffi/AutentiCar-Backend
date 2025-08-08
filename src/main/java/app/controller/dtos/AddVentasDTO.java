package app.controller.dtos;

import java.time.LocalDate;

public class AddVentasDTO {
    public int precio;
    public long compradorId;
    public long vendedorId;
    public long publicacionId;

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public long getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(long compradorId) {
        this.compradorId = compradorId;
    }

    public long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public long getPublicacionId() {
        return publicacionId;
    }

    public void setPublicacionId(long publicacionId) {
        this.publicacionId = publicacionId;
    }
}
