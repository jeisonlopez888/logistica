package co.edu.uniquindio.logistica.util.Mappers;

import co.edu.uniquindio.logistica.model.DTO.RepartidorDTO;
import co.edu.uniquindio.logistica.model.Repartidor;

/**
 * Mapper para convertir entre Repartidor (entidad) y RepartidorDTO
 */
public class RepartidorMapper {

    public static RepartidorDTO toDTO(Repartidor repartidor) {
        if (repartidor == null) return null;

        RepartidorDTO dto = new RepartidorDTO();
        dto.setId(repartidor.getId());
        dto.setNombre(repartidor.getNombre());
        dto.setDocumento(repartidor.getDocumento());
        dto.setTelefono(repartidor.getTelefono());
        dto.setZona(repartidor.getZona());
        dto.setDisponible(repartidor.isDisponible());

        return dto;
    }

    public static Repartidor toEntity(RepartidorDTO dto) {
        if (dto == null) return null;

        Repartidor repartidor = new Repartidor(
                dto.getId(),
                dto.getNombre(),
                dto.getDocumento(),
                dto.getTelefono(),
                dto.getZona(),
                dto.isDisponible()
        );

        return repartidor;
    }

    public static void updateEntityFromDTO(Repartidor entity, RepartidorDTO dto) {
        if (entity == null || dto == null) return;

        entity.setNombre(dto.getNombre());
        entity.setDocumento(dto.getDocumento());
        entity.setTelefono(dto.getTelefono());
        entity.setZona(dto.getZona());
        entity.setDisponible(dto.isDisponible());
    }
}


