package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;
import java.util.Objects;

public class UsuarioService {
    private final DataStore store = DataStore.getInstance();

    public Usuario login(String email, String password) {
        return store.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> listarUsuarios() {
        return store.getUsuarios();
    }

    private void validarUsuarioDuplicado(Usuario usuario) {
        boolean idExiste = store.getUsuarios().stream()
                .anyMatch(u -> Objects.equals(u.getId(), usuario.getId()));
        boolean correoExiste = store.getUsuarios().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()));

        if (idExiste) {
            throw new IllegalArgumentException("Ya existe un usuario con ese ID o cédula.");
        }
        if (correoExiste) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
    }

    public void registrarUsuario(Usuario usuario) {
        validarUsuarioDuplicado(usuario);
        store.addUsuario(usuario);
    }

    public void eliminarUsuario(Usuario usuario) {
        store.getUsuarios().remove(usuario);
    }
}
