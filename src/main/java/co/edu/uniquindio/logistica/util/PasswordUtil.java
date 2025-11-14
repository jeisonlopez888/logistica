package co.edu.uniquindio.logistica.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilidad para manejo de contraseñas.
 * Implementación sin dependencias externas, usando SHA-256 + Base64.
 * Patrón: Utility Class (no instanciable, solo métodos estáticos).
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Genera un hash SHA-256 codificado en Base64.
     *
     * @param plainPassword contraseña original
     * @return hash seguro en formato Base64
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si la contraseña ingresada coincide con el hash almacenado.
     *
     * @param plainPassword contraseña en texto plano
     * @return true si coincide, false si no
     */
    public static boolean verify(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) return false;

        // Caso 1: la contraseña guardada ya está hasheada
        String hashedInput = hash(plainPassword);
        if (hashedInput.equals(storedPassword)) {
            return true;
        }

        // Caso 2: compatibilidad con usuarios antiguos (contraseña en texto plano)
        return plainPassword.equals(storedPassword);
    }

}
