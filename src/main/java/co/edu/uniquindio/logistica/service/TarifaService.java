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

    /**
     * Compatibilidad: anterior calcularTarifa por peso.
     * Usa la primera tarifa registrada como base.
     */
    public double calcularTarifa(double peso) {
        Tarifa t = store.getTarifas().isEmpty() ? null : store.getTarifas().get(0);
        return (t != null) ? t.calcularCosto(peso) : 0;
    }

    /**
     * Calcula el costo total para un Envío considerando:
     * - tarifa base y costo por kilo (se toma la primera tarifa disponible como referencia)
     * - cargo por volumen (proporcional)
     * - recargo por zonas origen/destino (reglas especificadas)
     * - recargo por prioridad (express)
     * - seguro (servicio adicional)
     *
     * Devuelve solo el total. Si necesitas desglose, usa desglosarTarifa(envio).
     */
    public double calcularTarifa(Envio envio) {
        TarifaDetalle d = desglosarTarifa(envio);
        return d.getTotal();
    }

    /**
     * Retorna un desglose de componentes de la tarifa: base, porPeso, porVolumen,
     * recargoZona, recargoPrioridad, seguro y total.
     */
    public TarifaDetalle desglosarTarifa(Envio envio) {
        Tarifa t = store.getTarifas().isEmpty() ? null : store.getTarifas().get(0);

        double base = (t != null) ? t.getCostoBase() : 0.0;
        double porKilo = (t != null) ? t.getCostoPorKilo() : 0.0;

        double componentePeso = envio.getPeso() * porKilo;

        // Volumen: asumo que volumen está en cm3; convierto a "equivalente kilos" de forma simple:
        // volumenFactor = volumen / 1000 -> metros cúbicos aproximado => multiplicar por porKilo
        double componenteVolumen = envio.getVolumen() > 0 ? (envio.getVolumen() / 1000.0) * porKilo : 0.0;

        double subtotal = base + componentePeso + componenteVolumen;

        // Recargo por zonas según reglas:
        double recargoZona = calcularRecargoPorZona(envio.getOrigen(), envio.getDestino(), envio.getPeso(), porKilo);

        // Recargo por prioridad (ej: 20% del subtotal)
        double recargoPrioridad = envio.isPrioridad() ? subtotal * 0.20 : 0.0;

        // Seguro: ejemplo 2% del subtotal + recargos (puedes ajustar)
        double recargoSeguro = envio.isSeguro() ? (subtotal + recargoZona + recargoPrioridad) * 0.02 : 0.0;

        double total = subtotal + recargoZona + recargoPrioridad + recargoSeguro;

        // redondeo a 2 decimales
        total = Math.round(total * 100.0) / 100.0;

        return new TarifaDetalle(base, componentePeso, componenteVolumen, recargoZona, recargoPrioridad, recargoSeguro, total);
    }

    /**
     * Implementa las reglas de zona:
     * - si origen CENTRO y destino SUR o NORTE: +5% por kilo
     * - si origen SUR y destino CENTRO: +5% por kilo
     * - si origen SUR y destino NORTE: +10% por kilo
     * - viceversa para origen NORTE -> CENTRO 5%, NORTE->SUR 10%
     *
     * Este método devuelve un valor monetario (no porcentaje) calculado sobre el peso * porKilo.
     */
    private double calcularRecargoPorZona(co.edu.uniquindio.logistica.model.Direccion origen,
                                          co.edu.uniquindio.logistica.model.Direccion destino,
                                          double peso, double porKilo) {
        if (origen == null || destino == null) return 0.0;
        String o = origen.getCoordenadas() != null ? origen.getCoordenadas().toLowerCase() : origen.getCiudad() != null ? origen.getCiudad().toLowerCase() : "";
        String d = destino.getCoordenadas() != null ? destino.getCoordenadas().toLowerCase() : destino.getCiudad() != null ? destino.getCiudad().toLowerCase() : "";

        // Normalizar
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
        } else {
            // Si no coincide con etiquetas, intentar comparar exactas "sur", "centro", "norte"
            if (o.equals("centro") && (d.equals("sur") || d.equals("norte"))) recargoPercent = 0.05;
            if (o.equals("sur") && d.equals("centro")) recargoPercent = 0.05;
            if (o.equals("sur") && d.equals("norte")) recargoPercent = 0.10;
            if (o.equals("norte") && d.equals("centro")) recargoPercent = 0.05;
            if (o.equals("norte") && d.equals("sur")) recargoPercent = 0.10;
        }

        double recargoMonetario = peso * porKilo * recargoPercent;
        // redondeo
        return Math.round(recargoMonetario * 100.0) / 100.0;
    }

    // Clase para devolver desglose
    public static class TarifaDetalle {
        private final double base;
        private final double porPeso;
        private final double porVolumen;
        private final double recargoZona;
        private final double recargoPrioridad;
        private final double recargoSeguro;
        private final double total;

        public TarifaDetalle(double base, double porPeso, double porVolumen, double recargoZona, double recargoPrioridad, double recargoSeguro, double total) {
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
