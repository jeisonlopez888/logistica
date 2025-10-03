package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;

public class UsuarioService {
    private final DataStore store = DataStore.getInstance();

    // LOGIN
    public Usuario login(String email, String password) {
        return store.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email)
                        && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // LISTAR
    public List<Usuario> listarUsuarios() {
        return store.getUsuarios();
    }

    // CREAR
    public void registrarUsuario(Usuario usuario) {
        store.addUsuario(usuario);
    }

    // ELIMINAR
    public void eliminarUsuario(Usuario usuario) {
        store.getUsuarios().remove(usuario);
    }
}

