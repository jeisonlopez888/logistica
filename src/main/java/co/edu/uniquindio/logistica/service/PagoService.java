package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;

public class PagoService {

    private final DataStore store = DataStore.getInstance();
    private final EnvioService envioService = new EnvioService();

    // Registrar pago simulado (no confirmado por defecto)
    public Pago registrarPagoEnvio(Envio envio, double montoPagado, MetodoPago metodo) {
        Pago pago = new Pago(store.nextId(), envio, montoPagado, metodo);
        // No confirmamos aquí: la confirmación la hace el flujo de pago (facade / UI)
        store.addPago(pago);
        return pago;
    }

    // Listar todos los pagos
    public List<Pago> listarPagos() {
        return store.getPagos();
    }

    // Eliminar pago
    public void eliminarPago(Pago pago) {
        store.getPagos().remove(pago);
    }

    // Buscar pago por envío
    public Pago buscarPagoPorEnvio(Envio envio) {
        return store.getPagos().stream()
                .filter(p -> p.getEnvio() != null && p.getEnvio().equals(envio))
                .findFirst()
                .orElse(null);
    }

    public Pago buscarPagoPorId(Long id) {
        return store.getPagos().stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String confirmarPago(Long pagoId) {
        Pago pago = buscarPagoPorId(pagoId);
        if (pago == null) return "❌ Pago no encontrado.";
        pago.setConfirmado(true);
        Envio envio = pago.getEnvio();
        if (envio != null) {
            return envioService.confirmarPago(envio);
        }
        return "⚠️ El pago no tiene envío asociado.";
    }

}
