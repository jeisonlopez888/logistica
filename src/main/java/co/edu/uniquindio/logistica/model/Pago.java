package co.edu.uniquindio.logistica.model;

import java.time.LocalDateTime;

public class Pago {

    private Long id;
    private Envio envio;
    private double montoPagado;
    private MetodoPago metodo;
    private LocalDateTime fechaPago;
    private boolean confirmado;

    // 🔹 Datos derivados del envío
    private double peso;
    private double volumen;
    private boolean prioridad;
    private boolean seguro;
    private double costoBase;
    private double costoFinal;


    public Pago(Long id, Envio envio, double montoPagado, MetodoPago metodo) {
        this.id = id;
        this.envio = envio;
        this.montoPagado = montoPagado;
        this.metodo = metodo;
        this.fechaPago = LocalDateTime.now();
        this.confirmado = false;


        if (envio != null) {
            this.peso = envio.getPeso();
            this.volumen = envio.getVolumen();
            this.prioridad = envio.isPrioridad();
            this.seguro = envio.isSeguro();

            // Tarifa base
            this.costoBase = montoPagado;

            // Ajuste por prioridad o seguro
            double ajuste = 1.0;
            if (prioridad) ajuste += 0.2;   // +20% si es prioritario
            if (seguro) ajuste += 0.1;      // +10% si tiene seguro

            // Volumen afecta costo si es alto
            if (volumen > 0.5) ajuste += 0.15;

            this.costoFinal = montoPagado * ajuste;
            this.montoPagado = this.costoFinal;
        }
    }

    // ✅ Getters y Setters
    public Long getId() { return id; }
    public Envio getEnvio() { return envio; }

    public double getMontoPagado() {
        return montoPagado;
    }


    public LocalDateTime getFechaPago() { return fechaPago; }
    public double getPeso() { return peso; }
    public double getVolumen() { return volumen; }
    public boolean isPrioridad() { return prioridad; }
    public boolean isSeguro() { return seguro; }
    public double getCostoBase() { return costoBase; }
    public double getCostoFinal() { return costoFinal; }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    @Override
    public String toString() {
        return String.format("Pago #%d - %.2f (%s) [peso=%.2fkg, volumen=%.2fm³, prioridad=%s, seguro=%s]",
                id, montoPagado, metodo, peso, volumen, prioridad, seguro);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getFecha() {
        return fechaPago != null ? fechaPago.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public void setFecha(LocalDateTime fecha) {
        this.fechaPago = fecha;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}
