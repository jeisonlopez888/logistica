package co.edu.uniquindio.logistica.model;

import co.edu.uniquindio.logistica.store.DataStore;

public class EnvioBuilder {
    private Envio envio;

    public EnvioBuilder() {
        envio = new Envio();
        envio.setId(DataStore.getInstance().nextId());
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

    public Envio build() {
        return envio;
    }
}

