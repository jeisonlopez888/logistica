package co.edu.uniquindio.logistica.observer;

import co.edu.uniquindio.logistica.model.Envio;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta de Subject para eventos de envíos
 * Gestiona la lista de observadores y notifica eventos
 */
public class EnvioSubject implements Subject {

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registrarObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void eliminarObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notificarObservers(Envio envio, TipoEvento tipoEvento, String mensaje) {
        for (Observer observer : observers) {
            observer.notificar(envio, tipoEvento, mensaje);
        }
    }


    public void notificarCreacionEnvio(Envio envio) {
        String mensaje = String.format("Se ha creado un nuevo envío #%d para el usuario %s",
                envio.getId(),
                envio.getUsuario() != null ? envio.getUsuario().getNombre() : "N/A");
        notificarObservers(envio, TipoEvento.CREACION_ENVIO, mensaje);
    }


    public void notificarCambioEstado(Envio envio, String estadoAnterior, String estadoNuevo) {
        String mensaje = String.format("El envío #%d ha cambiado de estado: %s → %s",
                envio.getId(), estadoAnterior, estadoNuevo);
        notificarObservers(envio, TipoEvento.CAMBIO_ESTADO, mensaje);
    }


    public void notificarAsignacionRepartidor(Envio envio) {
        String mensaje = String.format("Se ha asignado el repartidor %s al envío #%d",
                envio.getRepartidor() != null ? envio.getRepartidor().getNombre() : "N/A",
                envio.getId());
        notificarObservers(envio, TipoEvento.ASIGNACION_REPARTIDOR, mensaje);
    }
}


