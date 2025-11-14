package co.edu.uniquindio.logistica.model.DTO;

import co.edu.uniquindio.logistica.model.MetodoPago;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagoDTO {
    private Long id;
    private Long idEnvio;  // Solo ID, no la entidad completa
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

    public PagoDTO() {
    }

    public PagoDTO(Long id, Long idEnvio, double montoPagado, MetodoPago metodo) {
        this.id = id;
        this.idEnvio = idEnvio;
        this.montoPagado = montoPagado;
        this.metodo = metodo;
        this.fechaPago = LocalDateTime.now();
        this.confirmado = false;
    }

    // ✅ Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdEnvio() { return idEnvio; }
    public void setIdEnvio(Long idEnvio) { this.idEnvio = idEnvio; }

    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public boolean isPrioridad() { return prioridad; }
    public void setPrioridad(boolean prioridad) { this.prioridad = prioridad; }

    public boolean isSeguro() { return seguro; }
    public void setSeguro(boolean seguro) { this.seguro = seguro; }

    public double getCostoBase() { return costoBase; }
    public void setCostoBase(double costoBase) { this.costoBase = costoBase; }

    public double getCostoFinal() { return costoFinal; }
    public void setCostoFinal(double costoFinal) { this.costoFinal = costoFinal; }

    public String getFecha() {
        return fechaPago != null ? fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    @Override
    public String toString() {
        return String.format("Pago #%d - %.2f (%s) [peso=%.2fkg, volumen=%.2fm³, prioridad=%s, seguro=%s]",
                id, montoPagado, metodo, peso, volumen, prioridad, seguro);
    }
}
