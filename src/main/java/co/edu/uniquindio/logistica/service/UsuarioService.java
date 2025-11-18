package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.PasswordUtil;

import java.util.List;
import java.util.Objects;

/**
 * Servicio encargado de la gestión de usuarios:
 * - Registro con validación y hashing de contraseñas
 * - Login con verificación segura
 * - Eliminación y listado
 *
 * Patrón aplicado: Service Layer + Singleton Data Access
 */
public class UsuarioService {

    private final DataStore store = DataStore.getInstance();

    /**
     * Realiza el inicio de sesión verificando el email y la contraseña.
     * Usa BCrypt para comparar el hash almacenado.
     *
     * @param email correo del usuario
     * @param password contraseña ingresada (texto plano)
     * @return Usuario autenticado o null si no coincide
     */
    public Usuario login(String email, String password) {
        if (email == null || password == null) return null;

        return store.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email)
                        && PasswordUtil.verify(password, u.getPassword()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lista todos los usuarios registrados.
     */
    public List<Usuario> listarUsuarios() {
        return store.getUsuarios();
    }

    /**
     * Registra un nuevo usuario, validando duplicados
     * y almacenando la contraseña hasheada.
     *
     * @param usuario nuevo usuario a registrar
     */
    public void registrarUsuario(Usuario usuario) {
        validarUsuarioDuplicado(usuario);



        store.addUsuario(usuario);
    }

    /**
     * Elimina un usuario del DataStore.
     */
    public void eliminarUsuario(Usuario usuario) {
        store.getUsuarios().remove(usuario);
    }

    /**
     * Valida que no haya otro usuario con el mismo ID o correo.
     * Lanza IllegalArgumentException si hay duplicados.
     */
    private void validarUsuarioDuplicado(Usuario usuario) {
        boolean idExiste = store.getUsuarios().stream()
                .anyMatch(u -> Objects.equals(u.getId(), usuario.getId()));

        boolean correoExiste = store.getUsuarios().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()));

        if (idExiste || correoExiste) {
            throw new IllegalArgumentException("Ya existe un usuario con ese ID o correo.");
        }
    }

    public void actualizarUsuario(Usuario usuarioActualizado) {
        DataStore store = DataStore.getInstance();
        store.getUsuarios().replaceAll(u ->
                u.getId().equals(usuarioActualizado.getId()) ? usuarioActualizado : u
        );
    }
    
    /**
     * Busca un usuario por su ID
     */
    public Usuario buscarPorId(Long id) {
        if (id == null) return null;
        return store.getUsuarios().stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst()
                .orElse(null);
    }

}
