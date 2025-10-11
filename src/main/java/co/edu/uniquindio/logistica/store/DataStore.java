package co.edu.uniquindio.logistica.store;

import co.edu.uniquindio.logistica.model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class DataStore {

    private static DataStore instance;
    private final AtomicLong seq = new AtomicLong(1);

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

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Envio> getEnvios() { return envios; }
    public List<Repartidor> getRepartidores() { return repartidores; }
    public List<Tarifa> getTarifas() { return tarifas; }
    public List<Pago> getPagos() { return pagos; }

    public void addUsuario(Usuario u) { usuarios.add(u); }

    public void addEnvio(Envio envio) {
        if (envio.getId() == null) {
            envio.setId(nextId());
        }
        envios.add(envio);
    }

    public void addRepartidor(Repartidor r) {
        if (r.getId() == null) {
            r = new Repartidor(nextId(), r.getNombre(), r.getTelefono(), r.getZona(), r.isDisponible());
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


    private void initSampleData() {
        // Datos de ejemplo (reducidos para brevedad y coherencia)
        // --- Usuarios ---
        Usuario u1 = new Usuario(nextId(), "Carlos Pérez", "carlos@uniquindio.edu", "3101234567", "12345", false);
        Usuario u2 = new Usuario(nextId(), "Ana López", "ana@uniquindio.edu", "3109876543", "abcd", true);
        Usuario u3 = new Usuario(nextId(), "María Gómez", "maria@uniquindio.edu", "3115558899", "maria123", false);
        Usuario u4 = new Usuario(nextId(), "Pedro Sánchez", "pedro@uniquindio.edu", "3124445566", "pedro456", true);
        Usuario u5 = new Usuario(nextId(), "Lucía Ramírez", "lucia@uniquindio.edu", "3132223344", "lucia789", false);

        usuarios.add(u1);
        usuarios.add(u2);
        usuarios.add(u3);
        usuarios.add(u4);
        usuarios.add(u5);

        // --- Direcciones ---
        Direccion d1 = new Direccion(nextId(), "Casa", "Calle 10 # 5-20", "Armenia", "4.53,-75.68");
        Direccion d2 = new Direccion(nextId(), "Trabajo", "Cra 14 # 2-30", "Armenia", "4.54,-75.68");
        Direccion d3 = new Direccion(nextId(), "Casa", "Carrera 15 # 8-90", "Pereira", "4.81,-75.69");
        Direccion d4 = new Direccion(nextId(), "Oficina", "Av. Bolívar # 20-15", "Cali", "3.45,-76.53");
        Direccion d5 = new Direccion(nextId(), "Apartamento", "Calle 45 # 12-60", "Bogotá", "4.65,-74.09");
        Direccion d6 = new Direccion(nextId(), "Casa", "Carrera 7 # 30-20", "Medellín", "6.24,-75.57");
        Direccion d7 = new Direccion(nextId(), "Tienda", "Calle 23 # 15-40", "Manizales", "5.07,-75.52");
        Direccion d8 = new Direccion(nextId(), "Oficina", "Av. 6N # 34-90", "Cali", "3.46,-76.53");
        Direccion d9 = new Direccion(nextId(), "Casa", "Calle 12 # 18-45", "Armenia", "4.53,-75.67");
        Direccion d10 = new Direccion(nextId(), "Trabajo", "Cra 20 # 10-33", "Pereira", "4.81,-75.70");

        // --- Envíos ---
        Envio e1 = new EnvioBuilder()
                .usuario(u1)
                .origen(d1)
                .destino(d2)
                .peso(2.5)
                .estado(Envio.EstadoEnvio.ENTREGADO)
                .build();

        Envio e2 = new EnvioBuilder()
                .usuario(u2)
                .origen(d3)
                .destino(d4)
                .peso(1.2)
                .estado(Envio.EstadoEnvio.PENDIENTE)
                .build();

        Envio e3 = new EnvioBuilder()
                .usuario(u3)
                .origen(d5)
                .destino(d6)
                .peso(3.8)
                .estado(Envio.EstadoEnvio.EN_CAMINO)
                .build();

        Envio e4 = new EnvioBuilder()
                .usuario(u4)
                .origen(d7)
                .destino(d8)
                .peso(0.9)
                .estado(Envio.EstadoEnvio.PENDIENTE)
                .build();

        Envio e5 = new EnvioBuilder()
                .usuario(u5)
                .origen(d9)
                .destino(d10)
                .peso(5.4)
                .estado(Envio.EstadoEnvio.ENTREGADO)
                .build();

        addEnvio(e1);
        addEnvio(e2);
        addEnvio(e3);
        addEnvio(e4);
        addEnvio(e5);

        // --- Repartidores ---
        Repartidor r1 = new Repartidor(nextId(), "Juan Repartidor", "3001112233", "Norte", true);
        Repartidor r2 = new Repartidor(nextId(), "Laura Entregas", "3002223344", "Centro", false);
        Repartidor r3 = new Repartidor(nextId(), "Miguel Torres", "3003334455", "Sur", true);
        Repartidor r4 = new Repartidor(nextId(), "Sofía Pérez", "3004445566", "Occidente", true);
        Repartidor r5 = new Repartidor(nextId(), "Camilo Ríos", "3005556677", "Oriente", false);

        addRepartidor(r1);
        addRepartidor(r2);
        addRepartidor(r3);
        addRepartidor(r4);
        addRepartidor(r5);

        // --- Tarifas ---
        Tarifa t1 = new Tarifa(nextId(), "Tarifa Urbana", 5000, 1500);
        Tarifa t2 = new Tarifa(nextId(), "Tarifa Express", 8000, 2500);
        Tarifa t3 = new Tarifa(nextId(), "Tarifa Nacional", 12000, 3000);
        Tarifa t4 = new Tarifa(nextId(), "Tarifa Regional", 7000, 1800);
        Tarifa t5 = new Tarifa(nextId(), "Tarifa Económica", 4000, 1200);

        addTarifa(t1);
        addTarifa(t2);
        addTarifa(t3);
        addTarifa(t4);
        addTarifa(t5);

        // --- Pagos ---
        // Usamos el nuevo constructor Pago(Long, Envio, double, MetodoPago)
        Pago p1 = new Pago(nextId(), e1,
                t1.calcularCosto(e1.getPeso()), MetodoPago.TARJETA_CREDITO);
        Pago p2 = new Pago(nextId(), e2,
                t2.calcularCosto(e2.getPeso()), MetodoPago.PSE);
        Pago p3 = new Pago(nextId(), e3,
                t3.calcularCosto(e3.getPeso()), MetodoPago.EFECTIVO);
        Pago p4 = new Pago(nextId(), e4,
                t4.calcularCosto(e4.getPeso()), MetodoPago.TRANSFERENCIA);
        Pago p5 = new Pago(nextId(), e5,
                t5.calcularCosto(e5.getPeso()), MetodoPago.TARJETA_CREDITO);

        addPago(p1);
        addPago(p2);
        addPago(p3);
        addPago(p4);
        addPago(p5);
    }
}
