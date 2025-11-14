package co.edu.uniquindio.logistica.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.List;

public class TestFXMLCargadores extends Application {

    @Override
    public void start(Stage stage) {
        List<String> vistas = List.of(

                "/fxml/login.fxml",
                "/fxml/admin.fxml",
                "/fxml/usuarios.fxml",
                "/fxml/admins.fxml",
                "/fxml/repartidores.fxml",
                "/fxml/crear_usuario_admin.fxml",
                "/fxml/crear_envio_user.fxml",
                "/fxml/crear_repartidor.fxml",
                "/fxml/detalle_tarifa.fxml",
                "/fxml/editar_usuario_admin.fxml",
                "/fxml/editar_usuario_user.fxml",
                "/fxml/historial_envios_user.fxml",
                "/fxml/user.fxml",
                "/fxml/pagos.fxml",
                "/fxml/rastreo.fxml",
                "/fxml/registro.fxml",
                "/fxml/reportes.fxml",
                "/fxml/tarifas.fxml",
                "/fxml/user_admin.fxml",
                "/fxml/crear_envio_admin.fxml",
                "/fxml/historial_envios_admin.fxml",
                "/fxml/editar_envio.fxml"



        );

        System.out.println("🔍 Iniciando test de carga FXML...");
        int errores = 0;

        for (String vista : vistas) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
                Parent root = loader.load();
                System.out.println("✅ Cargó correctamente: " + vista);
            } catch (Exception e) {
                errores++;
                System.err.println("❌ Error cargando " + vista + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("\n📊 Test completado. Archivos con error: " + errores);
        stage.close();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}

