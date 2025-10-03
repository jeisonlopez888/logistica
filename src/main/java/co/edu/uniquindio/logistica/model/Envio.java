package co.edu.uniquindio.logistica.model;

public class Envio {
    private Long id;
    private Direccion origen;
    private Direccion destino;
    private double peso;
    private Usuario usuario;
    private String estado = "SOLICITADO";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Direccion getOrigen() { return origen; }
    public void setOrigen(Direccion origen) { this.origen = origen; }

    public Direccion getDestino() { return destino; }
    public void setDestino(Direccion destino) { this.destino = destino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Métodos para TableView (evitan bindings complejos)
    public String getOrigenDireccion() {
        return origen == null ? "" : origen.getCalle();
    }
    public String getDestinoDireccion() {
        return destino == null ? "" : destino.getCalle();
    }
}


