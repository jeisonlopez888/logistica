package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RegistroController {

    @FXML private TextField idField;          // Cédula del usuario
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;

    // Campos para las direcciones
    @FXML private TextField alias1Field;
    @FXML private TextField detalle1Field;
    @FXML private TextField ciudad1Field;
    @FXML private TextField alias2Field;
    @FXML private TextField detalle2Field;
    @FXML private TextField ciudad2Field;

    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleRegistrar() {
        try {
            // 🪪 Cédula como ID
            if (idField.getText().isEmpty()) {
                mostrarMensaje("❌ Debes ingresar la cédula del usuario", "red");
                return;
            }

            Long cedula = Long.parseLong(idField.getText());
            String nombre = nombreField.getText();
            String email = emailField.getText();
            String telefono = telefonoField.getText();
            String password = passwordField.getText();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            // 🔎 Validar que la cédula o correo no estén registrados
            boolean existe = facade.listarUsuarios().stream()
                    .anyMatch(u -> u.getId().equals(cedula) || u.getEmail().equalsIgnoreCase(email));

            if (existe) {
                mostrarMensaje("⚠️ Ya existe un usuario con esa cédula o correo", "orange");
                return;
            }

            // Crear usuario (no generamos ID automáticamente)
            Usuario nuevo = new Usuario(cedula, nombre, email, telefono, password, false);

            // Crear lista de direcciones
            List<Direccion> direcciones = new ArrayList<>();

            // Dirección 1 (obligatoria)
            if (!detalle1Field.getText().isEmpty() && !ciudad1Field.getText().isEmpty()) {
                Direccion d1 = new Direccion(
                        DataStore.getInstance().nextId(), // ID de dirección autogenerado
                        alias1Field.getText().isEmpty() ? "Casa" : alias1Field.getText(),
                        detalle1Field.getText(),
                        ciudad1Field.getText(),
                        ""
                );
                direcciones.add(d1);
            } else {
                mostrarMensaje("❌ Debes ingresar al menos una dirección válida", "red");
                return;
            }

            // Dirección 2 (opcional)
            if (!detalle2Field.getText().isEmpty() && !ciudad2Field.getText().isEmpty()) {
                Direccion d2 = new Direccion(
                        DataStore.getInstance().nextId(),
                        alias2Field.getText().isEmpty() ? "Trabajo" : alias2Field.getText(),
                        detalle2Field.getText(),
                        ciudad2Field.getText(),
                        ""
                );
                direcciones.add(d2);
            }

            nuevo.setDirecciones(direcciones);

            // Registrar usuario
            facade.registrarUsuario(nuevo);

            mostrarMensaje("✅ Registro exitoso, ya puedes iniciar sesión", "green");

            // Cerrar ventana tras éxito
            Stage stage = (Stage) mensajeLabel.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ La cédula debe ser numérica", "red");
        } catch (IllegalArgumentException ex) {
            mostrarMensaje("⚠️ " + ex.getMessage(), "orange");
        } catch (Exception e) {
            mostrarMensaje("❌ Error al registrar: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }


}
