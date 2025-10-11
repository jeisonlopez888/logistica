package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarUsuarioController {

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheck;
    @FXML private Label mensajeLabel;

    private Usuario usuario;
    private Runnable onUsuarioEditado;

    private LogisticaFacade facade = LogisticaFacade.getInstance();
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            idField.setText(usuario.getId() != null ? String.valueOf(usuario.getId()) : "");
            nombreField.setText(usuario.getNombre() != null ? usuario.getNombre() : "");
            emailField.setText(usuario.getEmail() != null ? usuario.getEmail() : "");
            telefonoField.setText(usuario.getTelefono() != null ? usuario.getTelefono() : "");
            passwordField.setText(usuario.getPassword() != null ? usuario.getPassword() : "");
            adminCheck.setSelected(usuario.isAdmin());
        }
    }

    public void setOnUsuarioEditado(Runnable callback) {
        this.onUsuarioEditado = callback;
    }

    @FXML
    private void handleGuardar() {
        try {
            // Validar campos obligatorios
            String idText = idField.getText().trim();
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String password = passwordField.getText().trim();
            boolean admin = adminCheck.isSelected();

            if (idText.isEmpty() || nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            Long nuevoId = Long.parseLong(idText);

            // Validar si ya existe otro usuario con el mismo ID
            boolean existeOtro = facade.listarUsuarios().stream()
                    .anyMatch(u -> !u.equals(usuario) && u.getId().equals(nuevoId));

            if (existeOtro) {
                mostrarMensaje("❌ Ya existe otro usuario con esa cédula", "red");
                return;
            }

            // Actualizar datos
            usuario.setId(nuevoId);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setPassword(password);
            usuario.setAdmin(admin);

            mostrarMensaje("✅ Usuario actualizado correctamente", "green");

            // Refrescar tabla en AdminController
            if (onUsuarioEditado != null) {
                onUsuarioEditado.run();
            }

            // Cerrar ventana después de guardar
            cerrar();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ El ID debe ser numérico", "red");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("⚠️ Error al actualizar usuario", "red");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void cerrar() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
}
