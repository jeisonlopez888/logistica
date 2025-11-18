package co.edu.uniquindio.logistica.observer;

/**
 * Enum que define los tipos de eventos que pueden ocurrir en el sistema
 */
public enum TipoEvento {
    CREACION_ENVIO("Creación de Envío"),
    CAMBIO_ESTADO("Cambio de Estado"),
    ASIGNACION_REPARTIDOR("Asignación de Repartidor"),
    INCIDENCIA("Incidencia Registrada"),
    ENTREGA_COMPLETADA("Entrega Completada");

    private final String descripcion;

    TipoEvento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}


