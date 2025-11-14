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
 * Controlador de Usuario Admin - Solo valida y usa DTOs
 */
public class UserAdminController {

    private UsuarioDTO usuario;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setUsuario(UsuarioDTO usuarioDTO) {
        this.usuario = usuarioDTO;
        Sesion.setUsuarioActual(usuarioDTO);
    }

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        setUsuario(usuarioDTO);
    }

    @FXML
    private void handleCrearEnvioAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio_admin.fxml"));
            Parent root = loader.load();
            CrearEnvioAdminController ctrl = loader.getController();
            ctrl.setUsuarioActual(usuario != null ? usuario : Sesion.getUsuarioActual());
            ctrl.setOnEnvioCreado(() -> mostrarAlerta("Éxito", "El envío se registró correctamente."));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Envío (Admin)");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerHistorialAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial_envios_admin.fxml"));
            Parent root = loader.load();
            HistorialEnviosAdminController ctrl = loader.getController();
            ctrl.setUsuario(usuario != null ? usuario : Sesion.getUsuarioActual());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Envíos (Admin)");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolverAdmin(ActionEvent event) {
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
