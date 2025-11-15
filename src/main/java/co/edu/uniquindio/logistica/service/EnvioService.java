package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.factory.EntityFactory;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.store.DataStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnvioService {

    private final DataStore store = DataStore.getInstance();
    private final TarifaService tarifaService = new TarifaService();
    private final RepartidorService repartidorService = new RepartidorService();

    // 🔹 CREAR o GUARDAR envío
    public void registrarEnvio(Envio envio) {
        // Los servicios trabajan solo con entidades
        // La conversión de DTO a Entity se hace en la Facade
        // El usuario debe venir ya asignado desde la Facade

        // Si el envío ya existe, actualizarlo en lugar de crear uno nuevo
        Envio existente = buscarPorId(envio.getId());
        if (existente != null) {
            actualizarEnvioCompleto(existente, envio);
            return;
        }

        if (envio.getEstado() == null)
            envio.setEstado(Envio.EstadoEnvio.SOLICITADO);

        if (envio.getFechaCreacion() == null)
            envio.setFechaCreacion(LocalDateTime.now());

        if (envio.getCostoEstimado() == 0.0)
            envio.setCostoEstimado(tarifaService.calcularTarifa(envio));

        store.addEnvio(envio);
    }
    
    // 🔹 Actualizar completamente un envío existente
    private void actualizarEnvioCompleto(Envio existente, Envio actualizado) {
        existente.setOrigen(actualizado.getOrigen());
        existente.setDestino(actualizado.getDestino());
        existente.setPeso(actualizado.getPeso());
        existente.setVolumen(actualizado.getVolumen());
        existente.setPrioridad(actualizado.isPrioridad());
        existente.setSeguro(actualizado.isSeguro());
        existente.setFragil(actualizado.isFragil());
        existente.setFirmaRequerida(actualizado.isFirmaRequerida());
        existente.setCostoEstimado(actualizado.getCostoEstimado());
        
        if (actualizado.getEstado() != null) {
            existente.setEstado(actualizado.getEstado());
        }
        
        if (actualizado.getRepartidor() != null) {
            existente.setRepartidor(actualizado.getRepartidor());
        }
        
        if (actualizado.getUsuario() != null) {
            existente.setUsuario(actualizado.getUsuario());
        }
        
        existente.setFechaConfirmacion(actualizado.getFechaConfirmacion());
        existente.setFechaEntrega(actualizado.getFechaEntrega());
        existente.setFechaEntregaEstimada(actualizado.getFechaEntregaEstimada());
        existente.setFechaIncidencia(actualizado.getFechaIncidencia());
        existente.setIncidenciaDescripcion(actualizado.getIncidenciaDescripcion());
    }





    // 🔹 Buscar envío por ID
    public Envio buscarPorId(Long id) {
        return store.getEnvios().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 🔹 Actualizar envío existente
    public boolean actualizarEnvio(Envio envio, Envio actualizado, Usuario usuarioActual) {
        if (envio == null) return false;
        boolean esAdmin = usuarioActual != null && usuarioActual.isAdmin();

        // Solo se puede editar si está solicitado o si el usuario es admin
        if (!esAdmin && envio.getEstado() != Envio.EstadoEnvio.SOLICITADO)
            return false;

        envio.setOrigen(actualizado.getOrigen());
        envio.setDestino(actualizado.getDestino());
        envio.setPeso(actualizado.getPeso());
        envio.setVolumen(actualizado.getVolumen());
        envio.setPrioridad(actualizado.isPrioridad());
        envio.setSeguro(actualizado.isSeguro());
        envio.setFragil(actualizado.isFragil());
        envio.setFirmaRequerida(actualizado.isFirmaRequerida());
        envio.setCostoEstimado(actualizado.getCostoEstimado());
        envio.setEstado(actualizado.getEstado());
        envio.setUsuario(usuarioActual);
        
        if (actualizado.getRepartidor() != null) {
            envio.setRepartidor(actualizado.getRepartidor());
        }

        return true;
    }

    // 🔹 Actualizar estado del envío
    public void actualizarEstado(Long idEnvio, Envio.EstadoEnvio nuevoEstado) {
        Envio envio = buscarPorId(idEnvio);
        if (envio == null) return;

        envio.setEstado(nuevoEstado);

        switch (nuevoEstado) {
            case CONFIRMADO -> {
                envio.setFechaConfirmacion(LocalDateTime.now());

                // ✅ Asignar automáticamente repartidor disponible en la zona
                boolean asignado = asignarRepartidor(envio);
                if (!asignado) {
                    System.out.println("⚠️ No se encontró repartidor disponible para la zona de origen del envío.");
                }
            }
            case ENTREGADO -> envio.setFechaEntrega(LocalDateTime.now());
            default -> { /* No se necesita acción extra */ }
        }
    }

    // 🔹 Asignar automáticamente un repartidor disponible según la zona de destino
    public boolean asignarRepartidor(Envio envio) {
        if (envio == null || envio.getDestino() == null) return false;

        // Usar ciudad (zona: Norte, Centro, Sur) en lugar de coordenadas
        String zona = envio.getDestino().getCiudad();
        if (zona == null || zona.isBlank()) {
            // Si ciudad está vacía, intentar con coordenadas como fallback
            zona = envio.getDestino().getCoordenadas();
            if (zona == null || zona.isBlank()) return false;
        }

        // Buscar repartidor disponible en esa zona
        Repartidor elegido = repartidorService.buscarDisponiblePorZona(zona);
        if (elegido == null) {
            System.out.println("⚠️ No hay repartidores disponibles en la zona: " + zona);
            return false;
        }

        // Asignar repartidor al envío
        envio.setRepartidor(elegido);
        envio.setEstado(Envio.EstadoEnvio.ASIGNADO);
        envio.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));

        // ✅ Permitir que un repartidor tenga múltiples envíos asignados
        // No se marca como no disponible
        // repartidorService.marcarNoDisponible(elegido);

        System.out.println("🚚 Repartidor asignado: " + elegido.getNombre() + " (Zona: " + elegido.getZona() + ")");
        return true;
    }

    // 🔹 Registrar incidencia
    public void registrarIncidencia(Envio envio, String descripcion) {
        if (envio == null || descripcion == null || descripcion.isBlank()) return;

        envio.setEstado(Envio.EstadoEnvio.INCIDENCIA);
        envio.setIncidenciaDescripcion(descripcion);
        envio.setFechaIncidencia(LocalDateTime.now());

        // Reemplazar en DataStore
        store.getEnvios().replaceAll(e -> e.getId().equals(envio.getId()) ? envio : e);
    }

    // 🔹 Consultar descripción de una incidencia
    public String obtenerIncidencia(Long envioId) {
        Envio envio = buscarPorId(envioId);
        if (envio != null && envio.getEstado() == Envio.EstadoEnvio.INCIDENCIA) {
            return envio.getIncidenciaDescripcion();
        }
        return "Sin incidencias registradas.";
    }

    public Envio crearEnvio(Usuario usuario, Direccion origen, Direccion destino, double peso) {
        Envio envio = EntityFactory.createEnvio(usuario, origen, destino, peso);
        registrarEnvio(envio);
        return envio;
    }

    public String confirmarPago(Envio envio) {
        envio.setFechaConfirmacion(LocalDateTime.now());
        envio.setEstado(Envio.EstadoEnvio.CONFIRMADO);
        boolean asignado = asignarRepartidor(envio);
        return asignado ? "✅ Repartidor asignado" : "⚠️ No hay repartidores disponibles.";
    }









    // Retorna todos los envíos registrados
    public List<Envio> listarTodos() {
        return new ArrayList<>(DataStore.getInstance().getEnvios());
    }

    // Retorna solo los envíos de un usuario específico
    public List<Envio> listarEnviosPorUsuario(Usuario usuario) {
        return DataStore.getInstance().getEnvios().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());
    }


    // Elimina un envío del DataStore
    public void eliminarEnvio(Envio envio) {
        DataStore.getInstance().getEnvios().removeIf(e -> e.getId().equals(envio.getId()));
    }








}
