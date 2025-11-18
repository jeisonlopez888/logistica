package co.edu.uniquindio.logistica.store;

import co.edu.uniquindio.logistica.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DataStore - Almacenamiento centralizado de datos en memoria
 * Patrón Singleton para garantizar una única instancia
 * 
 * RF-036: Almacenamiento centralizado en memoria
 * RF-042: Patrón Singleton
 */
public class DataStore {

    private static DataStore instance;
    private final AtomicLong seq = new AtomicLong(111); // Inicia en 111 para que los nuevos envíos no se solapen con los de ejemplo

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Envio> envios = new ArrayList<>();
    private final List<Repartidor> repartidores = new ArrayList<>();
    private final List<Tarifa> tarifas = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();

    private DataStore() {
        initSampleData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public long nextId() {
        return seq.getAndIncrement();
    }

    // Getters
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Envio> getEnvios() { return envios; }
    public List<Repartidor> getRepartidores() { return repartidores; }
    public List<Tarifa> getTarifas() { return tarifas; }
    public List<Pago> getPagos() { return pagos; }

    // Métodos para agregar datos
    public void addUsuario(Usuario u) { usuarios.add(u); }

    public void addEnvio(Envio envio) {
        if (envio.getId() == null) {
            envio.setId(nextId());
        }
        if (envio.getFechaCreacion() == null) {
            envio.setFechaCreacion(LocalDateTime.now());
        }
        envios.add(envio);
    }

    public void addRepartidor(Repartidor r) {
        if (r.getId() == null) {
            r = new Repartidor(nextId(), r.getNombre(), r.getDocumento(), r.getTelefono(), r.getZona(), r.isDisponible());
        }
        repartidores.add(r);
    }

    public void addTarifa(Tarifa t) {
        if (t.getId() == null) {
            t.setId(nextId());
        }
        tarifas.add(t);
    }

    public void addPago(Pago p) {
        pagos.add(p);
    }

    /**
     * Inicializa datos de ejemplo completos y consistentes para el sistema
     * Incluye: Usuarios, Direcciones, Métodos de Pago, Repartidores, Tarifas, Envíos y Pagos
     * 
     * RF-015 a RF-035: Todas las entidades con datos completos
     */
    private void initSampleData() {
        System.out.println("🔄 Cargando datos de ejemplo del sistema logístico...\n");

        // ==================== USUARIOS (RF-015 a RF-017) ====================
        
        // Usuario regular 1 - Carlos Pérez
        Usuario u1 = new Usuario(nextId(), "Carlos Pérez", "carlos@uniquindio.edu", "3101234567", "12345", false);
        List<Direccion> direccionesU1 = new ArrayList<>();
        direccionesU1.add(new Direccion(nextId(), "Casa", "Calle 10 # 5-20", "Norte", "Armenia"));
        direccionesU1.add(new Direccion(nextId(), "Trabajo", "Cra 14 # 2-30", "Norte", "Armenia"));
        u1.setDirecciones(direccionesU1);
        u1.agregarMetodoPago(MetodoPago.TARJETA_CREDITO);
        u1.agregarMetodoPago(MetodoPago.PSE);
        usuarios.add(u1);

        // Administrador 1 - Ana López (RF-010)
        Usuario u2 = new Usuario(nextId(), "Ana López", "ana@uniquindio.edu", "3109876543", "admin123", true);
        List<Direccion> direccionesU2 = new ArrayList<>();
        direccionesU2.add(new Direccion(nextId(), "Oficina Principal", "Carrera 15 # 8-90", "Centro", "Armenia"));
        direccionesU2.add(new Direccion(nextId(), "Casa", "Calle 20 # 5-10", "Centro", "Armenia"));
        u2.setDirecciones(direccionesU2);
        u2.agregarMetodoPago(MetodoPago.TRANSFERENCIA);
        u2.agregarMetodoPago(MetodoPago.EFECTIVO);
        usuarios.add(u2);

        // Usuario regular 2 - María Gómez
        Usuario u3 = new Usuario(nextId(), "María Gómez", "maria@uniquindio.edu", "3115558899", "maria123", false);
        List<Direccion> direccionesU3 = new ArrayList<>();
        direccionesU3.add(new Direccion(nextId(), "Apartamento", "Calle 45 # 12-60", "Sur", "Armenia"));
        direccionesU3.add(new Direccion(nextId(), "Oficina", "Av. Bolívar # 20-15", "Centro", "Armenia"));
        u3.setDirecciones(direccionesU3);
        u3.agregarMetodoPago(MetodoPago.EFECTIVO);
        u3.agregarMetodoPago(MetodoPago.PSE);
        usuarios.add(u3);

        // Administrador 2 - Pedro Sánchez (RF-010)
        Usuario u4 = new Usuario(nextId(), "Pedro Sánchez", "pedro@uniquindio.edu", "3124445566", "admin456", true);
        List<Direccion> direccionesU4 = new ArrayList<>();
        direccionesU4.add(new Direccion(nextId(), "Casa", "Carrera 7 # 30-20", "Centro", "Armenia"));
        direccionesU4.add(new Direccion(nextId(), "Tienda", "Calle 23 # 15-40", "Centro", "Armenia"));
        u4.setDirecciones(direccionesU4);
        u4.agregarMetodoPago(MetodoPago.TARJETA_CREDITO);
        u4.agregarMetodoPago(MetodoPago.TRANSFERENCIA);
        usuarios.add(u4);

        // Usuario regular 3 - Lucía Ramírez
        Usuario u5 = new Usuario(nextId(), "Lucía Ramírez", "lucia@uniquindio.edu", "3132223344", "lucia789", false);
        List<Direccion> direccionesU5 = new ArrayList<>();
        direccionesU5.add(new Direccion(nextId(), "Casa", "Calle 12 # 18-45", "Norte", "Armenia"));
        direccionesU5.add(new Direccion(nextId(), "Trabajo", "Cra 20 # 10-33", "Norte", "Armenia"));
        u5.setDirecciones(direccionesU5);
        u5.agregarMetodoPago(MetodoPago.TRANSFERENCIA);
        u5.agregarMetodoPago(MetodoPago.EFECTIVO);
        u5.agregarMetodoPago(MetodoPago.PSE);
        usuarios.add(u5);

        // Usuario regular 4 - Pepito Pérez
        Usuario u6 = new Usuario(nextId(), "Pepito Pérez", "pepito@uniquindio.edu", "3213213211", "pepito123", false);
        List<Direccion> direccionesU6 = new ArrayList<>();
        direccionesU6.add(new Direccion(nextId(), "Residencia", "Av. 6N # 34-90", "Centro", "Armenia"));
        direccionesU6.add(new Direccion(nextId(), "Negocio", "Calle 8 # 18-22", "Norte", "Armenia"));
        u6.setDirecciones(direccionesU6);
        u6.agregarMetodoPago(MetodoPago.PSE);
        u6.agregarMetodoPago(MetodoPago.TARJETA_CREDITO);
        usuarios.add(u6);

        // Usuario regular 5 - Sofía Torres
        Usuario u7 = new Usuario(nextId(), "Sofía Torres", "sofia@uniquindio.edu", "3145556677", "sofia456", false);
        List<Direccion> direccionesU7 = new ArrayList<>();
        direccionesU7.add(new Direccion(nextId(), "Hogar", "Cra 18 # 25-50", "Sur", "Armenia"));
        direccionesU7.add(new Direccion(nextId(), "Oficina", "Calle 30 # 10-20", "Sur", "Armenia"));
        u7.setDirecciones(direccionesU7);
        u7.agregarMetodoPago(MetodoPago.EFECTIVO);
        usuarios.add(u7);

        System.out.println("✅ Usuarios cargados: " + usuarios.size() + 
                         " (" + usuarios.stream().filter(Usuario::isAdmin).count() + " administradores)");

        // ==================== REPARTIDORES (RF-018 a RF-019) ====================
        
        Repartidor r1 = new Repartidor(nextId(), "Juan Repartidor", "1000123456", "3001112233", "Norte", true);
        Repartidor r2 = new Repartidor(nextId(), "Laura Entregas", "1000123457", "3002223344", "Centro", true);
        Repartidor r3 = new Repartidor(nextId(), "Miguel Torres", "1000123458", "3003334455", "Sur", true);
        Repartidor r4 = new Repartidor(nextId(), "Sofía Pérez", "1000123459", "3004445566", "Norte", true);
        Repartidor r5 = new Repartidor(nextId(), "Camilo Ríos", "1000123460", "3005556677", "Centro", false); // No disponible
        Repartidor r6 = new Repartidor(nextId(), "Edgar Dávila", "1000123461", "3015538977", "Sur", true);
        Repartidor r7 = new Repartidor(nextId(), "Diana Morales", "1000123462", "3016667788", "Norte", true);

        addRepartidor(r1);
        addRepartidor(r2);
        addRepartidor(r3);
        addRepartidor(r4);
        addRepartidor(r5);
        addRepartidor(r6);
        addRepartidor(r7);

        System.out.println("✅ Repartidores cargados: " + repartidores.size() + 
                         " (" + repartidores.stream().filter(Repartidor::isDisponible).count() + " disponibles)");

        // ==================== TARIFAS (RF-029 a RF-031) ====================
        
        // Solo dos tarifas: Normal y Express
        Tarifa t1 = new Tarifa(nextId(), "Normal", 5000, 3000, 50000, 0.05);
        Tarifa t2 = new Tarifa(nextId(), "Express", 8000, 3500, 60000, 0.06);

        addTarifa(t1);
        addTarifa(t2);

        System.out.println("✅ Tarifas cargadas: " + tarifas.size());

        // ==================== ENVÍOS (RF-020 a RF-026) ====================
        
        // Envío ID:101 - ENTREGADO (Carlos a su trabajo) - RF-022: Con servicios adicionales
        Envio e1 = new EnvioBuilder()
                .id(101L)
                .usuario(u1)
                .origen(direccionesU1.get(0))
                .destino(direccionesU1.get(1))
                .peso(2.5)
                .volumen(0.15)
                .prioridad(true)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.ENTREGADO)
                .costoEstimado(t1.calcularCosto(2.5) + 3000) // Con recargos
                .repartidor(r1)
                .fechaCreacion(LocalDateTime.now().minusDays(5))
                .fechaConfirmacion(LocalDateTime.now().minusDays(4))
                .fechaEntregaEstimada(LocalDateTime.now().minusDays(1))
                .fechaEntrega(LocalDateTime.now().minusHours(6))
                .incidenciaDescripcion("Entrega exitosa sin novedades.")
                .build();
        addEnvio(e1);

        // Envío ID:102 - SOLICITADO (María - frágil) - RF-007: Servicio adicional frágil
        Envio e2 = new EnvioBuilder()
                .id(102L)
                .usuario(u3)
                .origen(direccionesU3.get(0))
                .destino(direccionesU3.get(1))
                .peso(1.2)
                .volumen(0.08)
                .prioridad(false)
                .seguro(true)
                .fragil(true)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.SOLICITADO)
                .costoEstimado(t2.calcularCosto(1.2) + 2000)
                .fechaCreacion(LocalDateTime.now().minusDays(2))
                .incidenciaDescripcion("Paquete frágil - Manejar con cuidado.")
                .build();
        addEnvio(e2);

