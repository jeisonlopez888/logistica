package co.edu.uniquindio.logistica.model;

/**
 * Representa la estructura de una tarifa de envío.
 * Define costos base, costo por kilo, costo por volumen y posibles recargos.
 */
public class Tarifa {

    private Long id;
    private String descripcion;
    private double costoBase;
    private double costoPorKilo;
    private double costoPorVolumen; // costo adicional por volumen (opcional)
    private double recargoSeguro;   // nuevo campo para recargo por seguro (porcentaje o valor fijo)

    public Tarifa(Long id, String descripcion, double costoBase, double costoPorKilo) {
        this(id, descripcion, costoBase, costoPorKilo, 0, 0);
    }

    public Tarifa(Long id, String descripcion, double costoBase, double costoPorKilo, double costoPorVolumen, double recargoSeguro) {
        this.id = id;
        this.descripcion = descripcion;
        this.costoBase = costoBase;
        this.costoPorKilo = costoPorKilo;
        this.costoPorVolumen = costoPorVolumen;
        this.recargoSeguro = recargoSeguro;
    }

    // Getters
    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public double getCostoBase() { return costoBase; }
    public double getCostoPorKilo() { return costoPorKilo; }
    public double getCostoPorVolumen() { return costoPorVolumen; }
    public double getRecargoSeguro() { return recargoSeguro; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCostoBase(double costoBase) { this.costoBase = costoBase; }
    public void setCostoPorKilo(double costoPorKilo) { this.costoPorKilo = costoPorKilo; }
    public void setCostoPorVolumen(double costoPorVolumen) { this.costoPorVolumen = costoPorVolumen; }
    public void setRecargoSeguro(double recargoSeguro) { this.recargoSeguro = recargoSeguro; }

    /**
     * Método de compatibilidad simple: calcula el costo total en base al peso.
     * (Usado por el código de inicialización y ejemplos antiguos)
     */
    public double calcularCosto(double peso) {
        return costoBase + (peso * costoPorKilo);
    }
}
