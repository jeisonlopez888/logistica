package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CrearUsuarioController {

    @FXML private TextField idField;        // 🔹 cédula
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheck;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleRegistrar() {
        try {
            Long id = Long.parseLong(idField.getText()); // 🔹 leer cédula
            String nombre = nombreField.getText();
            String email = emailField.getText();
            String telefono = telefonoField.getText();
            String password = passwordField.getText();
            boolean admin = adminCheck.isSelected();

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
                mensajeLabel.setText("❌ Todos los campos son obligatorios");
                mensajeLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            Usuario nuevo = facade.crearUsuario(id, nombre, email, telefono, password, admin);
            mensajeLabel.setText("✅ Usuario creado: " + nuevo.getNombre());
            mensajeLabel.setStyle("-fx-text-fill: green;");

            // limpiar campos
            idField.clear();
            nombreField.clear();
            emailField.clear();
            telefonoField.clear();
            passwordField.clear();
            adminCheck.setSelected(false);

        } catch (NumberFormatException e) {
            mensajeLabel.setText("❌ El ID debe ser un número válido");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
