package co.edu.uniquindio.logistica.util.Mappers;

import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Usuario (entidad) y UsuarioDTO
 */
public class UsuarioMapper {

    public static UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setPassword(usuario.getPassword());
        dto.setAdmin(usuario.isAdmin());

        // Convertir direcciones a DTOs
        List<DireccionDTO> direccionesDTO = usuario.getDirecciones().stream()
                .map(DireccionMapper::toDTO)
                .collect(Collectors.toList());
        dto.setDirecciones(direccionesDTO);

        // Métodos de pago (enums se pueden pasar directamente)
        dto.setMetodosPago(usuario.getMetodosPago());

        return dto;
    }

    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setPassword(dto.getPassword());
        usuario.setAdmin(dto.isAdmin());

        // Convertir direcciones DTOs a entidades
        List<Direccion> direcciones = dto.getDirecciones().stream()
                .map(DireccionMapper::toEntity)
                .collect(Collectors.toList());
        usuario.setDirecciones(direcciones);

        // Métodos de pago
        usuario.setMetodosPago(dto.getMetodosPago());

        return usuario;
    }

    public static void updateEntityFromDTO(Usuario entity, UsuarioDTO dto) {
        if (entity == null || dto == null) return;

        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setPassword(dto.getPassword());
        entity.setAdmin(dto.isAdmin());

        // Actualizar direcciones
        List<Direccion> direcciones = dto.getDirecciones().stream()
                .map(DireccionMapper::toEntity)
                .collect(Collectors.toList());
        entity.setDirecciones(direcciones);

        // Métodos de pago
        entity.setMetodosPago(dto.getMetodosPago());
    }
}


