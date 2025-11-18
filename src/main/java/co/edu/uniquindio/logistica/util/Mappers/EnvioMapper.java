package co.edu.uniquindio.logistica.util.Mappers;

import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Repartidor;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;

/**
 * Mapper para convertir entre Envio (entidad) y EnvioDTO
 */
public class EnvioMapper {

    private static final DataStore store = DataStore.getInstance();

    public static EnvioDTO toDTO(Envio envio) {
        if (envio == null) return null;

        EnvioDTO dto = new EnvioDTO();
        dto.setId(envio.getId());
        dto.setIdUsuario(envio.getUsuario() != null ? envio.getUsuario().getId() : null);
        dto.setOrigen(DireccionMapper.toDTO(envio.getOrigen()));
        dto.setDestino(DireccionMapper.toDTO(envio.getDestino()));
        dto.setPeso(envio.getPeso());
        dto.setVolumen(envio.getVolumen());
        dto.setPrioridad(envio.isPrioridad());
        dto.setSeguro(envio.isSeguro());
        dto.setFragil(envio.isFragil());
        dto.setFirmaRequerida(envio.isFirmaRequerida());
        dto.setTipoTarifa(envio.getTipoTarifa());
        dto.setCostoEstimado(envio.getCostoEstimado());

        // Convertir estado
        if (envio.getEstado() != null) {
            dto.setEstado(EnvioDTO.EstadoEnvio.valueOf(envio.getEstado().name()));
        }

        dto.setIdRepartidor(envio.getRepartidor() != null ? envio.getRepartidor().getId() : null);
        dto.setIncidenciaDescripcion(envio.getIncidenciaDescripcion());
        dto.setFechaCreacion(envio.getFechaCreacion());
        dto.setFechaConfirmacion(envio.getFechaConfirmacion());
        dto.setFechaEntrega(envio.getFechaEntrega());
        dto.setFechaEntregaEstimada(envio.getFechaEntregaEstimada());
        dto.setFechaIncidencia(envio.getFechaIncidencia());

        return dto;
    }

    public static Envio toEntity(EnvioDTO dto) {
        if (dto == null) return null;

        Envio envio = new Envio();
        envio.setId(dto.getId());

        // Obtener usuario desde DataStore
        if (dto.getIdUsuario() != null) {
            Usuario usuario = store.getUsuarios().stream()
                    .filter(u -> u.getId().equals(dto.getIdUsuario()))
                    .findFirst()
                    .orElse(null);
            envio.setUsuario(usuario);
        }

        envio.setOrigen(DireccionMapper.toEntity(dto.getOrigen()));
        envio.setDestino(DireccionMapper.toEntity(dto.getDestino()));
        envio.setPeso(dto.getPeso());
        envio.setVolumen(dto.getVolumen());
        envio.setPrioridad(dto.isPrioridad());
        envio.setSeguro(dto.isSeguro());
        envio.setFragil(dto.isFragil());
        envio.setFirmaRequerida(dto.isFirmaRequerida());
        envio.setTipoTarifa(dto.getTipoTarifa());
        envio.setCostoEstimado(dto.getCostoEstimado());

        // Convertir estado
        if (dto.getEstado() != null) {
            envio.setEstado(Envio.EstadoEnvio.valueOf(dto.getEstado().name()));
        }

        // Obtener repartidor desde DataStore
        if (dto.getIdRepartidor() != null) {
            Repartidor repartidor = store.getRepartidores().stream()
                    .filter(r -> r.getId().equals(dto.getIdRepartidor()))
                    .findFirst()
                    .orElse(null);
            envio.setRepartidor(repartidor);
        }

        envio.setIncidenciaDescripcion(dto.getIncidenciaDescripcion());
        envio.setFechaCreacion(dto.getFechaCreacion());
        envio.setFechaConfirmacion(dto.getFechaConfirmacion());
        envio.setFechaEntrega(dto.getFechaEntrega());
        envio.setFechaEntregaEstimada(dto.getFechaEntregaEstimada());
        envio.setFechaIncidencia(dto.getFechaIncidencia());

        return envio;
    }

    public static void updateEntityFromDTO(Envio entity, EnvioDTO dto) {
        if (entity == null || dto == null) return;

        // Obtener usuario desde DataStore
        if (dto.getIdUsuario() != null) {
            Usuario usuario = store.getUsuarios().stream()
                    .filter(u -> u.getId().equals(dto.getIdUsuario()))
                    .findFirst()
                    .orElse(null);
            entity.setUsuario(usuario);
        }

        entity.setOrigen(DireccionMapper.toEntity(dto.getOrigen()));
        entity.setDestino(DireccionMapper.toEntity(dto.getDestino()));
        entity.setPeso(dto.getPeso());
        entity.setVolumen(dto.getVolumen());
        entity.setPrioridad(dto.isPrioridad());
        entity.setSeguro(dto.isSeguro());
        entity.setFragil(dto.isFragil());
        entity.setFirmaRequerida(dto.isFirmaRequerida());
        entity.setTipoTarifa(dto.getTipoTarifa());
        entity.setCostoEstimado(dto.getCostoEstimado());

        if (dto.getEstado() != null) {
            entity.setEstado(Envio.EstadoEnvio.valueOf(dto.getEstado().name()));
        }

        // Obtener repartidor desde DataStore
        if (dto.getIdRepartidor() != null) {
            Repartidor repartidor = store.getRepartidores().stream()
                    .filter(r -> r.getId().equals(dto.getIdRepartidor()))
                    .findFirst()
                    .orElse(null);
            entity.setRepartidor(repartidor);
        }

        entity.setIncidenciaDescripcion(dto.getIncidenciaDescripcion());
        entity.setFechaConfirmacion(dto.getFechaConfirmacion());
        entity.setFechaEntrega(dto.getFechaEntrega());
        entity.setFechaEntregaEstimada(dto.getFechaEntregaEstimada());
        entity.setFechaIncidencia(dto.getFechaIncidencia());
    }
}


