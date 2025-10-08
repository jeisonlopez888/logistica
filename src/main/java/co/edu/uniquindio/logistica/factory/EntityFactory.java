
package co.edu.uniquindio.logistica.factory;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.EnvioBuilder;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.store.DataStore;

public class EntityFactory {

    // ✅ Crear un usuario (solo crea el objeto, NO lo guarda todavía)
    public static Usuario createUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        return new Usuario(id, nombre, email, telefono, password, admin);
    }

    // 🔹 Crear un envío (este sí puede registrar directamente porque tiene builder)
    public static Envio createEnvio(Usuario usuario, Direccion origen, Direccion destino, double peso) {
        Envio envio = new EnvioBuilder()
                .usuario(usuario)
                .origen(origen)
                .destino(destino)
                .peso(peso)
                .build();

        DataStore.getInstance().addEnvio(envio); // mantenerlo aquí está bien
        return envio;
    }
}
