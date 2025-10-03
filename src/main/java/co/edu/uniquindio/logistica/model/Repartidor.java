package co.edu.uniquindio.logistica.model;

public class Repartidor {
    private Long id;
    private String nombre;
    private String telefono;
    private String zona;
    private boolean disponible;

    public Repartidor(Long id, String nombre, String telefono, String zona, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.zona = zona;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getZona() { return zona; }
    public boolean isDisponible() { return disponible; }
}

