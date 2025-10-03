package co.edu.uniquindio.logistica.service;

public class TarifaService {

    // Tarifa base
    private static final double TARIFA_BASE = 5000.0;
    private static final double COSTO_POR_KILO = 2000.0;

    public double calcularTarifa(double peso) {
        return TARIFA_BASE + (peso * COSTO_POR_KILO);
    }
}