        // Envío ID:103 - EN_RUTA (Pedro - envío urgente)
        Envio e3 = new EnvioBuilder()
                .id(103L)
                .usuario(u4)
                .origen(direccionesU4.get(0))
                .destino(direccionesU4.get(1))
                .peso(3.8)
                .volumen(0.25)
                .prioridad(true)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.EN_RUTA)
                .costoEstimado(t2.calcularCosto(3.8) + 4000)
                .repartidor(r3)
                .fechaCreacion(LocalDateTime.now().minusDays(3))
                .fechaConfirmacion(LocalDateTime.now().minusDays(2))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                .incidenciaDescripcion("En camino - Llegada estimada mañana.")
                .build();
        addEnvio(e3);

        // Envío ID:104 - ASIGNADO (Lucía)
        Envio e4 = new EnvioBuilder()
                .id(104L)
                .usuario(u5)
                .origen(direccionesU5.get(0))
                .destino(direccionesU5.get(1))
                .peso(0.9)
                .volumen(0.05)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.ASIGNADO)
                .costoEstimado(t1.calcularCosto(0.9))
                .repartidor(r2)
                .fechaCreacion(LocalDateTime.now().minusDays(1))
                .fechaConfirmacion(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(2))
                .incidenciaDescripcion("Entrega programada sin novedades.")
                .build();
        addEnvio(e4);

        // Envío ID:105 - INCIDENCIA (Pepito - problema de entrega) - RF-013: Incidencia
        Envio e5 = new EnvioBuilder()
                .id(105L)
                .usuario(u6)
                .origen(direccionesU6.get(0))
                .destino(direccionesU1.get(0))
                .peso(5.4)
                .volumen(0.35)
                .prioridad(true)
                .seguro(true)
                .fragil(true)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.INCIDENCIA)
                .costoEstimado(t1.calcularCosto(5.4) + 5000)
                .repartidor(r4)
                .fechaCreacion(LocalDateTime.now().minusDays(6))
                .fechaConfirmacion(LocalDateTime.now().minusDays(5))
                .fechaEntregaEstimada(LocalDateTime.now().minusDays(1))
                .incidenciaDescripcion("Retraso por mal clima en la ruta de entrega. Se reprogramará para mañana.")
                .build();
        e5.setFechaIncidencia(LocalDateTime.now().minusHours(12));
        addEnvio(e5);

        // Envío ID:106 - CONFIRMADO (Carlos - esperando asignación)
        Envio e6 = new EnvioBuilder()
                .id(106L)
                .usuario(u1)
                .origen(direccionesU1.get(1))
                .destino(direccionesU3.get(0))
                .peso(1.5)
                .volumen(0.10)
                .prioridad(false)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.CONFIRMADO)
                .costoEstimado(t1.calcularCosto(1.5) + 1500)
                .fechaCreacion(LocalDateTime.now().minusHours(8))
                .fechaConfirmacion(LocalDateTime.now().minusHours(7))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                .incidenciaDescripcion("Confirmado - Esperando asignación de repartidor.")
                .build();
        addEnvio(e6);

        // Envío ID:107 - CANCELADO (María - canceló el pedido) - RF-005: Cancelación
        Envio e7 = new EnvioBuilder()
                .id(107L)
                .usuario(u3)
                .origen(direccionesU3.get(1))
                .destino(direccionesU4.get(0))
                .peso(0.5)
                .volumen(0.03)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.CANCELADO)
                .costoEstimado(t1.calcularCosto(0.5))
                .fechaCreacion(LocalDateTime.now().minusDays(4))
                .incidenciaDescripcion("Cancelado por el usuario antes de la asignación.")
                .build();
        addEnvio(e7);

        // Envío ID:108 - SOLICITADO (Sofía - paquete simple)
        Envio e8 = new EnvioBuilder()
                .id(108L)
                .usuario(u7)
                .origen(direccionesU7.get(0))
                .destino(direccionesU2.get(0))
                .peso(0.8)
                .volumen(0.04)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.SOLICITADO)
                .costoEstimado(t1.calcularCosto(0.8))
                .fechaCreacion(LocalDateTime.now().minusHours(3))
                .incidenciaDescripcion("Envío económico estándar.")
                .build();
        addEnvio(e8);

        // Envío ID:109 - EN_RUTA (Pepito - segundo envío)
        Envio e9 = new EnvioBuilder()
                .id(109L)
                .usuario(u6)
                .origen(direccionesU6.get(1))
                .destino(direccionesU5.get(0))
                .peso(2.0)
                .volumen(0.12)
                .prioridad(true)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.EN_RUTA)
                .costoEstimado(t1.calcularCosto(2.0) + 2500)
                .repartidor(r6)
                .fechaCreacion(LocalDateTime.now().minusDays(1))
                .fechaConfirmacion(LocalDateTime.now().minusHours(20))
                .fechaEntregaEstimada(LocalDateTime.now().plusHours(6))
                .incidenciaDescripcion("En ruta - Entrega hoy por la tarde.")
                .build();
        addEnvio(e9);

        // Envío ID:110 - ENTREGADO (Lucía - otro envío completado)
        Envio e10 = new EnvioBuilder()
                .id(110L)
                .usuario(u5)
                .origen(direccionesU5.get(1))
                .destino(direccionesU4.get(1))
                .peso(1.3)
                .volumen(0.07)
                .prioridad(false)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.ENTREGADO)
                .costoEstimado(t1.calcularCosto(1.3) + 1500)
                .repartidor(r7)
                .fechaCreacion(LocalDateTime.now().minusDays(7))
                .fechaConfirmacion(LocalDateTime.now().minusDays(6))
                .fechaEntregaEstimada(LocalDateTime.now().minusDays(3))
                .fechaEntrega(LocalDateTime.now().minusDays(2))
                .incidenciaDescripcion("Entregado exitosamente en la tienda.")
                .build();
        addEnvio(e10);

        // ==================== ENVÍOS ADICIONALES PARA COMPLETAR 3 POR USUARIO ====================
        
        // Envío ID:111 - Tercer envío de Carlos (u1)
        Envio e11 = new EnvioBuilder()
                .id(111L)
                .usuario(u1)
                .origen(direccionesU1.get(0))
                .destino(direccionesU1.get(1))
                .peso(2.0)
                .volumen(0.12)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.SOLICITADO)
                .costoEstimado(t1.calcularCosto(2.0))
                .fechaCreacion(LocalDateTime.now().minusHours(2))
                .incidenciaDescripcion("Envío estándar en proceso.")
                .build();
        addEnvio(e11);

        // Envío ID:112 - Primer envío de Ana (u2)
        Envio e12 = new EnvioBuilder()
                .id(112L)
                .usuario(u2)
                .origen(direccionesU2.get(0))
                .destino(direccionesU2.get(1))
                .peso(1.8)
                .volumen(0.10)
                .prioridad(true)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.CONFIRMADO)
                .costoEstimado(t2.calcularCosto(1.8) + 2000)
                .fechaCreacion(LocalDateTime.now().minusDays(1))
                .fechaConfirmacion(LocalDateTime.now().minusHours(12))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(2))
                .incidenciaDescripcion("Envío administrativo confirmado.")
                .build();
        addEnvio(e12);

        // Envío ID:113 - Segundo envío de Ana (u2)
        Envio e13 = new EnvioBuilder()
                .id(113L)
                .usuario(u2)
                .origen(direccionesU2.get(1))
                .destino(direccionesU1.get(0))
                .peso(0.7)
                .volumen(0.04)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.ASIGNADO)
                .costoEstimado(t1.calcularCosto(0.7))
                .repartidor(r2)
                .fechaCreacion(LocalDateTime.now().minusDays(2))
                .fechaConfirmacion(LocalDateTime.now().minusDays(1))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                .incidenciaDescripcion("Asignado a repartidor.")
                .build();
        addEnvio(e13);

        // Envío ID:114 - Tercer envío de Ana (u2)
        Envio e14 = new EnvioBuilder()
                .id(114L)
                .usuario(u2)
                .origen(direccionesU2.get(0))
                .destino(direccionesU3.get(0))
                .peso(3.2)
                .volumen(0.20)
                .prioridad(false)
                .seguro(true)
                .fragil(true)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.EN_RUTA)
                .costoEstimado(t2.calcularCosto(3.2) + 3000)
                .repartidor(r3)
                .fechaCreacion(LocalDateTime.now().minusDays(3))
                .fechaConfirmacion(LocalDateTime.now().minusDays(2))
                .fechaEntregaEstimada(LocalDateTime.now().plusHours(8))
                .incidenciaDescripcion("En ruta - Paquete frágil.")
                .build();
        addEnvio(e14);

        // Envío ID:115 - Tercer envío de María (u3)
        Envio e15 = new EnvioBuilder()
                .id(115L)
                .usuario(u3)
                .origen(direccionesU3.get(1))
                .destino(direccionesU3.get(0))
                .peso(1.0)
                .volumen(0.06)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.SOLICITADO)
                .costoEstimado(t1.calcularCosto(1.0))
                .fechaCreacion(LocalDateTime.now().minusHours(5))
                .incidenciaDescripcion("Envío simple solicitado.")
                .build();
        addEnvio(e15);

        // Envío ID:116 - Segundo envío de Pedro (u4)
        Envio e16 = new EnvioBuilder()
                .id(116L)
                .usuario(u4)
                .origen(direccionesU4.get(1))
                .destino(direccionesU4.get(0))
                .peso(2.5)
                .volumen(0.15)
                .prioridad(true)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.CONFIRMADO)
                .costoEstimado(t2.calcularCosto(2.5) + 2500)
                .fechaCreacion(LocalDateTime.now().minusHours(10))
                .fechaConfirmacion(LocalDateTime.now().minusHours(8))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                .incidenciaDescripcion("Envío prioritario confirmado.")
                .build();
        addEnvio(e16);

        // Envío ID:117 - Tercer envío de Pedro (u4)
        Envio e17 = new EnvioBuilder()
                .id(117L)
                .usuario(u4)
                .origen(direccionesU4.get(0))
                .destino(direccionesU5.get(1))
                .peso(1.5)
                .volumen(0.09)
                .prioridad(false)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.ENTREGADO)
                .costoEstimado(t1.calcularCosto(1.5) + 1800)
                .repartidor(r1)
                .fechaCreacion(LocalDateTime.now().minusDays(4))
                .fechaConfirmacion(LocalDateTime.now().minusDays(3))
                .fechaEntregaEstimada(LocalDateTime.now().minusDays(1))
                .fechaEntrega(LocalDateTime.now().minusHours(12))
                .incidenciaDescripcion("Entregado con éxito.")
                .build();
        addEnvio(e17);

        // Envío ID:118 - Tercer envío de Lucía (u5)
        Envio e18 = new EnvioBuilder()
                .id(118L)
                .usuario(u5)
                .origen(direccionesU5.get(0))
                .destino(direccionesU5.get(1))
                .peso(0.6)
                .volumen(0.03)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.SOLICITADO)
                .costoEstimado(t1.calcularCosto(0.6))
                .fechaCreacion(LocalDateTime.now().minusHours(1))
                .incidenciaDescripcion("Envío ligero solicitado.")
                .build();
        addEnvio(e18);

        // Envío ID:119 - Tercer envío de Pepito (u6)
        Envio e19 = new EnvioBuilder()
                .id(119L)
                .usuario(u6)
                .origen(direccionesU6.get(0))
                .destino(direccionesU6.get(1))
                .peso(1.4)
                .volumen(0.08)
                .prioridad(false)
                .seguro(true)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.CONFIRMADO)
                .costoEstimado(t1.calcularCosto(1.4) + 1200)
                .fechaCreacion(LocalDateTime.now().minusHours(6))
                .fechaConfirmacion(LocalDateTime.now().minusHours(4))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(2))
                .incidenciaDescripcion("Envío confirmado con seguro.")
                .build();
        addEnvio(e19);

        // Envío ID:120 - Segundo envío de Sofía (u7)
        Envio e20 = new EnvioBuilder()
                .id(120L)
                .usuario(u7)
                .origen(direccionesU7.get(1))
                .destino(direccionesU7.get(0))
                .peso(2.2)
                .volumen(0.13)
                .prioridad(true)
                .seguro(false)
                .fragil(true)
                .firmaRequerida(true)
                .estado(Envio.EstadoEnvio.ASIGNADO)
                .costoEstimado(t2.calcularCosto(2.2) + 2800)
                .repartidor(r4)
                .fechaCreacion(LocalDateTime.now().minusDays(1))
                .fechaConfirmacion(LocalDateTime.now().minusHours(18))
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                .incidenciaDescripcion("Asignado - Paquete frágil prioritario.")
                .build();
        addEnvio(e20);

        // Envío ID:121 - Tercer envío de Sofía (u7)
        Envio e21 = new EnvioBuilder()
                .id(121L)
                .usuario(u7)
                .origen(direccionesU7.get(0))
                .destino(direccionesU2.get(0))
                .peso(0.9)
                .volumen(0.05)
                .prioridad(false)
                .seguro(false)
                .fragil(false)
                .firmaRequerida(false)
                .estado(Envio.EstadoEnvio.ENTREGADO)
                .costoEstimado(t1.calcularCosto(0.9))
                .repartidor(r5)
                .fechaCreacion(LocalDateTime.now().minusDays(5))
                .fechaConfirmacion(LocalDateTime.now().minusDays(4))
                .fechaEntregaEstimada(LocalDateTime.now().minusDays(2))
                .fechaEntrega(LocalDateTime.now().minusDays(1))
                .incidenciaDescripcion("Entregado correctamente.")
                .build();
        addEnvio(e21);

        System.out.println("✅ Envíos cargados: " + envios.size());
        System.out.println("   Estados: " + 
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.SOLICITADO).count() + " SOLICITADOS, " +
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.CONFIRMADO).count() + " CONFIRMADOS, " +
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.ASIGNADO).count() + " ASIGNADOS, " +
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.EN_RUTA).count() + " EN_RUTA, " +
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.ENTREGADO).count() + " ENTREGADOS, " +
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.INCIDENCIA).count() + " INCIDENCIAS, " +
                         envios.stream().filter(e -> e.getEstado() == Envio.EstadoEnvio.CANCELADO).count() + " CANCELADOS");

        // ==================== PAGOS (RF-032 a RF-035) ====================
        
        // Pago del envío 1 (Entregado) - TARJETA_CREDITO - CONFIRMADO
        Pago p1 = new Pago(nextId(), e1, e1.getCostoEstimado(), MetodoPago.TARJETA_CREDITO);
        p1.setFechaPago(e1.getFechaCreacion().plusHours(1));
        p1.setConfirmado(true);
        addPago(p1);

        // Pago del envío 2 (Solicitado - pendiente) - PSE - CONFIRMADO
        Pago p2 = new Pago(nextId(), e2, e2.getCostoEstimado() * 0.8, MetodoPago.PSE);
        p2.setFechaPago(e2.getFechaCreacion().plusMinutes(30));
        p2.setConfirmado(true);
        addPago(p2);

        // Pago del envío 3 (En ruta) - EFECTIVO - CONFIRMADO
        Pago p3 = new Pago(nextId(), e3, e3.getCostoEstimado(), MetodoPago.EFECTIVO);
        p3.setFechaPago(e3.getFechaCreacion().plusHours(2));
        p3.setConfirmado(true);
        addPago(p3);

        // Pago del envío 4 (Asignado) - TRANSFERENCIA - CONFIRMADO
        Pago p4 = new Pago(nextId(), e4, e4.getCostoEstimado(), MetodoPago.TRANSFERENCIA);
        p4.setFechaPago(e4.getFechaCreacion().plusMinutes(45));
        p4.setConfirmado(true);
        addPago(p4);

        // Pago del envío 5 (Incidencia) - TARJETA_CREDITO - CONFIRMADO
        Pago p5 = new Pago(nextId(), e5, e5.getCostoEstimado(), MetodoPago.TARJETA_CREDITO);
        p5.setFechaPago(e5.getFechaCreacion().plusHours(1));
        p5.setConfirmado(true);
        addPago(p5);

        // Pago del envío 6 (Confirmado) - PSE - CONFIRMADO
        Pago p6 = new Pago(nextId(), e6, e6.getCostoEstimado(), MetodoPago.PSE);
        p6.setFechaPago(e6.getFechaCreacion().plusMinutes(20));
        p6.setConfirmado(true);
        addPago(p6);

        // Pago del envío 9 (En ruta) - TRANSFERENCIA - CONFIRMADO
        Pago p9 = new Pago(nextId(), e9, e9.getCostoEstimado(), MetodoPago.TRANSFERENCIA);
        p9.setFechaPago(e9.getFechaCreacion().plusHours(1));
        p9.setConfirmado(true);
        addPago(p9);

        // Pago del envío 10 (Entregado) - EFECTIVO - CONFIRMADO
        Pago p10 = new Pago(nextId(), e10, e10.getCostoEstimado(), MetodoPago.EFECTIVO);
        p10.setFechaPago(e10.getFechaCreacion().plusMinutes(40));
        p10.setConfirmado(true);
        addPago(p10);

        // Pagos de los envíos adicionales
        Pago p11 = new Pago(nextId(), e11, e11.getCostoEstimado(), MetodoPago.PSE);
        p11.setFechaPago(e11.getFechaCreacion().plusMinutes(15));
        p11.setConfirmado(true);
        addPago(p11);

        Pago p12 = new Pago(nextId(), e12, e12.getCostoEstimado(), MetodoPago.TRANSFERENCIA);
        p12.setFechaPago(e12.getFechaCreacion().plusHours(1));
        p12.setConfirmado(true);
        addPago(p12);

        Pago p13 = new Pago(nextId(), e13, e13.getCostoEstimado(), MetodoPago.EFECTIVO);
        p13.setFechaPago(e13.getFechaCreacion().plusMinutes(30));
        p13.setConfirmado(true);
        addPago(p13);

        Pago p14 = new Pago(nextId(), e14, e14.getCostoEstimado(), MetodoPago.TRANSFERENCIA);
        p14.setFechaPago(e14.getFechaCreacion().plusHours(2));
        p14.setConfirmado(true);
        addPago(p14);

        Pago p15 = new Pago(nextId(), e15, e15.getCostoEstimado(), MetodoPago.PSE);
        p15.setFechaPago(e15.getFechaCreacion().plusMinutes(20));
        p15.setConfirmado(true);
        addPago(p15);

        Pago p16 = new Pago(nextId(), e16, e16.getCostoEstimado(), MetodoPago.TARJETA_CREDITO);
        p16.setFechaPago(e16.getFechaCreacion().plusHours(1));
        p16.setConfirmado(true);
        addPago(p16);

        Pago p17 = new Pago(nextId(), e17, e17.getCostoEstimado(), MetodoPago.TARJETA_CREDITO);
        p17.setFechaPago(e17.getFechaCreacion().plusHours(1));
        p17.setConfirmado(true);
        addPago(p17);

        Pago p18 = new Pago(nextId(), e18, e18.getCostoEstimado(), MetodoPago.EFECTIVO);
        p18.setFechaPago(e18.getFechaCreacion().plusMinutes(10));
        p18.setConfirmado(true);
        addPago(p18);

        Pago p19 = new Pago(nextId(), e19, e19.getCostoEstimado(), MetodoPago.PSE);
        p19.setFechaPago(e19.getFechaCreacion().plusMinutes(25));
        p19.setConfirmado(true);
        addPago(p19);

        Pago p20 = new Pago(nextId(), e20, e20.getCostoEstimado(), MetodoPago.TRANSFERENCIA);
        p20.setFechaPago(e20.getFechaCreacion().plusHours(1));
        p20.setConfirmado(true);
        addPago(p20);

        Pago p21 = new Pago(nextId(), e21, e21.getCostoEstimado(), MetodoPago.EFECTIVO);
        p21.setFechaPago(e21.getFechaCreacion().plusMinutes(35));
        p21.setConfirmado(true);
        addPago(p21);

        System.out.println("✅ Pagos cargados: " + pagos.size());
        System.out.println("   Confirmados: " + pagos.stream().filter(Pago::isConfirmado).count());
        System.out.println("   Monto total: $" + String.format("%,.0f", pagos.stream().mapToDouble(Pago::getMontoPagado).sum()));

        System.out.println("\n✅ ¡Datos de ejemplo cargados correctamente!");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   📊 Resumen del Sistema");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   👥 Usuarios: " + usuarios.size() + " (Admin: " + 
                         usuarios.stream().filter(Usuario::isAdmin).count() + ", Regular: " + 
                         usuarios.stream().filter(u -> !u.isAdmin()).count() + ")");
        System.out.println("   🚚 Repartidores: " + repartidores.size() + " (Disponibles: " + 
                         repartidores.stream().filter(Repartidor::isDisponible).count() + ")");
        System.out.println("   📦 Envíos: " + envios.size());
        System.out.println("   💰 Pagos: " + pagos.size());
        System.out.println("   💵 Tarifas: " + tarifas.size());
        System.out.println("═══════════════════════════════════════════════════════\n");
    }
}
