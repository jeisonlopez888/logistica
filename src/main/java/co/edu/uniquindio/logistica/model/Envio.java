package co.edu.uniquindio.logistica.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Envio implements Serializable {

    public enum EstadoEnvio {
        SOLICITADO, PENDIENTE, CONFIRMADO, ASIGNADO, EN_CAMINO, ENTREGADO, CANCELADO, INCIDENCIA
    }

    private Long id;
    private Direccion origen;
    private Direccion destino;
    private double peso;
    private double volumen; // ✅ Mantiene volumen del paquete (cm³ o m³)
    private boolean prioridad;
    private boolean seguro;
    private Usuario usuario;
    private EstadoEnvio estado;
    private double costoEstimado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaEntregaEstimada; // ✅ Añadido para compatibilidad con EnvioService
    private Repartidor repartidor;

    public Envio() {
        this.estado = EstadoEnvio.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    // ========================
    // ✅ Getters / Setters
    // ========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Direccion getOrigen() { return origen; }
    public void setOrigen(Direccion origen) { this.origen = origen; }

    public Direccion getDestino() { return destino; }
    public void setDestino(Direccion destino) { this.destino = destino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public boolean isPrioridad() { return prioridad; }
    public void setPrioridad(boolean prioridad) { this.prioridad = prioridad; }

    public boolean isSeguro() { return seguro; }
    public void setSeguro(boolean seguro) { this.seguro = seguro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
        if (estado == EstadoEnvio.CONFIRMADO && fechaConfirmacion == null)
            fechaConfirmacion = LocalDateTime.now();
        if (estado == EstadoEnvio.ENTREGADO && fechaEntrega == null)
            fechaEntrega = LocalDateTime.now();
    }

    public double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(double costoEstimado) { this.costoEstimado = costoEstimado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    // ✅ NUEVO: soporta fecha estimada de entrega
    public LocalDateTime getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(LocalDateTime fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }

    public Repartidor getRepartidor() { return repartidor; }
    public void setRepartidor(Repartidor repartidor) { this.repartidor = repartidor; }

    // ========================
    // ✅ Helpers para interfaz
    // ========================
    public String getOrigenDireccion() { return origen == null ? "" : origen.getCalle(); }
    public String getDestinoDireccion() { return destino == null ? "" : destino.getCalle(); }

    public String getFechaCreacionStr() {
        return fechaCreacion == null ? "" : fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaConfirmacionStr() {
        return fechaConfirmacion == null ? "" : fechaConfirmacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaEntregaStr() {
        return fechaEntrega == null ? "" : fechaEntrega.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

  
}
