package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;

import java.util.List;
import java.util.stream.Collectors;

public class EnvioService {
    private final DataStore store = DataStore.getInstance();

    // CREAR
    public void registrarEnvio(Envio envio) {
        store.addEnvio(envio);
    }

    // LISTAR POR USUARIO
    public List<Envio> listarEnviosPorUsuario(Usuario usuario) {
        return store.getEnvios().stream()
                .filter(e -> e.getUsuario().equals(usuario))
                .collect(Collectors.toList());
    }

    // LISTAR TODOS
    public List<Envio> listarTodos() {
        return store.getEnvios();
    }

    // ELIMINAR
    public void eliminarEnvio(Envio envio) {
        store.getEnvios().remove(envio);
    }

    // BUSCAR POR ID
    public Envio buscarPorId(Long id) {
        return store.getEnvios().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // EDITAR (actualizar datos de un envío existente)
    public void actualizarEnvio(Envio envio, Envio actualizado) {
        envio.setOrigen(actualizado.getOrigen());
        envio.setDestino(actualizado.getDestino());
        envio.setPeso(actualizado.getPeso());
        // Puedes extender aquí con más atributos
    }
}
