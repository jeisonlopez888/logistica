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
        // Buscar tarifa según el tipo especificado en el envío, o usar la primera disponible
        Tarifa t = null;
        if (envio.getTipoTarifa() != null && !envio.getTipoTarifa().isEmpty()) {
            t = obtenerTarifaPorDescripcion(envio.getTipoTarifa());
        }
        if (t == null) {
            t = store.getTarifas().isEmpty() ? null : store.getTarifas().get(0);
        }

        // Valores por defecto realistas si no hay tarifa configurada
        double base = (t != null && t.getCostoBase() > 0) ? t.getCostoBase() : 5000.0; // Mínimo 5,000 COP
        double porKilo = (t != null && t.getCostoPorKilo() > 0) ? t.getCostoPorKilo() : 3000.0; // 3,000 COP/kg
        double porVolumen = (t != null && t.getCostoPorVolumen() > 0) ? t.getCostoPorVolumen() : 50000.0; // 50,000 COP/m³
        double recargoSeguroConfig = (t != null && t.getRecargoSeguro() > 0) ? t.getRecargoSeguro() : 0.05; // 5% por defecto

        // --- COMPONENTES BASE ---
        double componentePeso = Math.max(envio.getPeso(), 0.1) * porKilo; // Mínimo 0.1 kg para evitar 0

        // Volumen: calcular si no está definido o es 0
        double volumenReal = envio.getVolumen();
        if (volumenReal <= 0 && envio.getPeso() > 0) {
            // Estimar volumen basado en peso (1 kg ≈ 0.01 m³ para la mayoría de paquetes)
            volumenReal = envio.getPeso() * 0.01;
        }
        // Mínimo 0.001 m³ (1 litro) para evitar 0
        volumenReal = Math.max(volumenReal, 0.001);
        
        double componenteVolumen = volumenReal * porVolumen;

        double subtotal = base + componentePeso + componenteVolumen;
        
        // Asegurar que subtotal nunca sea 0
        subtotal = Math.max(subtotal, base);

        // --- RECARGOS POR ZONA ---
        double recargoZona = calcularRecargoPorZona(envio.getOrigen(), envio.getDestino(), subtotal);
        
        // --- RECARGOS POR SERVICIOS ADICIONALES ---
        // Prioridad: 30% del subtotal (entrega express)
        double recargoPrioridad = envio.isPrioridad() ? subtotal * 0.30 : 0.0;

        // Seguro: 5% del subtotal (mínimo 2,000 COP)
        double recargoSeguro = 0.0;
        if (envio.isSeguro()) {
            if (recargoSeguroConfig > 1) {
                recargoSeguro = Math.max(recargoSeguroConfig, 2000.0); // Valor fijo mínimo 2,000
            } else {
                recargoSeguro = Math.max(subtotal * recargoSeguroConfig, 2000.0); // Porcentaje con mínimo
            }
        }

        // Frágil: 15% del subtotal (manejo especial)
        double recargoFragil = envio.isFragil() ? subtotal * 0.15 : 0.0;

        // Firma requerida: 10% del subtotal (confirmación de entrega)
        double recargoFirma = envio.isFirmaRequerida() ? subtotal * 0.10 : 0.0;

        // Calcular total con todos los recargos
        double total = subtotal + recargoZona + recargoPrioridad + recargoSeguro + recargoFragil + recargoFirma;
        
        // Redondear a 2 decimales
        total = Math.round(total * 100.0) / 100.0;

        return new TarifaDetalle(
                base, 
                componentePeso, 
                componenteVolumen,
                recargoZona, 
                recargoPrioridad, 
                recargoSeguro,
                recargoFragil,
                recargoFirma,
                total
        );
    }

    private double calcularRecargoPorZona(co.edu.uniquindio.logistica.model.Direccion origen,
                                          co.edu.uniquindio.logistica.model.Direccion destino,
                                          double subtotal) {
        if (origen == null || destino == null) return 0.0;
        
        // Usar ciudad (zona: Norte, Centro, Sur) en lugar de coordenadas
        String zonaOrigen = origen.getCiudad() != null ? origen.getCiudad().toLowerCase().trim() : "";
        String zonaDestino = destino.getCiudad() != null ? destino.getCiudad().toLowerCase().trim() : "";
        
        // Si ciudad está vacía, intentar con coordenadas como fallback
        if (zonaOrigen.isEmpty()) {
            zonaOrigen = origen.getCoordenadas() != null ? origen.getCoordenadas().toLowerCase().trim() : "";
        }
        if (zonaDestino.isEmpty()) {
            zonaDestino = destino.getCoordenadas() != null ? destino.getCoordenadas().toLowerCase().trim() : "";
        }

        // Si ambas zonas son iguales, no hay recargo
        if (zonaOrigen.equals(zonaDestino)) {
            return 0.0;
        }

        double recargoPercent = 0.0;

        // Calcular recargo según distancia entre zonas
        if (zonaOrigen.contains("centro")) {
            if (zonaDestino.contains("sur") || zonaDestino.contains("norte")) {
                recargoPercent = 0.08; // 8% para zonas contiguas desde centro
            }
        } else if (zonaOrigen.contains("sur")) {
            if (zonaDestino.contains("centro")) {
                recargoPercent = 0.08; // 8% para zonas contiguas
            } else if (zonaDestino.contains("norte")) {
                recargoPercent = 0.15; // 15% para zona opuesta
            }
        } else if (zonaOrigen.contains("norte")) {
            if (zonaDestino.contains("centro")) {
                recargoPercent = 0.08; // 8% para zonas contiguas
            } else if (zonaDestino.contains("sur")) {
                recargoPercent = 0.15; // 15% para zona opuesta
            }
        }

        // Calcular recargo monetario basado en subtotal (no en peso)
        double recargoMonetario = subtotal * recargoPercent;
        
        // Mínimo 1,000 COP de recargo por zona si aplica
        if (recargoPercent > 0) {
            recargoMonetario = Math.max(recargoMonetario, 1000.0);
        }
        
        return Math.round(recargoMonetario * 100.0) / 100.0;
    }

    public static class TarifaDetalle {
        private final double base;
        private final double porPeso;
        private final double porVolumen;
        private final double recargoZona;
        private final double recargoPrioridad;
        private final double recargoSeguro;
        private final double recargoFragil;
        private final double recargoFirma;
        private final double total;

        public TarifaDetalle(double base, double porPeso, double porVolumen,
                             double recargoZona, double recargoPrioridad, double recargoSeguro,
                             double recargoFragil, double recargoFirma, double total) {
            this.base = base;
            this.porPeso = porPeso;
            this.porVolumen = porVolumen;
            this.recargoZona = recargoZona;
            this.recargoPrioridad = recargoPrioridad;
            this.recargoSeguro = recargoSeguro;
            this.recargoFragil = recargoFragil;
            this.recargoFirma = recargoFirma;
            this.total = total;
        }

        public double getBase() { return base; }
        public double getPorPeso() { return porPeso; }
        public double getPorVolumen() { return porVolumen; }
        public double getRecargoZona() { return recargoZona; }
        public double getRecargoPrioridad() { return recargoPrioridad; }
        public double getRecargoSeguro() { return recargoSeguro; }
        public double getRecargoFragil() { return recargoFragil; }
        public double getRecargoFirma() { return recargoFirma; }
        public double getTotal() { return total; }
    }
}
