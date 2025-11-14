package co.edu.uniquindio.logistica.factory;

import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.store.DataStore;

import java.time.LocalDateTime;

public class EntityFactory {

    // ✅ Crear un usuario
    public static Usuario createUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        return new Usuario(id, nombre, email, telefono, password, admin);
    }

    /**
     * 🔹 Crear un envío (solo crea el objeto, NO lo guarda)
     * Incluye todos los atributos compatibles con el modelo Envio actual.
     */
    public static Envio createEnvio(Usuario usuario, Direccion origen, Direccion destino, double peso) {
        Long id = DataStore.getInstance().nextId();

        // 🔹 Asignar coordenadas por zona si no existen
        if (origen != null && origen.getCoordenadas() == null)
            origen.setCoordenadas(obtenerCoordenadasZona(origen.getCiudad()));
        if (destino != null && destino.getCoordenadas() == null)
            destino.setCoordenadas(obtenerCoordenadasZona(destino.getCiudad()));

        // 🔹 Crear Envío con todos los campos relevantes
        Envio envio = new Envio();
        envio.setId(id);
        envio.setUsuario(usuario);
        envio.setOrigen(origen);
        envio.setDestino(destino);
        envio.setPeso(peso);
        envio.setVolumen(0.0); // valor por defecto
        envio.setPrioridad(false);
        envio.setSeguro(false);
        envio.setCostoEstimado(0.0);
        envio.setEstado(Envio.EstadoEnvio.SOLICITADO);
        envio.setFechaCreacion(LocalDateTime.now());
        envio.setFechaConfirmacion(null);
        envio.setFechaEntrega(null);
        envio.setFechaEntregaEstimada(null);
        envio.setRepartidor(null);

        return envio;
    }

    // 🔹 Obtener coordenadas simuladas según la zona
    private static String obtenerCoordenadasZona(String zona) {
        if (zona == null) return "0,0";
        return switch (zona.toLowerCase()) {
            case "sur" -> "-1.234,-75.567";
            case "centro" -> "0.000,-75.000";
            case "norte" -> "1.234,-75.789";
            default -> "0,0";
        };
    }

    /**
     * 🔹 Crear un pago asociado a un envío
     */
    public static Pago createPago(Envio envio, double monto, MetodoPago metodo) {
        Pago pago = new Pago(DataStore.getInstance().nextId(), envio, monto, metodo);
        pago.setConfirmado(false);
        pago.setFecha(LocalDateTime.now());
        return pago;
    }
}

