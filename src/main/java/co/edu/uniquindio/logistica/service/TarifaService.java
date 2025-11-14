package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Envio;
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
        Tarifa t = store.getTarifas().isEmpty() ? null : store.getTarifas().get(0);
        return (t != null) ? t.calcularCosto(peso) : 0;
    }

    public double calcularTarifa(Envio envio) {
        TarifaDetalle d = desglosarTarifa(envio);
        return d.getTotal();
    }

    public TarifaDetalle desglosarTarifa(Envio envio) {
        Tarifa t = store.getTarifas().isEmpty() ? null : store.getTarifas().get(0);

        double base = (t != null) ? t.getCostoBase() : 0.0;
        double porKilo = (t != null) ? t.getCostoPorKilo() : 0.0;
        double porVolumen = (t != null) ? t.getCostoPorVolumen() : 0.0;
        double recargoSeguroConfig = (t != null) ? t.getRecargoSeguro() : 0.0;

        // --- COMPONENTES ---
        double componentePeso = envio.getPeso() * porKilo;

        // Volumen (convertido a m³)
        double componenteVolumen = (envio.getVolumen() > 0)
                ? envio.getVolumen() * porVolumen
                : 0.0;

        double subtotal = base + componentePeso + componenteVolumen;

        // --- RECARGOS ---
        double recargoZona = calcularRecargoPorZona(envio.getOrigen(), envio.getDestino(), envio.getPeso(), porKilo);
        double recargoPrioridad = envio.isPrioridad() ? subtotal * 0.25 : 0.0;

        // Recargo por seguro configurable
        double recargoSeguro;
        if (envio.isSeguro()) {
            if (recargoSeguroConfig > 1) {
                recargoSeguro = recargoSeguroConfig;
            } else {
                recargoSeguro = subtotal * recargoSeguroConfig;
            }
        } else {
            recargoSeguro = 0.0;
        }

        double total = subtotal + recargoZona + recargoPrioridad + recargoSeguro;
        total = Math.round(total * 100.0) / 100.0;

        return new TarifaDetalle(base, componentePeso, componenteVolumen,
                recargoZona, recargoPrioridad, recargoSeguro, total);
    }

    private double calcularRecargoPorZona(co.edu.uniquindio.logistica.model.Direccion origen,
                                          co.edu.uniquindio.logistica.model.Direccion destino,
                                          double peso, double porKilo) {
        if (origen == null || destino == null) return 0.0;
        String o = origen.getCoordenadas() != null ? origen.getCoordenadas().toLowerCase() : "";
        String d = destino.getCoordenadas() != null ? destino.getCoordenadas().toLowerCase() : "";

        o = o.trim();
        d = d.trim();

        double recargoPercent = 0.0;

        if (o.contains("centro")) {
            if (d.contains("sur") || d.contains("norte")) recargoPercent = 0.05;
        } else if (o.contains("sur")) {
            if (d.contains("centro")) recargoPercent = 0.05;
            else if (d.contains("norte")) recargoPercent = 0.10;
        } else if (o.contains("norte")) {
            if (d.contains("centro")) recargoPercent = 0.05;
            else if (d.contains("sur")) recargoPercent = 0.10;
        }

        double recargoMonetario = peso * porKilo * recargoPercent;
        return Math.round(recargoMonetario * 100.0) / 100.0;
    }

    public static class TarifaDetalle {
        private final double base;
        private final double porPeso;
        private final double porVolumen;
        private final double recargoZona;
        private final double recargoPrioridad;
        private final double recargoSeguro;
        private final double total;

        public TarifaDetalle(double base, double porPeso, double porVolumen,
                             double recargoZona, double recargoPrioridad, double recargoSeguro, double total) {
            this.base = base;
            this.porPeso = porPeso;
            this.porVolumen = porVolumen;
            this.recargoZona = recargoZona;
            this.recargoPrioridad = recargoPrioridad;
            this.recargoSeguro = recargoSeguro;
            this.total = total;
        }

        public double getBase() { return base; }
        public double getPorPeso() { return porPeso; }
        public double getPorVolumen() { return porVolumen; }
        public double getRecargoZona() { return recargoZona; }
        public double getRecargoPrioridad() { return recargoPrioridad; }
        public double getRecargoSeguro() { return recargoSeguro; }
        public double getTotal() { return total; }
    }
}
