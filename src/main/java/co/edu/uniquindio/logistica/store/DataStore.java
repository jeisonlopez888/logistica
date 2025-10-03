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

    private DataStore() {
        initSampleData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public long nextId() { return seq.getAndIncrement(); }

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Envio> getEnvios() { return envios; }
    public List<Repartidor> getRepartidores() { return repartidores; }

    public void addUsuario(Usuario u) { usuarios.add(u); }

    public void addEnvio(Envio envio) {
        if (envio.getId() == null) {
            envio.setId(nextId()); // nextId() devuelve long → autobox a Long
        }
        envios.add(envio);
    }

    public void addRepartidor(Repartidor r) { repartidores.add(r); }

    private void initSampleData() {
        // Ahora los usuarios tienen: id, nombre, email, telefono, password, admin
        Usuario u1 = new Usuario(nextId(), "Carlos Pérez", "carlos@uniquindio.edu", "3101234567", "12345", false);
        Usuario u2 = new Usuario(nextId(), "Ana López", "ana@uniquindio.edu", "3109876543", "abcd", true);
        usuarios.add(u1);
        usuarios.add(u2);

        Direccion d1 = new Direccion(nextId(), "Casa", "Calle 10 # 5-20", "Armenia", "4.53,-75.68");
        Direccion d2 = new Direccion(nextId(), "Trabajo", "Cra 14 # 2-30", "Armenia", "4.54,-75.68");

        Envio e1 = new EnvioBuilder()
                .usuario(u1)
                .origen(d1)
                .destino(d2)
                .peso(2.5)
                .build();

        Envio e2 = new EnvioBuilder()
                .usuario(u2)
                .origen(d2)
                .destino(d1)
                .peso(1.2)
                .build();

        envios.add(e1);
        envios.add(e2);

        Repartidor r1 = new Repartidor(nextId(), "Juan Repartidor", "3001112233", "Norte", true);
        addRepartidor(r1);
    }
}
