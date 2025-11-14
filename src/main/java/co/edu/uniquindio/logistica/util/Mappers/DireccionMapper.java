package co.edu.uniquindio.logistica.util.Mappers;

import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.Direccion;

public class DireccionMapper {
    public static DireccionDTO toDTO(Direccion direccion) {
        if (direccion == null) return null;
        return new DireccionDTO(
                direccion.getId(),
                direccion.getAlias(),
                direccion.getCiudad(),
                direccion.getCalle(),
                direccion.getCoordenadas()
        );
    }

    public static Direccion toEntity(DireccionDTO dto) {
        if (dto == null) return null;
        return new Direccion(
                dto.getId(),
                dto.getAlias(),
                dto.getCiudad(),
                dto.getCalle(),
                dto.getCoordenadas()
        );
    }

    public static void updateEntityFromDTO(Direccion entity, DireccionDTO dto) {
        if (entity == null || dto == null) return;
        entity.setId(dto.getId());
        entity.setAlias(dto.getAlias());
        entity.setCiudad(dto.getCiudad());
        entity.setCalle(dto.getCalle());
        entity.setCoordenadas(dto.getCoordenadas());
    }
}
