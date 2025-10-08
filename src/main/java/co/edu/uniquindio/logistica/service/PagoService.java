package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Pago;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;

public class PagoService {

    private final DataStore store = DataStore.getInstance();

    public void registrarPago(Pago pago) {
        store.addPago(pago);
    }

    public List<Pago> listarPagos() {
        return store.getPagos();
    }

    /**
     * Registra un pago para un envío.
     * @param envio envío asociado
     * @param montoPagado monto abonado por el usuario
     * @param montoCalculado monto total según la tarifa
     */
    public Pago registrarPagoEnvio(Envio envio, double montoPagado, double montoCalculado) {
        Pago pago = new Pago(store.nextId(), envio, montoPagado, montoCalculado, montoPagado >= montoCalculado);
        store.addPago(pago);
        return pago;
    }
}


