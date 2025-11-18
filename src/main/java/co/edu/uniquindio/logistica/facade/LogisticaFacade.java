package co.edu.uniquindio.logistica.facade;

import co.edu.uniquindio.logistica.factory.EntityFactory;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.model.DTO.*;
import co.edu.uniquindio.logistica.service.*;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.Mappers.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade que actúa como capa de presentación entre controladores y servicios.
 * Trabaja exclusivamente con DTOs, convirtiendo a entidades para los servicios.
 */
public class LogisticaFacade {

    private final UsuarioService usuarioService;
    private final EnvioService envioService;
    private final TarifaService tarifaService;
    private final PagoService pagoService;
    private final DataStore store;
    private final RepartidorService repartidorService;

    private static LogisticaFacade instance;

    private LogisticaFacade() {
        this.usuarioService = new UsuarioService();
        this.envioService = new EnvioService();
        this.tarifaService = new TarifaService();
        this.pagoService = new PagoService();
        this.store = DataStore.getInstance();
        this.repartidorService = new RepartidorService();
    }

    public static LogisticaFacade getInstance() {
        if (instance == null) {
            instance = new LogisticaFacade();
        }
        return instance;
    }

    // ========== USUARIOS ==========
    
    public UsuarioDTO login(String email, String password) {
        Usuario usuario = usuarioService.login(email, password);
        return UsuarioMapper.toDTO(usuario);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios().stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void registrarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = UsuarioMapper.toEntity(usuarioDTO);
        usuarioService.registrarUsuario(usuario);
    }

