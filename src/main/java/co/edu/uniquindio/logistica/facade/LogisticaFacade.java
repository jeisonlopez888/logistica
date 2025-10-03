package co.edu.uniquindio.logistica.facade;

import co.edu.uniquindio.logistica.factory.EntityFactory;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.service.EnvioService;
import co.edu.uniquindio.logistica.service.PagoService;
import co.edu.uniquindio.logistica.service.TarifaService;
import co.edu.uniquindio.logistica.service.UsuarioService;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;

public class LogisticaFacade {

    private final UsuarioService usuarioService;
    private final EnvioService envioService;
    private final TarifaService tarifaService;
    private final PagoService pagoService;
    private final DataStore store;

    // Singleton
    private static LogisticaFacade instance;

    private LogisticaFacade() {
        this.usuarioService = new UsuarioService();
        this.envioService = new EnvioService();
        this.tarifaService = new TarifaService();
        this.pagoService = new PagoService();
        this.store = DataStore.getInstance();
    }

    public static LogisticaFacade getInstance() {
        if (instance == null) {
            instance = new LogisticaFacade();
        }
        return instance;
    }

    // ---------------- USUARIOS ----------------
    public Usuario login(String email, String password) {
        return usuarioService.login(email, password);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    public void registrarUsuario(Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
    }

    // ✅ Nuevo método centralizado para crear usuarios
    public Usuario crearUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        Usuario usuario = EntityFactory.createUsuario(id, nombre, email, telefono, password, admin);
        usuarioService.registrarUsuario(usuario);
        return usuario;
    }

    // ---------------- ENVIOS ----------------
    // En LogisticaFacade
    public Long generarId() {
        return store.nextId();
    }


    public void registrarEnvio(Envio envio) {
        envioService.registrarEnvio(envio);
    }

    public List<Envio> listarEnviosPorUsuario(Usuario usuario) {
        return envioService.listarEnviosPorUsuario(usuario);
    }

    public List<Envio> listarTodosEnvios() {
        return envioService.listarTodos();
    }

    public void eliminarEnvio(Envio envio) {
        envioService.eliminarEnvio(envio);
    }
}
