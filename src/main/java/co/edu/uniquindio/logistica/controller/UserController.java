package co.edu.uniquindio.logistica.controller;

import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UserController {

    private Usuario usuario;

    // Este método será llamado desde el LoginController
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void abrirCrearEnvio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio.fxml"));
            Parent root = loader.load();

            CrearEnvioController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Envío");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void verHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial_envios.fxml"));
            Parent root = loader.load();

            HistorialEnviosController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Envíos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void cerrarSesion() {
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(Window::isShowing).get(0);
        stage.close(); // cierra la ventana
    }

    private void mostrarMensaje(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Funcionalidad");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
