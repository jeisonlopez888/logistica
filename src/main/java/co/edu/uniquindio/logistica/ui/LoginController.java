package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label mensajeLabel;

    // Usamos Singleton
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        Usuario user = facade.login(email, password);

        if (user != null) {
            Sesion.setUsuarioActual(user);
            mensajeLabel.setText("Bienvenido, " + user.getNombre() + "!");
            mensajeLabel.setStyle("-fx-text-fill: green;");

            try {
                // Si es admin abrimos admin.fxml, si no user.fxml
                String fxml = user.isAdmin() ? "/fxml/admin.fxml" : "/fxml/user.fxml";

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();

                if (user.isAdmin()) {
                    AdminController ctrl = loader.getController();
                    ctrl.setFacade(facade);
                } else {
                    UserController ctrl = loader.getController();
                    ctrl.setUsuario(user);
                }

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(user.isAdmin() ? "Panel de Administrador" : "Panel de Usuario");
                stage.show();

                // cerrar login
                Stage current = (Stage) emailField.getScene().getWindow();
                current.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            mensajeLabel.setText("❌ Credenciales incorrectas");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleAbrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registro.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRastreo(ActionEvent event) {
        try {
            Sesion.cerrarSesion();

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/rastreo.fxml"));
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
