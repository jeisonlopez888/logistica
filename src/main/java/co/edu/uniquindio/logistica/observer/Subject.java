package co.edu.uniquindio.logistica.observer;

/**
 * Interfaz Subject del patrón Observer
 * Define los métodos para registrar y notificar observadores
 */
public interface Subject {

    void registrarObserver(Observer observer);


    void eliminarObserver(Observer observer);


    void notificarObservers(co.edu.uniquindio.logistica.model.Envio envio, TipoEvento tipoEvento, String mensaje);
}


