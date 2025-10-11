package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Repartidor;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EnvioService {

    private final DataStore store = DataStore.getInstance();
    private final TarifaService tarifaService = new TarifaService();

    // CREAR
    public void registrarEnvio(Envio envio) {
        if (envio.getEstado() == null) {
            envio.setEstado(Envio.EstadoEnvio.PENDIENTE);
        }
        // calcular costo estimado al crear (si no está calculado)
        if (envio.getCostoEstimado() == 0.0) {
            double total = tarifaService.calcularTarifa(envio);
            envio.setCostoEstimado(total);
        }
        // establecer fecha estimada de entrega simple (por ejemplo +2 dias si es express)
        if (envio.getFechaEntregaEstimada() == null) {
            LocalDateTime estimada = LocalDateTime.now().plusDays(envio.isPrioridad() ? 1 : 2);
            envio.setFechaEntregaEstimada(estimada);
        }

        store.addEnvio(envio);
    }

    // LISTAR POR USUARIO
    public List<Envio> listarEnviosPorUsuario(Usuario usuario) {
        return store.getEnvios().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().equals(usuario))
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

    // ACTUALIZAR ENVÍO COMPLETO
    public void actualizarEnvio(Envio envio, Envio actualizado) {
        envio.setOrigen(actualizado.getOrigen());
        envio.setDestino(actualizado.getDestino());
        envio.setPeso(actualizado.getPeso());
        envio.setVolumen(actualizado.getVolumen());
        envio.setPrioridad(actualizado.isPrioridad());
        envio.setSeguro(actualizado.isSeguro());
        envio.setEstado(actualizado.getEstado());
        envio.setCostoEstimado(actualizado.getCostoEstimado());
    }

    // ACTUALIZAR SOLO EL ESTADO (convalidaciones simples)
    public void actualizarEstado(Long idEnvio, Envio.EstadoEnvio nuevoEstado) {
        Envio envio = buscarPorId(idEnvio);
        if (envio != null) {
            envio.setEstado(nuevoEstado);
            if (nuevoEstado == Envio.EstadoEnvio.ENTREGADO) {
                envio.setFechaEntrega(LocalDateTime.now());
            }
        }
    }

    /**
     * Intenta asignar un repartidor disponible en la misma zona del origen.
     * Si encuentra uno, lo marca como no disponible y setea el repartidor en el envío.
     * También actualiza el estado a ASIGNADO y devuelve true; si no hay disponibles devuelve false.
     */
    public boolean asignarRepartidor(Envio envio) {
        if (envio == null || envio.getOrigen() == null) return false;

        String zonaTemp = envio.getOrigen().getCoordenadas();
        if (zonaTemp == null) zonaTemp = envio.getOrigen().getCiudad();

        if (zonaTemp == null) return false;
        final String zonaOrigen = zonaTemp.toLowerCase(); // 🔹 copia final para usar en lambda


        // buscar repartidor disponible que coincida en zona (comparación sencilla contains/equals)
        List<Repartidor> candidatos = store.getRepartidores().stream()
                .filter(Repartidor::isDisponible)
                .filter(r -> {
                    String z = r.getZona() != null ? r.getZona().toLowerCase() : "";
                    return z.contains(zonaOrigen) || zonaOrigen.contains(z);
                })
                .collect(Collectors.toList());

        if (candidatos.isEmpty()) return false;

        Repartidor elegido = candidatos.get(0); // tomar el primero disponible
        // marcar como no disponible: Repartidor es inmutable en tu clase actual (sin setter), así que reemplazamos el objeto
        store.getRepartidores().removeIf(r -> r.getId().equals(elegido.getId()));
        Repartidor actualizado = new Repartidor(elegido.getId(), elegido.getNombre(), elegido.getTelefono(), elegido.getZona(), false);
        store.getRepartidores().add(actualizado);

        envio.setRepartidor(actualizado);
        envio.setEstado(Envio.EstadoEnvio.ASIGNADO);
        return true;
    }

    /**
     * Filtrar envíos por fecha (rango), estado o zona (origen/destino).
     */
    public List<Envio> filtrarEnvios(Envio.EstadoEnvio estado, LocalDateTime desde, LocalDateTime hasta, String zona) {
        return store.getEnvios().stream()
                .filter(e -> estado == null || e.getEstado() == estado)
                .filter(e -> desde == null || !e.getFechaCreacion().isBefore(desde))
                .filter(e -> hasta == null || !e.getFechaCreacion().isAfter(hasta))
                .filter(e -> {
                    if (zona == null || zona.isEmpty()) return true;
                    String z = zona.toLowerCase();
                    boolean inOrigen = e.getOrigen() != null && ((e.getOrigen().getCoordenadas()!=null && e.getOrigen().getCoordenadas().toLowerCase().contains(z)) || (e.getOrigen().getCiudad()!=null && e.getOrigen().getCiudad().toLowerCase().contains(z)));
                    boolean inDestino = e.getDestino() != null && ((e.getDestino().getCoordenadas()!=null && e.getDestino().getCoordenadas().toLowerCase().contains(z)) || (e.getDestino().getCiudad()!=null && e.getDestino().getCiudad().toLowerCase().contains(z)));
                    return inOrigen || inDestino;
                })
                .collect(Collectors.toList());
    }
}
