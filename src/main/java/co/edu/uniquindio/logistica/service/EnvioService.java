package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.factory.EntityFactory;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.observer.EnvioSubject;
import co.edu.uniquindio.logistica.store.DataStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnvioService {

    private final DataStore store = DataStore.getInstance();
    private final TarifaService tarifaService = new TarifaService();
    private final RepartidorService repartidorService = new RepartidorService();
    private final EnvioSubject envioSubject = new EnvioSubject();
    
    // Singleton para NotificationService
    private static NotificationService notificationService;
    
    public EnvioService() {
        // Registrar el servicio de notificaciones como observador
        if (notificationService == null) {
            notificationService = new NotificationService();
        }
        envioSubject.registrarObserver(notificationService);
    }
    
    /**
     * Establece el canal de notificación preferido
     */
    public void setCanalNotificacion(NotificationService.CanalNotificacion canal) {
        if (notificationService != null) {
            notificationService.setCanalPreferido(canal);
        }
    }

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

        // Siempre recalcular el costo para asegurar que esté actualizado
        envio.setCostoEstimado(tarifaService.calcularTarifa(envio));

        store.addEnvio(envio);
        
        // Notificar creación de envío usando Observer
        envioSubject.notificarCreacionEnvio(envio);
    }
    
    // 🔹 Actualizar completamente un envío existente
    private void actualizarEnvioCompleto(Envio existente, Envio actualizado) {
        // Guardar estado anterior para notificación
        Envio.EstadoEnvio estadoAnterior = existente.getEstado();
        
        existente.setOrigen(actualizado.getOrigen());
        existente.setDestino(actualizado.getDestino());
        existente.setPeso(actualizado.getPeso());
        existente.setVolumen(actualizado.getVolumen());
        existente.setPrioridad(actualizado.isPrioridad());
        existente.setSeguro(actualizado.isSeguro());
        existente.setFragil(actualizado.isFragil());
        existente.setFirmaRequerida(actualizado.isFirmaRequerida());
        existente.setTipoTarifa(actualizado.getTipoTarifa());
        
        // Recalcular el costo si cambió algún parámetro relevante
        if (actualizado.getCostoEstimado() > 0) {
            existente.setCostoEstimado(actualizado.getCostoEstimado());
        } else {
            // Recalcular el costo basado en los nuevos datos
            existente.setCostoEstimado(tarifaService.calcularTarifa(existente));
        }
        
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
        
        // Notificar cambio de estado si cambió
        if (actualizado.getEstado() != null && estadoAnterior != actualizado.getEstado()) {
            envioSubject.notificarCambioEstado(existente, 
                estadoAnterior != null ? estadoAnterior.name() : "N/A",
                actualizado.getEstado().name());
        }
    }





    // 🔹 Buscar envío por ID
    public Envio buscarPorId(Long id) {
        return store.getEnvios().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // 🔹 Filtrar envíos por fecha y estado
    public List<Envio> filtrarEnvios(List<Envio> envios, java.time.LocalDate fechaInicio, 
                                     java.time.LocalDate fechaFin, Envio.EstadoEnvio estado) {
        List<Envio> filtrados = new ArrayList<>(envios);
        
        // Filtrar por fecha de inicio
        if (fechaInicio != null) {
            filtrados = filtrados.stream()
                    .filter(e -> {
                        if (e.getFechaCreacion() == null) return false;
                        return e.getFechaCreacion().toLocalDate().isAfter(fechaInicio.minusDays(1));
                    })
                    .collect(Collectors.toList());
        }
        
        // Filtrar por fecha de fin
        if (fechaFin != null) {
            filtrados = filtrados.stream()
                    .filter(e -> {
                        if (e.getFechaCreacion() == null) return false;
                        return e.getFechaCreacion().toLocalDate().isBefore(fechaFin.plusDays(1));
                    })
                    .collect(Collectors.toList());
        }
        
        // Filtrar por estado
        if (estado != null) {
            filtrados = filtrados.stream()
                    .filter(e -> e.getEstado() != null && e.getEstado().equals(estado))
                    .collect(Collectors.toList());
        }
        
        return filtrados;
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

        // Guardar estado anterior para notificación
        Envio.EstadoEnvio estadoAnterior = envio.getEstado();
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
            case ENTREGADO -> {
                envio.setFechaEntrega(LocalDateTime.now());
                // Notificar entrega completada
                String mensaje = String.format("El envío #%d ha sido entregado exitosamente",
                        envio.getId());
                envioSubject.notificarObservers(envio, 
                    co.edu.uniquindio.logistica.observer.TipoEvento.ENTREGA_COMPLETADA, mensaje);
            }
            default -> { /* No se necesita acción extra */ }
        }
        
        // Notificar cambio de estado usando Observer
        if (estadoAnterior != nuevoEstado) {
            envioSubject.notificarCambioEstado(envio, 
                estadoAnterior != null ? estadoAnterior.name() : "N/A",
                nuevoEstado.name());
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
        Envio.EstadoEnvio estadoAnterior = envio.getEstado();
        envio.setEstado(Envio.EstadoEnvio.ASIGNADO);
        envio.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));

        // ✅ Permitir que un repartidor tenga múltiples envíos asignados
        // No se marca como no disponible
        // repartidorService.marcarNoDisponible(elegido);

        System.out.println("🚚 Repartidor asignado: " + elegido.getNombre() + " (Zona: " + elegido.getZona() + ")");
        
        // Notificar asignación de repartidor usando Observer
        envioSubject.notificarAsignacionRepartidor(envio);
        
        // Notificar cambio de estado si cambió
        if (estadoAnterior != Envio.EstadoEnvio.ASIGNADO) {
            envioSubject.notificarCambioEstado(envio,
                estadoAnterior != null ? estadoAnterior.name() : "N/A",
                Envio.EstadoEnvio.ASIGNADO.name());
        }
        
        return true;
    }

    // 🔹 Registrar incidencia
    public void registrarIncidencia(Envio envio, String descripcion) {
        if (envio == null || descripcion == null || descripcion.isBlank()) return;

        Envio.EstadoEnvio estadoAnterior = envio.getEstado();
        envio.setEstado(Envio.EstadoEnvio.INCIDENCIA);
        envio.setIncidenciaDescripcion(descripcion);
        envio.setFechaIncidencia(LocalDateTime.now());

        // Reemplazar en DataStore
        store.getEnvios().replaceAll(e -> e.getId().equals(envio.getId()) ? envio : e);
        
        // Notificar incidencia usando Observer
        String mensaje = String.format("Se ha registrado una incidencia en el envío #%d: %s",
                envio.getId(), descripcion);
        envioSubject.notificarObservers(envio, 
            co.edu.uniquindio.logistica.observer.TipoEvento.INCIDENCIA, mensaje);
        
        // Notificar cambio de estado si cambió
        if (estadoAnterior != Envio.EstadoEnvio.INCIDENCIA) {
            envioSubject.notificarCambioEstado(envio,
                estadoAnterior != null ? estadoAnterior.name() : "N/A",
                Envio.EstadoEnvio.INCIDENCIA.name());
        }
    }

    // 🔹 Consultar descripción de una incidencia
    public String obtenerIncidencia(Long envioId) {
        Envio envio = buscarPorId(envioId);
        if (envio != null && envio.getEstado() == Envio.EstadoEnvio.INCIDENCIA) {
            // Usar fecha de incidencia, si no existe usar fecha de entrega, si no existe usar fecha de confirmación (asignación)
            LocalDateTime fechaIncidencia = envio.getFechaIncidencia();
            if (fechaIncidencia == null) {
                fechaIncidencia = envio.getFechaEntrega();
            }
            if (fechaIncidencia == null) {
                fechaIncidencia = envio.getFechaConfirmacion();
            }
            
            String fechaStr = fechaIncidencia != null 
                    ? fechaIncidencia.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "Sin fecha registrada";
            
            return "📅 Fecha: " + fechaStr + "\n\n📝 Descripción:\n" + 
                   (envio.getIncidenciaDescripcion() != null ? envio.getIncidenciaDescripcion() : "Sin descripción registrada.");
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
        // Asignar repartidor disponible en la zona de destino
        boolean asignado = asignarRepartidor(envio);
        if (asignado) {
            // Si se asignó repartidor, el estado cambia a ASIGNADO
            // Mantener el estado como ASIGNADO después de asignar repartidor
            return "✅ Repartidor asignado en zona de destino";
        } else {
            // Si no hay repartidor disponible, mantener como CONFIRMADO
            return "⚠️ No hay repartidores disponibles en la zona de destino. Estado: CONFIRMADO";
        }
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
