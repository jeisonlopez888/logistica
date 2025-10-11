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

public class CrearUsuarioController {

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheck;
    @FXML private Label mensajeLabel;

    // Campos para direcciones
    @FXML private TextField alias1Field;
    @FXML private TextField detalle1Field;
    @FXML private TextField alias2Field;
    @FXML private TextField detalle2Field;

    // 🔹 Nuevos campos de selección de zona
    @FXML private ComboBox<String> zonaOrigenCombo;
    @FXML private ComboBox<String> zonaDestinoCombo;

    private LogisticaFacade facade = LogisticaFacade.getInstance();
    private Runnable onUsuarioCreado;

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
    }

    public void setOnUsuarioCreado(Runnable onUsuarioCreado) {
        this.onUsuarioCreado = onUsuarioCreado;
    }

    @FXML
    private void initialize() {
        zonaOrigenCombo.getItems().addAll("Sur", "Centro", "Norte");
        zonaDestinoCombo.getItems().addAll("Sur", "Centro", "Norte");
    }

    @FXML
    private void handleRegistrar() {
        try {
            Long id = Long.parseLong(idField.getText());
            String nombre = nombreField.getText();
            String email = emailField.getText();
            String telefono = telefonoField.getText();
            String password = passwordField.getText();
            boolean admin = adminCheck != null && adminCheck.isSelected();

            String zonaOrigen = zonaOrigenCombo.getValue();
            String zonaDestino = zonaDestinoCombo.getValue();

            // Validaciones
            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty() ||
                    zonaOrigen == null || zonaDestino == null) {
                mostrarMensaje("❌ Todos los campos y zonas son obligatorios", "red");
                return;
            }

            boolean existe = facade.listarUsuarios().stream()
                    .anyMatch(u -> u.getId().equals(id) || u.getEmail().equalsIgnoreCase(email));
            if (existe) {
                mostrarMensaje("⚠️ Ya existe un usuario con esa cédula o correo", "orange");
                return;
            }

            Usuario nuevo = facade.crearUsuario(id, nombre, email, telefono, password, admin);

            // Crear lista de direcciones
            List<Direccion> direcciones = new ArrayList<>();

            // Dirección 1 (Origen)
            if (!detalle1Field.getText().isEmpty()) {
                Direccion d1 = new Direccion(
                        DataStore.getInstance().nextId(),

                        alias1Field.getText().isEmpty() ? "Origen" : alias1Field.getText(),
                        detalle1Field.getText(),
                        "", // campo ciudad vacío
                        zonaOrigen // 🔹 Guardamos la zona de origen en coordenadas
                );
                direcciones.add(d1);
            } else {
                mostrarMensaje("❌ Debes ingresar la dirección de origen", "red");
                return;
            }

            // Dirección 2 (Destino)
            if (!detalle2Field.getText().isEmpty()) {
                Direccion d2 = new Direccion(
                        DataStore.getInstance().nextId(),

                        alias2Field.getText().isEmpty() ? "Destino" : alias2Field.getText(),
                        detalle2Field.getText(),
                        "", // campo ciudad vacío
                        zonaDestino // 🔹 Guardamos la zona de destino en coordenadas
                );
                direcciones.add(d2);
            } else {
                mostrarMensaje("❌ Debes ingresar la dirección de destino", "red");
                return;
            }

            nuevo.setDirecciones(direcciones);

            mostrarMensaje("✅ Usuario creado correctamente", "green");
            limpiarCampos();

            if (onUsuarioCreado != null) onUsuarioCreado.run();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ El ID debe ser un número válido", "red");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("⚠️ Error al crear el usuario", "red");
        }
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
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

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill:" + color + ";");
    }

    private void limpiarCampos() {
        idField.clear();
        nombreField.clear();
        emailField.clear();
        telefonoField.clear();
        passwordField.clear();
        if (adminCheck != null) adminCheck.setSelected(false);
        alias1Field.clear();
        detalle1Field.clear();
        alias2Field.clear();
        detalle2Field.clear();
        zonaOrigenCombo.getSelectionModel().clearSelection();
        zonaDestinoCombo.getSelectionModel().clearSelection();
    }
}
