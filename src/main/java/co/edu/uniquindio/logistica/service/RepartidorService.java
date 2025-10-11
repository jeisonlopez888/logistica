package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Repartidor;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;
import java.util.stream.Collectors;

public class RepartidorService {

    private final DataStore store = DataStore.getInstance();

    // 🔹 Listar todos
    public List<Repartidor> listarTodos() {
        return store.getRepartidores();
    }

    // 🔹 Listar solo los disponibles
    public List<Repartidor> listarDisponibles() {
        return store.getRepartidores().stream()
                .filter(Repartidor::isDisponible)
                .collect(Collectors.toList());
    }

    // 🔹 Buscar repartidor por zona
    public Repartidor buscarDisponiblePorZona(String zona) {
        if (zona == null) return null;

        final String zonaFinal = zona.toLowerCase();

        return store.getRepartidores().stream()
                .filter(Repartidor::isDisponible)
                .filter(r -> r.getZona() != null &&
                        (r.getZona().equalsIgnoreCase(zonaFinal) || r.getZona().toLowerCase().contains(zonaFinal)))
                .findFirst()
                .orElse(null);
    }


    // 🔹 Marcar repartidor como no disponible
    public void marcarNoDisponible(Repartidor repartidor) {
        if (repartidor == null) return;
        store.getRepartidores().removeIf(r -> r.getId().equals(repartidor.getId()));
        store.addRepartidor(new Repartidor(repartidor.getId(),
                repartidor.getNombre(), repartidor.getTelefono(),
                repartidor.getZona(), false));
    }

    // 🔹 Marcar repartidor como disponible
    public void marcarDisponible(Repartidor repartidor) {
        if (repartidor == null) return;
        store.getRepartidores().removeIf(r -> r.getId().equals(repartidor.getId()));
        store.addRepartidor(new Repartidor(repartidor.getId(),
                repartidor.getNombre(), repartidor.getTelefono(),
                repartidor.getZona(), true));
    }
}

