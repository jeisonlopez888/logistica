package co.edu.uniquindio.logistica.model;

import java.time.LocalDateTime;

public class EnvioBuilder {
    private final Envio envio;

    public EnvioBuilder() {
        envio = new Envio();
        envio.setEstado(Envio.EstadoEnvio.PENDIENTE); // Estado inicial por defecto
    }

    // ✅ Nuevo método para asignar el ID del envío
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

    public EnvioBuilder estado(Envio.EstadoEnvio estado) {
        envio.setEstado(estado);
        return this;
    }

    public EnvioBuilder fechaCreacion(LocalDateTime fecha) {
        // Permite sobreescribir manualmente si se necesita
        // (por defecto Envio ya pone LocalDateTime.now())
        return this;
    }

    public Envio build() {
        return envio;
    }
}
