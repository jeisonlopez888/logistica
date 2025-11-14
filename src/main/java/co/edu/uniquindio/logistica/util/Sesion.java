package co.edu.uniquindio.logistica.util;

import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;

/**
 * Clase para gestionar la sesión del usuario actual.
 * Trabaja con DTOs, no con entidades.
 */
public class Sesion {

    private static UsuarioDTO usuarioActual;

    public static void setUsuarioActual(UsuarioDTO usuario) {
        usuarioActual = usuario;
    }

    public static UsuarioDTO getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}

