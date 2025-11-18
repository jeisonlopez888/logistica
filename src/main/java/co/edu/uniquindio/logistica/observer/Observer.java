package co.edu.uniquindio.logistica.observer;

import co.edu.uniquindio.logistica.model.Envio;

/**
 * Interfaz Observer del patrón Observer
 * Define el metodo que será llamado cuando ocurra un evento
 */
public interface Observer {

    void notificar(Envio envio, TipoEvento tipoEvento, String mensaje);
}
