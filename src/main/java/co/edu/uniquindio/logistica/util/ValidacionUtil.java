package co.edu.uniquindio.logistica.util;

/**
 * Utilidad para validación de datos de entrada en controladores.
 * RF-040: Los controladores solo validan datos, no contienen lógica de negocio.
 */
public class ValidacionUtil {

    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) return false;
        return telefono.matches("^[0-9]{10}$");
    }

    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        return nombre.trim().length() >= 3 && nombre.trim().length() <= 100;
    }

    public static boolean esPasswordValido(String password) {
        if (password == null || password.trim().isEmpty()) return false;
        return password.length() >= 4;
    }

    public static boolean esPesoValido(double peso) {
        return peso > 0 && peso <= 1000; // Máximo 1000 kg
    }

    public static boolean esVolumenValido(double volumen) {
        return volumen >= 0 && volumen <= 10; // Máximo 10 m³
    }

    public static boolean esMontoValido(double monto) {
        return monto > 0;
    }

    public static boolean esIdValido(Long id) {
        return id != null && id > 0;
    }

    public static String validarYLimpiarEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    public static String validarYLimpiarTexto(String texto) {
        if (texto == null) return null;
        return texto.trim();
    }

    public static boolean isEmpty(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        return esEmailValido(email);
    }

    public static boolean isPhoneValid(String telefono) {
        return esTelefonoValido(telefono);
    }
}
