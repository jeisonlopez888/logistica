package co.edu.uniquindio.logistica.facade;

import co.edu.uniquindio.logistica.factory.EntityFactory;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.service.*;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;
import java.util.Objects;

public class LogisticaFacade {

    private final UsuarioService usuarioService;
    private final EnvioService envioService;
    private final TarifaService tarifaService;
    private final PagoService pagoService;
    private final DataStore store;

    // 🔹 Singleton
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

    // ---------------- 🧑 USUARIOS ----------------
    public Usuario login(String email, String password) {
        return usuarioService.login(email, password);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    public void registrarUsuario(Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
    }

    public Usuario crearUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        Usuario usuario = EntityFactory.createUsuario(id, nombre, email, telefono, password, admin);
        usuarioService.registrarUsuario(usuario);
        return usuario;
    }

    // ---------------- 📦 ENVIOS ----------------
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

    public Envio buscarEnvioPorId(Long id) {
        return store.getEnvios().stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst()
                .orElse(null);
    }

    // ---------------- 💰 PAGOS ----------------
    public void addPago(Pago pago) {
        store.addPago(pago);
    }

    public void eliminarPago(Pago pago) {
        store.getPagos().remove(pago);
    }

    public List<Pago> getPagos() {
        return store.getPagos();
    }

    public Pago registrarPago(Envio envio, double montoPagado, double montoCalculado) {
        return pagoService.registrarPagoEnvio(envio, montoPagado, montoCalculado);
    }

    // ---------------- 📈 TARIFAS ----------------
    public void addTarifa(Tarifa tarifa) {
        store.addTarifa(tarifa);
    }

    public void eliminarTarifa(Tarifa tarifa) {
        store.getTarifas().remove(tarifa);
    }

    public List<Tarifa> getTarifas() {
        return store.getTarifas();
    }

    public double calcularTarifa(double peso) {
        return tarifaService.calcularTarifa(peso);
    }

    // ---------------- 🚚 REPARTIDORES ----------------
    public void registrarRepartidor(Repartidor repartidor) {
        store.addRepartidor(repartidor);
    }

    public void eliminarRepartidor(Repartidor repartidor) {
        store.getRepartidores().remove(repartidor);
    }

    public List<Repartidor> getRepartidores() {
        return store.getRepartidores();
    }

    public List<Repartidor> listarRepartidores() {
        return store.getRepartidores();
    }

    public Repartidor buscarRepartidorPorId(Long id) {
        return store.getRepartidores().stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst()
                .orElse(null);
    }

    // ---------------- 🧾 REPORTES (uso general) ----------------
    public List<Usuario> obtenerUsuariosParaReporte() {
        return listarUsuarios();
    }

    public List<Envio> obtenerEnviosParaReporte() {
        return listarTodosEnvios();
    }

    public List<Pago> obtenerPagosParaReporte() {
        return getPagos();
    }

    public List<Repartidor> obtenerRepartidoresParaReporte() {
        return getRepartidores();
    }
}
