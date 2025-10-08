package co.edu.uniquindio.logistica.model;

public class Tarifa {

    private Long id;
    private String descripcion;
    private double costoBase;
    private double costoPorKilo;

    public Tarifa(Long id, String descripcion, double costoBase, double costoPorKilo) {
        this.id = id;
        this.descripcion = descripcion;
        this.costoBase = costoBase;
        this.costoPorKilo = costoPorKilo;
    }

    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public double getCostoBase() { return costoBase; }
    public double getCostoPorKilo() { return costoPorKilo; }

    public void setId(Long id) { this.id = id; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCostoBase(double costoBase) { this.costoBase = costoBase; }
    public void setCostoPorKilo(double costoPorKilo) { this.costoPorKilo = costoPorKilo; }

    /**
     * Calcula el costo total de un envío según el peso.
     */
    public double calcularCosto(double peso) {
        return costoBase + (peso * costoPorKilo);
    }

    @Override
    public String toString() {
        return descripcion + " (Base $" + costoBase + ", +" + costoPorKilo + " por kg)";
    }
}
