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

    // ---------------- IDs ----------------
    public long nextId() {
        return seq.getAndIncrement();
    }

    // ---------------- Getters ----------------
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Envio> getEnvios() { return envios; }
    public List<Repartidor> getRepartidores() { return repartidores; }
    public List<Tarifa> getTarifas() { return tarifas; }
    public List<Pago> getPagos() { return pagos; }

    // ---------------- Métodos de agregado ----------------
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
        if (p.getId() == null) {
            p.setId(nextId());
        }
        pagos.add(p);
    }

    // ---------------- Datos de ejemplo ----------------
    private void initSampleData() {
        // --- Usuarios ---
        Usuario u1 = new Usuario(nextId(), "Carlos Pérez", "carlos@uniquindio.edu", "3101234567", "12345", false);
        Usuario u2 = new Usuario(nextId(), "Ana López", "ana@uniquindio.edu", "3109876543", "abcd", true);
        usuarios.add(u1);
        usuarios.add(u2);

        // --- Direcciones ---
        Direccion d1 = new Direccion(nextId(), "Casa", "Calle 10 # 5-20", "Armenia", "4.53,-75.68");
        Direccion d2 = new Direccion(nextId(), "Trabajo", "Cra 14 # 2-30", "Armenia", "4.54,-75.68");
        // asociar a usuarios como direcciones frecuentes
        u1.getDirecciones().add(d1);
        u1.getDirecciones().add(d2);
        u2.getDirecciones().add(new Direccion(nextId(), "Bodega", "Av 30 # 15-80", "Pereira", "4.81,-75.69"));

// --- Envíos ---
        Envio e1 = new EnvioBuilder()
                .usuario(u1)
                .origen(d1)
                .destino(d2)
                .peso(2.5)
                .estado(Envio.EstadoEnvio.ENTREGADO) // ✅ Enum, no String
                .build();

        Envio e2 = new EnvioBuilder()
                .usuario(u2)
                .origen(d2)
                .destino(d1)
                .peso(1.2)
                .estado(Envio.EstadoEnvio.PENDIENTE) // ✅ Enum, no String
                .build();

        addEnvio(e1);
        addEnvio(e2);

        // --- Repartidores ---
        Repartidor r1 = new Repartidor(nextId(), "Juan Repartidor", "3001112233", "Norte", true);
        Repartidor r2 = new Repartidor(nextId(), "Laura Entregas", "3002223344", "Centro", false);
        addRepartidor(r1);
        addRepartidor(r2);

        // --- Tarifas ---
        Tarifa t1 = new Tarifa(nextId(), "Tarifa Nacional", 5000, 1500);
        Tarifa t2 = new Tarifa(nextId(), "Tarifa Express", 8000, 2500);
        addTarifa(t1);
        addTarifa(t2);

        // --- Pagos ---
        Pago p1 = new Pago(nextId(), e1, t1.calcularCosto(e1.getPeso()), t1.calcularCosto(e1.getPeso()), true);
        Pago p2 = new Pago(nextId(), e2, t2.calcularCosto(e2.getPeso()), t2.calcularCosto(e2.getPeso()), false);
        addPago(p1);
        addPago(p2);
    }
}
