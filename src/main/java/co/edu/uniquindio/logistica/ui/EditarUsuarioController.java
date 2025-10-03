package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarUsuarioController {

    @FXML private TextField idField;           // cédula
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheck;
    @FXML private Label mensajeLabel;

    private Usuario usuario;
    private LogisticaFacade facade;
    private Runnable onUsuarioEditado;

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        // llenar campos
        idField.setText(usuario.getId() != null ? String.valueOf(usuario.getId()) : "");
        nombreField.setText(usuario.getNombre() != null ? usuario.getNombre() : "");
        emailField.setText(usuario.getEmail() != null ? usuario.getEmail() : "");
        telefonoField.setText(usuario.getTelefono() != null ? usuario.getTelefono() : "");
        passwordField.setText(usuario.getPassword() != null ? usuario.getPassword() : "");
        adminCheck.setSelected(usuario.isAdmin());
    }

    public void setOnUsuarioEditado(Runnable callback) {
        this.onUsuarioEditado = callback;
    }

    @FXML
    private void handleGuardar() {
        // Validaciones básicas
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            mensajeLabel.setText("❌ La cédula es obligatoria");
            mensajeLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            Long newId = Long.parseLong(idText);

            // Validar unicidad (permitir mismo id si es el mismo usuario)
            boolean existeOtro = facade.listarUsuarios().stream()
                    .anyMatch(u -> u.getId() != null && u.getId().equals(newId) && !u.equals(usuario));

            if (existeOtro) {
                mensajeLabel.setText("❌ Ya existe otro usuario con esa cédula");
                mensajeLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Validar campos requeridos
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String password = passwordField.getText();

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
                mensajeLabel.setText("❌ Todos los campos son obligatorios");
                mensajeLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Guardar cambios en el objeto usuario
            usuario.setId(newId);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setPassword(password);
            usuario.setAdmin(adminCheck.isSelected());

            mensajeLabel.setText("✅ Usuario actualizado");
            mensajeLabel.setStyle("-fx-text-fill: green;");

            // refrescar lista en la UI que abrió este diálogo
            if (onUsuarioEditado != null) onUsuarioEditado.run();

            // cerrar ventana
            cerrar();

        } catch (NumberFormatException ex) {
            mensajeLabel.setText("❌ Cédula inválida (debe ser numérica)");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
}
