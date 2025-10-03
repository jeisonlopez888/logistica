package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Envio;

public class PagoService {

    public boolean registrarPago(Envio envio, double montoPagado, double montoCalculado) {
        return Double.compare(montoPagado, montoCalculado) >= 0;
    }
}
