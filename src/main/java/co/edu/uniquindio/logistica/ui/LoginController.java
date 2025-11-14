package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de Login - Solo valida datos y se comunica con Facade.
 * No contiene lógica de negocio.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleLogin() {
        // 1. Validación de datos de entrada
        String email = ValidacionUtil.validarYLimpiarEmail(emailField.getText());
        String password = passwordField.getText() != null ? passwordField.getText() : "";

        if (!ValidacionUtil.esEmailValido(email)) {
            mostrarMensaje("❌ Email inválido", "red");
            return;
        }

        if (!ValidacionUtil.esPasswordValido(password)) {
            mostrarMensaje("❌ La contraseña debe tener al menos 4 caracteres", "red");
            return;
        }

        // 2. Comunicación con Facade (trabaja con DTOs)
        UsuarioDTO userDTO = facade.login(email, password);

        // 3. Manejo de respuesta y navegación
        if (userDTO != null) {
            Sesion.setUsuarioActual(userDTO);
            mostrarMensaje("Bienvenido, " + userDTO.getNombre() + "!", "green");

            try {
                String fxml = userDTO.isAdmin() ? "/fxml/admin.fxml" : "/fxml/user.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();

                if (userDTO.isAdmin()) {
                    AdminController ctrl = loader.getController();
                    ctrl.setUsuario(userDTO);
                } else {
                    UserController ctrl = loader.getController();
                    ctrl.setUsuario(userDTO);
                }

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(userDTO.isAdmin() ? "Panel de Administrador" : "Panel de Usuario");
                stage.show();

                ((Stage) emailField.getScene().getWindow()).close();

            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarMensaje("❌ Error al abrir panel: " + ex.getMessage(), "red");
            }

        } else {
            mostrarMensaje("❌ Credenciales incorrectas", "red");
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
            ((Stage) emailField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir registro", "red");
        }
    }

    @FXML
    private void handleRastreo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rastreo.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Rastreo de Envíos");
            stage.show();
            ((Stage) emailField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir rastreo", "red");
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
