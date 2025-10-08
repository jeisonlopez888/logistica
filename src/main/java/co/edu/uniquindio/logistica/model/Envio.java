package co.edu.uniquindio.logistica.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Envio implements Serializable {

    public enum EstadoEnvio {
        PENDIENTE,
        EN_CAMINO,
        ENTREGADO,
        CANCELADO
    }

    private Long id;
    private Direccion origen;
    private Direccion destino;
    private double peso;
    private Usuario usuario;
    private EstadoEnvio estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega;

    public Envio() {
        this.estado = EstadoEnvio.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Direccion getOrigen() { return origen; }
    public void setOrigen(Direccion origen) { this.origen = origen; }

    public Direccion getDestino() { return destino; }
    public void setDestino(Direccion destino) { this.destino = destino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
        if (estado == EstadoEnvio.ENTREGADO && this.fechaEntrega == null) {
            this.fechaEntrega = LocalDateTime.now();
        } else if (estado != EstadoEnvio.ENTREGADO) {
            this.fechaEntrega = null;
        }
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getOrigenDireccion() {
        return origen == null ? "" : origen.getCalle();
    }

    public String getDestinoDireccion() {
        return destino == null ? "" : destino.getCalle();
    }

    public String getFechaCreacionStr() {
        return fechaCreacion == null ? "" : fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaEntregaStr() {
        return fechaEntrega == null ? "" : fechaEntrega.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "Envio #" + id +
                " [" + estado + "] " +
                " - Peso: " + peso + "kg" +
                " - Creado: " + getFechaCreacionStr() +
                (fechaEntrega != null ? " - Entregado: " + getFechaEntregaStr() : "");
    }
}
