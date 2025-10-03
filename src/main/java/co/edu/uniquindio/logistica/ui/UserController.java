package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UserController {

    @FXML private Button crearEnvioBtn;
    @FXML private Button verHistorialBtn;
    @FXML private Button cerrarSesionBtn;

    private Usuario usuario;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    // Este método lo invoca LoginController al iniciar sesión
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void handleCrearEnvio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio.fxml"));
            Parent root = loader.load();

            CrearEnvioController ctrl = loader.getController();
            ctrl.setUsuario(usuario);
            ctrl.setOnEnvioCreado(() -> {}); // Puedes actualizar la lista si lo necesitas

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Envío");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial_envios.fxml"));
            Parent root = loader.load();

            HistorialEnviosController ctrl = loader.getController();
            ctrl.setUsuario(usuario); // Pasar el usuario actual a la ventana de historial

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Envíos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al login.");
        }
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
