package co.edu.uniquindio.logistica.model;

import java.time.LocalDateTime;

public class EnvioBuilder {
    private final Envio envio;

    public EnvioBuilder() {
        envio = new Envio();
        envio.setEstado(Envio.EstadoEnvio.SOLICITADO);
        envio.setFechaCreacion(LocalDateTime.now());
    }

    public EnvioBuilder id(Long id) {
        envio.setId(id);
        return this;
    }

    public EnvioBuilder usuario(Usuario usuario) {
        envio.setUsuario(usuario);
        return this;
    }

    public EnvioBuilder origen(Direccion origen) {
        envio.setOrigen(origen);
        return this;
    }

    public EnvioBuilder destino(Direccion destino) {
        envio.setDestino(destino);
        return this;
    }

    public EnvioBuilder peso(double peso) {
        envio.setPeso(peso);
        return this;
    }

    public EnvioBuilder volumen(double volumen) {
        envio.setVolumen(volumen);
        return this;
    }

    public EnvioBuilder prioridad(boolean prioridad) {
        envio.setPrioridad(prioridad);
        return this;
    }

    public EnvioBuilder seguro(boolean seguro) {
        envio.setSeguro(seguro);
        return this;
    }

    public EnvioBuilder fragil(boolean fragil) {
        envio.setFragil(fragil);
        return this;
    }

    public EnvioBuilder firmaRequerida(boolean firmaRequerida) {
        envio.setFirmaRequerida(firmaRequerida);
        return this;
    }

    public EnvioBuilder costoEstimado(double costoEstimado) {
        envio.setCostoEstimado(costoEstimado);
        return this;
    }

    public EnvioBuilder estado(Envio.EstadoEnvio estado) {
        envio.setEstado(estado);
        return this;
    }

    public EnvioBuilder repartidor(Repartidor repartidor) {
        envio.setRepartidor(repartidor);
        return this;
    }


    public EnvioBuilder fechaCreacion(LocalDateTime fechaCreacion) {
        envio.setFechaCreacion(fechaCreacion);
        return this;
    }

    public EnvioBuilder fechaModificacion(LocalDateTime fechaModificacion) {
        envio.setFechaCreacion(fechaModificacion);
        return this;
    }



    public EnvioBuilder fechaConfirmacion(LocalDateTime fecha) {
        envio.setFechaConfirmacion(fecha);
        return this;
    }

    public EnvioBuilder fechaEntrega(LocalDateTime fecha) {
        envio.setFechaEntrega(fecha);
        return this;
    }

    public EnvioBuilder fechaEntregaEstimada(LocalDateTime fecha) {
        envio.setFechaEntregaEstimada(fecha);
        return this;
    }

    public EnvioBuilder incidenciaDescripcion(String descripcion) {
        envio.setIncidenciaDescripcion(descripcion);
        return this;
    }


    public Envio build() {
        return envio;
    }
}
