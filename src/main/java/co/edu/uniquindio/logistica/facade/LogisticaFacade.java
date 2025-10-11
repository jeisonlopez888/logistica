package co.edu.uniquindio.logistica.facade;

import co.edu.uniquindio.logistica.factory.EntityFactory;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.service.*;
import co.edu.uniquindio.logistica.store.DataStore;

import java.time.LocalDateTime;
import java.util.List;

public class LogisticaFacade {

    private final UsuarioService usuarioService;
    private final EnvioService envioService;
    private final TarifaService tarifaService;
    private final PagoService pagoService;
    private final DataStore store;

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

    // Usuarios
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
        registrarUsuario(usuario);
        return usuario;
    }

    // Envíos
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
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Crear un nuevo envío.
     * Aseguramos que el envío tenga asignado el usuario antes de registrarlo.
     */
    public Envio crearEnvio(Usuario usuario, Direccion origen, Direccion destino, double peso) {
        Envio envio = EntityFactory.createEnvio(usuario, origen, destino, peso);
        // Por si EntityFactory no asignó el usuario, lo forzamos:
        if (envio.getUsuario() == null && usuario != null) {
            envio.setUsuario(usuario);
        }
        if (envio.getFechaCreacion() == null) {
            envio.setFechaCreacion(LocalDateTime.now());
        }
        registrarEnvio(envio);
        return envio;
    }

    // Pagos
    public void addPago(Pago pago) {
        store.addPago(pago);
    }

    public Pago registrarPagoSimulado(Envio envio, double monto, MetodoPago metodo) {
        return pagoService.registrarPagoEnvio(envio, monto, metodo);
    }

    public List<Pago> getPagos() {
        return pagoService.listarPagos();
    }

    public Pago buscarPagoPorId(Long id) {
        return pagoService.buscarPagoPorId(id);
    }

    public void eliminarPago(Pago pago) {
        pagoService.eliminarPago(pago);
    }

    /**
     * Confirmar un pago:
     * - marcar pago confirmado
     * - setear fecha de confirmación en el envío
     * - cambiar estado del envío a CONFIRMADO y luego intentar asignar repartidor
     * Devuelve mensaje con resultado y, si aplica, nombre del repartidor asignado.
     */
    public String confirmarPago(Long pagoId) {
        Pago pago = store.getPagos().stream()
                .filter(p -> p.getId().equals(pagoId))
                .findFirst()
                .orElse(null);

        if (pago == null) return "❌ No se encontró el pago.";

        pago.setConfirmado(true);

        Envio envio = pago.getEnvio();
        if (envio != null) {
            // Fecha de confirmación
            envio.setFechaConfirmacion(LocalDateTime.now());
            // Estado inicial: CONFIRMADO
            envio.setEstado(Envio.EstadoEnvio.CONFIRMADO);

            // Intentar asignar repartidor automáticamente
            boolean asignado = envioService.asignarRepartidor(envio);

            // Guardar cambios explícitamente en el DataStore (por si el objeto no es el mismo)
            store.getEnvios().replaceAll(e -> e.getId().equals(envio.getId()) ? envio : e);

            if (asignado) {
                return "✅ Pago confirmado y repartidor asignado: " +
                        (envio.getRepartidor() != null ? envio.getRepartidor().getNombre() : "Desconocido");
            } else {
                return "✅ Pago confirmado, pero no hay repartidores disponibles en la zona.";
            }
        }

        return "⚠️ Pago confirmado, pero el envío asociado no existe.";
    }

    // Tarifas
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

    // Repartidores
    public void registrarRepartidor(Repartidor repartidor) {
        store.addRepartidor(repartidor);
    }

    public void eliminarRepartidor(Repartidor repartidor) {
        store.getRepartidores().remove(repartidor);
    }

    public List<Repartidor> listarRepartidores() {
        return store.getRepartidores();
    }

    public Repartidor buscarRepartidorPorId(Long id) {
        return store.getRepartidores().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Utilidades
    public Usuario buscarUsuarioPorEmail(String email) {
        return store.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<Envio> buscarEnviosPorEstado(Usuario usuario, Envio.EstadoEnvio estado) {
        return store.getEnvios().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().equals(usuario) && e.getEstado() == estado)
                .toList();
    }

    public List<Repartidor> listarRepartidoresDisponibles() {
        return store.getRepartidores().stream()
                .filter(Repartidor::isDisponible)
                .toList();
    }
}
