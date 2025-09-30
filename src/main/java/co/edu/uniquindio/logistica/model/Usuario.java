package co.edu.uniquindio.logistica.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private List<Direccion> direcciones = new ArrayList<>();

    public Usuario(Long id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public List<Direccion> getDirecciones() { return direcciones; }
}

