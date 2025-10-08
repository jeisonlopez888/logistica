package co.edu.uniquindio.logistica.util;

import co.edu.uniquindio.logistica.model.Usuario;

public class Sesion {

    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}

