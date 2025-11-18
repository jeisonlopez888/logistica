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
        this.metodo = metodo;
        this.fechaPago = LocalDateTime.now();
        this.confirmado = false;

        if (envio != null) {
            this.peso = envio.getPeso();
            this.volumen = envio.getVolumen();
            this.prioridad = envio.isPrioridad();
            this.seguro = envio.isSeguro();

            // El monto pagado debe ser igual al costo estimado del envío (fuente de verdad)
            // Si el envío tiene un costo estimado, usarlo; de lo contrario usar el monto pasado
            if (envio.getCostoEstimado() > 0) {
                this.montoPagado = envio.getCostoEstimado();
                this.costoBase = envio.getCostoEstimado();
                this.costoFinal = envio.getCostoEstimado();
            } else {
                // Si no hay costo estimado, usar el monto pasado y actualizar el envío
                this.montoPagado = montoPagado;
                this.costoBase = montoPagado;
                this.costoFinal = montoPagado;
                envio.setCostoEstimado(montoPagado);
            }
        } else {
            this.montoPagado = montoPagado;
            this.costoBase = montoPagado;
            this.costoFinal = montoPagado;
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
