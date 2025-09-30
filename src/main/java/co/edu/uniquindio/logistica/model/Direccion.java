package co.edu.uniquindio.logistica.model;

public class Direccion {
    private Long id;
    private String alias;
    private String calle;
    private String ciudad;
    private String coordenadas;

    public Direccion(Long id, String alias, String calle, String ciudad, String coordenadas) {
        this.id = id;
        this.alias = alias;
        this.calle = calle;
        this.ciudad = ciudad;
        this.coordenadas = coordenadas;
    }

    public Long getId() { return id; }
    public String getAlias() { return alias; }
    public String getCalle() { return calle; }
    public String getCiudad() { return ciudad; }
    public String getCoordenadas() { return coordenadas; }
}