    public UsuarioDTO crearUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        Usuario usuario = EntityFactory.createUsuario(id, nombre, email, telefono, password, admin);
        usuarioService.registrarUsuario(usuario);
        return UsuarioMapper.toDTO(usuario);
    }

    public void eliminarUsuario(Long idUsuario) {
        Usuario usuario = store.getUsuarios().stream()
                .filter(u -> u.getId().equals(idUsuario))
                .findFirst()
                .orElse(null);
        if (usuario != null) {
            store.getUsuarios().remove(usuario);
        }
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = store.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
        return UsuarioMapper.toDTO(usuario);
    }

    public void actualizarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = UsuarioMapper.toEntity(usuarioDTO);
        usuarioService.actualizarUsuario(usuario);
    }

    // ========== ENVÍOS ==========
    
    public void registrarEnvio(EnvioDTO envioDTO) {
        Envio envio = EnvioMapper.toEntity(envioDTO);
        // Asegurar que el costo esté calculado antes de guardar
        if (envioDTO.getCostoEstimado() == 0.0 || envio.getCostoEstimado() == 0.0) {
            double costoCalculado = tarifaService.calcularTarifa(envio);
            envio.setCostoEstimado(costoCalculado);
            envioDTO.setCostoEstimado(costoCalculado);
        }
        envioService.registrarEnvio(envio);
    }
    
    /**
     * Establece el canal de notificación preferido para los próximos envíos
     */
    public void setCanalNotificacion(co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion canal) {
        envioService.setCanalNotificacion(canal);
    }

    public List<EnvioDTO> listarEnviosPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        if (usuario == null) return List.of();

        return envioService.listarEnviosPorUsuario(usuario).stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> listarTodosEnvios() {
        return envioService.listarTodos().stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void eliminarEnvio(Long idEnvio) {
        Envio envio = store.getEnvios().stream()
                .filter(e -> e.getId().equals(idEnvio))
                .findFirst()
                .orElse(null);
        if (envio != null) {
            envioService.eliminarEnvio(envio);
        }
    }

    public EnvioDTO buscarEnvioPorId(Long id) {
        Envio envio = envioService.buscarPorId(id);
        return EnvioMapper.toDTO(envio);
    }
    
    /**
     * Obtiene información completa de un envío incluyendo datos del repartidor
     */
    public EnvioDTO obtenerEnvioCompleto(Long id) {
        Envio envio = envioService.buscarPorId(id);
        if (envio == null) return null;
        return EnvioMapper.toDTO(envio);
    }
    
    /**
     * Filtra envíos por fecha y estado
     */
    public List<EnvioDTO> filtrarEnvios(Long idUsuario, java.time.LocalDate fechaInicio, 
                                        java.time.LocalDate fechaFin, EnvioDTO.EstadoEnvio estadoDTO) {
        List<Envio> envios;
        if (idUsuario != null) {
            Usuario usuario = usuarioService.buscarPorId(idUsuario);
            if (usuario == null) return List.of();
            envios = envioService.listarEnviosPorUsuario(usuario);
        } else {
            envios = envioService.listarTodos();
        }
        
        // Convertir estado DTO a entidad
        Envio.EstadoEnvio estado = null;
        if (estadoDTO != null) {
            estado = Envio.EstadoEnvio.valueOf(estadoDTO.name());
        }
        
        List<Envio> filtrados = envioService.filtrarEnvios(envios, fechaInicio, fechaFin, estado);
        return filtrados.stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el nombre de un usuario por su ID
     */
    public String obtenerNombreUsuario(Long idUsuario) {
        if (idUsuario == null) return "";
        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        return usuario != null ? usuario.getNombre() : "Usuario #" + idUsuario;
    }
    
    /**
     * Obtiene el nombre de un repartidor por su ID
     */
    public String obtenerNombreRepartidor(Long idRepartidor) {
        if (idRepartidor == null) return "Sin asignar";
        Repartidor repartidor = repartidorService.buscarPorId(idRepartidor);
        return repartidor != null ? repartidor.getNombre() : "Repartidor #" + idRepartidor;
    }
    
    /**
     * Obtiene información completa de un repartidor por su ID
     */
    public RepartidorDTO obtenerRepartidorCompleto(Long idRepartidor) {
        if (idRepartidor == null) return null;
        Repartidor repartidor = repartidorService.buscarPorId(idRepartidor);
        return repartidor != null ? RepartidorMapper.toDTO(repartidor) : null;
    }
    
    /**
     * Obtiene una lista de nombres de repartidores disponibles
     */
    public List<String> obtenerNombresRepartidores() {
        return repartidorService.listarRepartidoresDisponibles();
    }

    public EnvioDTO crearEnvio(UsuarioDTO usuarioDTO, DireccionDTO origenDTO, DireccionDTO destinoDTO, double peso) {
        Usuario usuario = UsuarioMapper.toEntity(usuarioDTO);
        Direccion origen = DireccionMapper.toEntity(origenDTO);
        Direccion destino = DireccionMapper.toEntity(destinoDTO);
        
        Envio envio = EntityFactory.createEnvio(usuario, origen, destino, peso);
        if (envio.getFechaCreacion() == null) {
            envio.setFechaCreacion(LocalDateTime.now());
        }
        envioService.registrarEnvio(envio);
        return EnvioMapper.toDTO(envio);
    }

    public List<EnvioDTO> buscarEnviosPorEstado(Long idUsuario, EnvioDTO.EstadoEnvio estadoDTO) {
        Usuario usuario = store.getUsuarios().stream()
                .filter(u -> u.getId().equals(idUsuario))
                .findFirst()
                .orElse(null);
        if (usuario == null) return List.of();
        
        Envio.EstadoEnvio estado = Envio.EstadoEnvio.valueOf(estadoDTO.name());
        return store.getEnvios().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().equals(usuario) && e.getEstado() == estado)
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean asignarRepartidor(Long idEnvio) {
        Envio envio = store.getEnvios().stream()
                .filter(e -> e.getId().equals(idEnvio))
                .findFirst()
                .orElse(null);
        if (envio == null) return false;
        return envioService.asignarRepartidor(envio);
    }

    public String obtenerIncidencia(Long idEnvio) {
        return envioService.obtenerIncidencia(idEnvio);
    }

    // ========== PAGOS ==========
    
    public PagoDTO registrarPagoSimulado(Long idEnvio, double monto, MetodoPago metodo) {
        Envio envio = store.getEnvios().stream()
                .filter(e -> e.getId().equals(idEnvio))
                .findFirst()
                .orElse(null);
        if (envio == null) return null;
        
        // Asegurar que el costo estimado del envío esté actualizado
        // Si el costo estimado es 0 o diferente al monto, actualizarlo
        if (envio.getCostoEstimado() == 0.0 || Math.abs(envio.getCostoEstimado() - monto) > 0.01) {
            envio.setCostoEstimado(monto);
        }
        
        // El monto pagado será igual al costo estimado del envío (se establece en el constructor de Pago)
        Pago pago = pagoService.registrarPagoEnvio(envio, envio.getCostoEstimado(), metodo);
        return PagoMapper.toDTO(pago);
    }

    public List<PagoDTO> getPagos() {
        return pagoService.listarPagos().stream()
                .map(PagoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PagoDTO buscarPagoPorId(Long id) {
        Pago pago = pagoService.buscarPagoPorId(id);
        return PagoMapper.toDTO(pago);
    }

    public PagoDTO buscarPagoPorEnvio(Long idEnvio) {
        Envio envio = store.getEnvios().stream()
                .filter(e -> e.getId().equals(idEnvio))
                .findFirst()
                .orElse(null);
        if (envio == null) return null;
        Pago pago = pagoService.buscarPagoPorEnvio(envio);
        return pago != null ? PagoMapper.toDTO(pago) : null;
    }

    public void eliminarPago(Long idPago) {
        Pago pago = store.getPagos().stream()
                .filter(p -> p.getId().equals(idPago))
                .findFirst()
                .orElse(null);
        if (pago != null) {
            pagoService.eliminarPago(pago);
        }
    }

    public String confirmarPago(Long pagoId) {
        return pagoService.confirmarPago(pagoId);
    }

    public void actualizarMetodoPago(Long pagoId, MetodoPago metodo) {
        pagoService.actualizarMetodoPago(pagoId, metodo);
    }

    // ========== TARIFAS ==========
    
    public void addTarifa(TarifaDTO tarifaDTO) {
        Tarifa tarifa = TarifaMapper.toEntity(tarifaDTO);
        store.addTarifa(tarifa);
    }

    public void eliminarTarifa(Long idTarifa) {
        Tarifa tarifa = store.getTarifas().stream()
                .filter(t -> t.getId().equals(idTarifa))
                .findFirst()
                .orElse(null);
        if (tarifa != null) {
            store.getTarifas().remove(tarifa);
        }
    }

    public void actualizarTarifa(TarifaDTO tarifaDTO) {
        Tarifa tarifa = store.getTarifas().stream()
                .filter(t -> t.getId().equals(tarifaDTO.getId()))
                .findFirst()
                .orElse(null);
        if (tarifa != null) {
            TarifaMapper.updateEntityFromDTO(tarifa, tarifaDTO);
        }
    }

    public List<TarifaDTO> getTarifas() {
        return store.getTarifas().stream()
                .map(TarifaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getNombresTarifas() {
        return store.getTarifas().stream()
                .map(Tarifa::getDescripcion)
                .collect(Collectors.toList());
    }

    public double calcularTarifa(double peso) {
        return tarifaService.calcularTarifa(peso);
    }

    public double calcularTarifa(EnvioDTO envioDTO) {
        Envio envio = EnvioMapper.toEntity(envioDTO);
        return tarifaService.calcularTarifa(envio);
    }

    public TarifaService.TarifaDetalle desglosarTarifa(EnvioDTO envioDTO) {
        Envio envio = EnvioMapper.toEntity(envioDTO);
        return tarifaService.desglosarTarifa(envio);
    }

    // ========== REPARTIDORES ==========
    
    public void registrarRepartidor(RepartidorDTO repartidorDTO) {
        Repartidor repartidor = RepartidorMapper.toEntity(repartidorDTO);
        store.addRepartidor(repartidor);
    }

    public void eliminarRepartidor(Long idRepartidor) {
        Repartidor repartidor = store.getRepartidores().stream()
                .filter(r -> r.getId().equals(idRepartidor))
                .findFirst()
                .orElse(null);
        if (repartidor != null) {
            store.getRepartidores().remove(repartidor);
        }
    }

    public List<RepartidorDTO> listarRepartidores() {
        return store.getRepartidores().stream()
                .map(RepartidorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public RepartidorDTO buscarRepartidorPorId(Long id) {
        Repartidor repartidor = store.getRepartidores().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
        return RepartidorMapper.toDTO(repartidor);
    }

    public RepartidorDTO buscarRepartidorPorNombre(String nombre) {
        Repartidor repartidor = store.getRepartidores().stream()
                .filter(r -> r.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
        return RepartidorMapper.toDTO(repartidor);
    }

    public List<RepartidorDTO> listarNombresRepartidoresDisponibles() {
        return store.getRepartidores().stream()
                .filter(Repartidor::isDisponible)
                .map(RepartidorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void marcarRepartidorNoDisponible(Long idRepartidor) {
        Repartidor repartidor = store.getRepartidores().stream()
                .filter(r -> r.getId().equals(idRepartidor))
                .findFirst()
                .orElse(null);
        if (repartidor != null) {
            repartidorService.marcarNoDisponible(repartidor);
        }
    }

    public void marcarRepartidorDisponible(Long idRepartidor) {
        Repartidor repartidor = store.getRepartidores().stream()
                .filter(r -> r.getId().equals(idRepartidor))
                .findFirst()
                .orElse(null);
        if (repartidor != null) {
            repartidorService.marcarDisponible(repartidor);
        }
    }

    // ========== UTILIDADES ==========
    
    public DireccionDTO crearDireccion(String nombre, String calle, String ciudad, String coordenadas) {
        // Constructor: Direccion(Long id, String alias, String calle, String ciudad, String coordenadas)
        Direccion direccion = new Direccion(DataStore.getInstance().nextId(), nombre, calle, ciudad, coordenadas != null ? coordenadas : "");
        return DireccionMapper.toDTO(direccion);
    }

    // Métodos legacy para compatibilidad con código existente (deberían ser refactorizados)
    @Deprecated
    public List<Envio> listarTodosLosEnvios() {
        return envioService.listarTodos();
    }
    
    // ========== FACTURAS ==========
    
    /**
     * Genera una factura/guía de envío en PDF
     */
    public void generarFacturaEnvio(Long envioId, String rutaArchivo) throws IOException {
        Envio envio = store.getEnvios().stream()
                .filter(e -> e.getId().equals(envioId))
                .findFirst()
                .orElse(null);
        if (envio == null) {
            throw new IllegalArgumentException("Envío no encontrado");
        }
        
        Pago pago = store.getPagos().stream()
                .filter(p -> p.getEnvio() != null && p.getEnvio().getId().equals(envioId))
                .findFirst()
                .orElse(null);
        
        co.edu.uniquindio.logistica.util.ReportUtil.exportarFacturaEnvio(envio, pago, rutaArchivo);
    }
}
