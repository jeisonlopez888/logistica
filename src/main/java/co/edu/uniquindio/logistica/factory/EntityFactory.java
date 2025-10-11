package co.edu.uniquindio.logistica.factory;

import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.store.DataStore;

public class EntityFactory {

    // ✅ Crear un usuario
    public static Usuario createUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        return new Usuario(id, nombre, email, telefono, password, admin);
    }

    /**
     * 🔹 Crear un envío (solo crea el objeto, NO lo guarda)
     */
    public static Envio createEnvio(Usuario usuario, Direccion origen, Direccion destino, double peso) {
        Long id = DataStore.getInstance().nextId();

        // Asignar coordenadas ficticias según zona
        if (origen != null && origen.getCoordenadas() == null)
            origen.setCoordenadas(obtenerCoordenadasZona(origen.getCiudad()));
        if (destino != null && destino.getCoordenadas() == null)
            destino.setCoordenadas(obtenerCoordenadasZona(destino.getCiudad()));

        // Crear el envío
        Envio envio = new Envio();
        envio.setId(id);
        envio.setUsuario(usuario);
        envio.setOrigen(origen);
        envio.setDestino(destino);
        envio.setPeso(peso);
        envio.setEstado(Envio.EstadoEnvio.PENDIENTE);
        envio.setFechaCreacion(java.time.LocalDateTime.now());

        return envio;
    }

    private static String obtenerCoordenadasZona(String zona) {
        if (zona == null) return "0,0";
        return switch (zona.toLowerCase()) {
            case "sur" -> "-1.234,-75.567";
            case "centro" -> "0.000,-75.000";
            case "norte" -> "1.234,-75.789";
            default -> "0,0";
        };
    }

    public static Pago createPago(Envio envio, double monto, MetodoPago metodo) {
        return new Pago(DataStore.getInstance().nextId(), envio, monto, metodo);
    }
}
