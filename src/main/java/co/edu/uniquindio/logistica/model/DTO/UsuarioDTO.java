package co.edu.uniquindio.logistica.model.DTO;

import co.edu.uniquindio.logistica.model.MetodoPago;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String password;
    private boolean admin;
    private List<DireccionDTO> direcciones = new ArrayList<>();
    private List<MetodoPago> metodosPago = new ArrayList<>();

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.admin = admin;
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return admin; }
    public List<DireccionDTO> getDirecciones() { return direcciones; }
    public List<MetodoPago> getMetodosPago() { return metodosPago; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setPassword(String password) { this.password = password; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public void setDirecciones(List<DireccionDTO> direcciones) { this.direcciones = direcciones; }
    public void setMetodosPago(List<MetodoPago> metodosPago) { this.metodosPago = metodosPago; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioDTO)) return false;
        UsuarioDTO u = (UsuarioDTO) o;
        return Objects.equals(id, u.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return nombre + " <" + email + ">";
    }
}
