
package co.edu.uniquindio.logistica.factory;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.EnvioBuilder;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.store.DataStore;

public class EntityFactory {

    // 🔹 Crear un usuario
    public static Usuario createUsuario(Long id, String nombre, String email, String telefono, String password, boolean admin) {
        Usuario u = new Usuario(id, nombre, email, telefono, password, admin);
        DataStore.getInstance().addUsuario(u); // opcional: registrar automáticamente en el DataStore
        return u;
    }

    // 🔹 Crear un envío (usando Builder + Factory)
    public static Envio createEnvio(Usuario usuario, Direccion origen, Direccion destino, double peso) {
        Envio envio = new EnvioBuilder()
                .usuario(usuario)
                .origen(origen)
                .destino(destino)
                .peso(peso)
                .build();

        DataStore.getInstance().addEnvio(envio); // registrar automáticamente
        return envio;
    }
}
