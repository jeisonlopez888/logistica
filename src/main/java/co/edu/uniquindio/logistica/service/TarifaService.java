package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Tarifa;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;

public class TarifaService {

    private final DataStore store = DataStore.getInstance();

    public List<Tarifa> listarTarifas() {
        return store.getTarifas();
    }

    public Tarifa obtenerTarifaPorDescripcion(String descripcion) {
        return store.getTarifas().stream()
                .filter(t -> t.getDescripcion().equalsIgnoreCase(descripcion))
                .findFirst()
                .orElse(null);
    }

    public double calcularTarifa(double peso) {
        // Por defecto usa la primera tarifa
        Tarifa t = store.getTarifas().isEmpty() ? null : store.getTarifas().get(0);
        return (t != null) ? t.calcularCosto(peso) : 0;
    }
}

