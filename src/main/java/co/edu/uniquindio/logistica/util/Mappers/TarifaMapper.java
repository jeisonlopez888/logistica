package co.edu.uniquindio.logistica.util.Mappers;

import co.edu.uniquindio.logistica.model.DTO.TarifaDTO;
import co.edu.uniquindio.logistica.model.Tarifa;

/**
 * Mapper para convertir entre Tarifa (entidad) y TarifaDTO
 */
public class TarifaMapper {

    public static TarifaDTO toDTO(Tarifa tarifa) {
        if (tarifa == null) return null;

        return new TarifaDTO(
                tarifa.getId(),
                tarifa.getDescripcion(),
                tarifa.getCostoBase(),
                tarifa.getCostoPorKilo(),
                tarifa.getCostoPorVolumen(),
                tarifa.getRecargoSeguro()
        );
    }

    public static Tarifa toEntity(TarifaDTO dto) {
        if (dto == null) return null;

        Tarifa tarifa = new Tarifa(
                dto.getId(),
                dto.getDescripcion(),
                dto.getCostoBase(),
                dto.getCostoPorKilo(),
                dto.getCostoPorVolumen(),
                dto.getRecargoSeguro()
        );

        return tarifa;
    }

    public static void updateEntityFromDTO(Tarifa entity, TarifaDTO dto) {
        if (entity == null || dto == null) return;

        entity.setDescripcion(dto.getDescripcion());
        entity.setCostoBase(dto.getCostoBase());
        entity.setCostoPorKilo(dto.getCostoPorKilo());
        entity.setCostoPorVolumen(dto.getCostoPorVolumen());
        entity.setRecargoSeguro(dto.getRecargoSeguro());
    }
}


