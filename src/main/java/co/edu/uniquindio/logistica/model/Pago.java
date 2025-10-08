package co.edu.uniquindio.logistica.model;

import java.time.LocalDateTime;

public class Pago {

    private Long id;
    private Envio envio;
    private double montoPagado;
    private double montoCalculado;
    private boolean completado;
    private LocalDateTime fechaPago;

    public Pago(Long id, Envio envio, double montoPagado, double montoCalculado, boolean completado) {
        this.id = id;
        this.envio = envio;
        this.montoPagado = montoPagado;
        this.montoCalculado = montoCalculado;
        this.completado = completado;
        this.fechaPago = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Envio getEnvio() { return envio; }
    public double getMontoPagado() { return montoPagado; }
    public double getMontoCalculado() { return montoCalculado; }
    public boolean isCompletado() { return completado; }
    public LocalDateTime getFechaPago() { return fechaPago; }

    public void setId(Long id) { this.id = id; }
    public void setEnvio(Envio envio) { this.envio = envio; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }
    public void setMontoCalculado(double montoCalculado) { this.montoCalculado = montoCalculado; }
    public void setCompletado(boolean completado) { this.completado = completado; }

    @Override
    public String toString() {
        return "Pago #" + id + " → $" + montoPagado +
                " / $" + montoCalculado +
                (completado ? " (Completado)" : " (Pendiente)");
    }
}
