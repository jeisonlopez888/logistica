package co.edu.uniquindio.logistica.model.DTO;

public class RepartidorDTO {
    private Long id;
    private String nombre;
    private String documento;
    private String telefono;
    private String zona;
    private boolean disponible;

    public RepartidorDTO() {
    }

    public RepartidorDTO(Long id, String nombre, String documento, String telefono, String zona, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.zona = zona;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getZona() { return zona; }
    public boolean isDisponible() { return disponible; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setZona(String zona) { this.zona = zona; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
