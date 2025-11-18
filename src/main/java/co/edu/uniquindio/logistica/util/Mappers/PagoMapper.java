package co.edu.uniquindio.logistica.util.Mappers;

import co.edu.uniquindio.logistica.model.DTO.PagoDTO;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Pago;
import co.edu.uniquindio.logistica.store.DataStore;

/**
 * Mapper para convertir entre Pago (entidad) y PagoDTO
 */
public class PagoMapper {

    private static final DataStore store = DataStore.getInstance();

    public static PagoDTO toDTO(Pago pago) {
        if (pago == null) return null;

        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setIdEnvio(pago.getEnvio() != null ? pago.getEnvio().getId() : null);
        dto.setMontoPagado(pago.getMontoPagado());
        dto.setMetodo(pago.getMetodo());
        dto.setFechaPago(pago.getFechaPago());
        dto.setConfirmado(pago.isConfirmado());
        dto.setPeso(pago.getPeso());
        dto.setVolumen(pago.getVolumen());
        dto.setPrioridad(pago.isPrioridad());
        dto.setSeguro(pago.isSeguro());
        dto.setCostoBase(pago.getCostoBase());
        dto.setCostoFinal(pago.getCostoFinal());

        return dto;
    }

    public static Pago toEntity(PagoDTO dto) {
        if (dto == null) return null;

        // Obtener envío desde DataStore
        Envio envio = null;
        if (dto.getIdEnvio() != null) {
            envio = store.getEnvios().stream()
                    .filter(e -> e.getId().equals(dto.getIdEnvio()))
                    .findFirst()
                    .orElse(null);
        }

        Pago pago = new Pago(dto.getId(), envio, dto.getMontoPagado(), dto.getMetodo());
        pago.setConfirmado(dto.isConfirmado());
        pago.setFechaPago(dto.getFechaPago());

        // Los datos derivados se calculan automáticamente en el constructor de Pago
        // basándose en el envío asociado, no necesitamos setterlos manualmente

        return pago;
    }

    public static void updateEntityFromDTO(Pago entity, PagoDTO dto) {
        if (entity == null || dto == null) return;

        // Pago es inmutable para el envio (se establece en constructor)
        // Solo podemos actualizar campos modificables
        entity.setMontoPagado(dto.getMontoPagado());
        entity.setMetodo(dto.getMetodo());
        entity.setConfirmado(dto.isConfirmado());
        entity.setFechaPago(dto.getFechaPago());
    }
}


