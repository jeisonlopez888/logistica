package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Controlador de Usuario - Solo valida y usa DTOs
 */
public class UserController {

    private UsuarioDTO usuario;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setUsuario(UsuarioDTO usuarioDTO) {
        this.usuario = usuarioDTO;
        Sesion.setUsuarioActual(usuarioDTO);
    }

    @FXML
    private void handleCrearEnvio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio_user.fxml"));
            Parent root = loader.load();
            CrearEnvioUserController ctrl = loader.getController();
            ctrl.setUsuarioActual(usuario != null ? usuario : Sesion.getUsuarioActual());
            ctrl.setOnEnvioCreado(() -> mostrarAlerta("Éxito", "El envío se registró correctamente."));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Envío");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditarUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar_usuario_user.fxml"));
            Parent root = loader.load();

            EditarUsuarioUserController controller = loader.getController();
            controller.setUsuario(usuario != null ? usuario : Sesion.getUsuarioActual());
            controller.setOnUsuarioEditado(() -> mostrarAlerta("Éxito", "Usuario actualizado correctamente."));

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Editar Usuario");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "❌ Error al abrir la ventana de edición de usuario.");
        }
    }

    @FXML
    private void handleVerHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial_envios_user.fxml"));
            Parent root = loader.load();
            HistorialEnviosUserController ctrl = loader.getController();
            ctrl.setUsuario(usuario != null ? usuario : Sesion.getUsuarioActual());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Envíos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
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
