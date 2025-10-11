package co.edu.uniquindio.logistica.model;

import java.time.LocalDateTime;

public class Pago {
    private Long id;
    private Envio envio;
    private double montoPagado;
    private MetodoPago metodo;
    private LocalDateTime fecha;
    private boolean confirmado;

    public Pago(Long id, Envio envio, double montoPagado, MetodoPago metodo) {
        this.id = id;
        this.envio = envio;
        this.montoPagado = montoPagado;
        this.metodo = metodo;
        this.fecha = LocalDateTime.now();
        this.confirmado = false; // ahora NO se confirma automáticamente
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }

    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
}
