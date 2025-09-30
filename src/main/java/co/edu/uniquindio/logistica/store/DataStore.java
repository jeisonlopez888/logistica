package co.edu.uniquindio.logistica.store;

import co.edu.uniquindio.logistica.model.*;
        import java.util.*;
        import java.util.concurrent.atomic.AtomicLong;

public class DataStore {

    private static DataStore instance;

    private final Map<Long, Usuario> usuarios = new HashMap<>();
    private final Map<Long, Repartidor> repartidores = new HashMap<>();
    private final Map<Long, Envio> envios = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    private DataStore() {
        initSampleData(); // datos de prueba
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public long nextId() {
        return sequence.getAndIncrement();
    }

    public Map<Long, Usuario> getUsuarios() { return usuarios; }
    public Map<Long, Repartidor> getRepartidores() { return repartidores; }
    public Map<Long, Envio> getEnvios() { return envios; }

    private void initSampleData() {
        // Crear usuarios de prueba
        Usuario u1 = new Usuario(nextId(), "Carlos Pérez", "carlos@uniquindio.edu", "12345");
        Usuario u2 = new Usuario(nextId(), "Ana López", "ana@uniquindio.edu", "67890");

        Direccion d1 = new Direccion(nextId(), "Casa", "Calle 10", "Armenia", "4.5339,-75.6811");
        Direccion d2 = new Direccion(nextId(), "Trabajo", "Cra 14", "Armenia", "4.5340,-75.6820");

        u1.getDirecciones().add(d1);
        u2.getDirecciones().add(d2);

        usuarios.put(u1.getId(), u1);
        usuarios.put(u2.getId(), u2);

        // Crear repartidores de prueba
        Repartidor r1 = new Repartidor(nextId(), "Juan Repartidor", "12345678", "3001112233", "Norte", true);
        Repartidor r2 = new Repartidor(nextId(), "María Repartidora", "87654321", "3012223344", "Sur", true);

        repartidores.put(r1.getId(), r1);
        repartidores.put(r2.getId(), r2);
    }
}

