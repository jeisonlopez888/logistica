package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CrearUsuarioController {

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheck;
    @FXML private Label mensajeLabel;

    private LogisticaFacade facade;
    private Runnable onUsuarioCreado; // 🔹 callback para refrescar la tabla en AdminController

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
    }

    public void setOnUsuarioCreado(Runnable onUsuarioCreado) {
        this.onUsuarioCreado = onUsuarioCreado;
    }

    @FXML
    private void handleRegistrar() {
        try {
            Long id = Long.parseLong(idField.getText());
            String nombre = nombreField.getText();
            String email = emailField.getText();
            String telefono = telefonoField.getText();
            String password = passwordField.getText();
            boolean admin = adminCheck.isSelected();

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            Usuario nuevo = facade.crearUsuario(id, nombre, email, telefono, password, admin);
            mostrarMensaje("✅ Usuario creado: " + nuevo.getNombre(), "green");

            limpiarCampos();

            // 🔹 Notificar al AdminController que se creó un nuevo usuario
            if (onUsuarioCreado != null) {
                onUsuarioCreado.run();
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ El ID debe ser un número válido", "red");
        } catch (Exception e) {
            mostrarMensaje("⚠️ Error al crear el usuario", "red");
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void limpiarCampos() {
        idField.clear();
        nombreField.clear();
        emailField.clear();
        telefonoField.clear();
        passwordField.clear();
        adminCheck.setSelected(false);
    }
}
