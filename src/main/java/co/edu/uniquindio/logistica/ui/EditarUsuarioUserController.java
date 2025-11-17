package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
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

/**
 * Controlador para editar usuario propio - Solo valida y usa DTOs
 */
public class EditarUsuarioUserController {

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Button togglePasswordButton;
    @FXML private Label mensajeLabel;

    @FXML private TextField alias1Field;
    @FXML private TextField detalle1Field;
    @FXML private TextField alias2Field;
    @FXML private TextField detalle2Field;
    @FXML private ComboBox<String> zonaOrigenCombo;
    @FXML private ComboBox<String> zonaDestinoCombo;

    private UsuarioDTO usuarioDTO; // usuario a editar
    private Runnable onUsuarioEditado;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setUsuario(UsuarioDTO usuarioDTO) {
        this.usuarioDTO = usuarioDTO;
        if (usuarioDTO != null) {
            idField.setText(String.valueOf(usuarioDTO.getId()));
            nombreField.setText(usuarioDTO.getNombre());
            emailField.setText(usuarioDTO.getEmail());
            telefonoField.setText(usuarioDTO.getTelefono());
            // Sincronizar contraseña en ambos campos
            passwordField.setText(usuarioDTO.getPassword());
            passwordVisibleField.setText(usuarioDTO.getPassword());

            if (!usuarioDTO.getDirecciones().isEmpty()) {
                // Cargar hasta 2 direcciones (si existen)
                if (usuarioDTO.getDirecciones().size() > 0) {
                    DireccionDTO d1 = usuarioDTO.getDirecciones().get(0);
                    alias1Field.setText(d1.getAlias());
                    detalle1Field.setText(d1.getCalle());
                    zonaOrigenCombo.setValue(d1.getCiudad());
                }
                if (usuarioDTO.getDirecciones().size() > 1) {
                    DireccionDTO d2 = usuarioDTO.getDirecciones().get(1);
                    alias2Field.setText(d2.getAlias());
                    detalle2Field.setText(d2.getCalle());
                    zonaDestinoCombo.setValue(d2.getCiudad());
                }
            }
        }
    }

    public void setOnUsuarioEditado(Runnable onUsuarioEditado) {
        this.onUsuarioEditado = onUsuarioEditado;
    }

    @FXML
    private void initialize() {
        zonaOrigenCombo.getItems().addAll("Sur", "Centro", "Norte");
        zonaDestinoCombo.getItems().addAll("Sur", "Centro", "Norte");
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        try {
            if (usuarioDTO == null) {
                mostrarMensaje("⚠️ No hay usuario seleccionado", "orange");
                return;
            }

            // Obtener la contraseña del campo visible
            String password = passwordField.isVisible() ? 
                (passwordField.getText() != null ? passwordField.getText() : "") :
                (passwordVisibleField.getText() != null ? passwordVisibleField.getText() : "");
            
            // Validación
            if (ValidacionUtil.isEmpty(nombreField.getText()) ||
                    ValidacionUtil.isEmpty(emailField.getText()) ||
                    ValidacionUtil.isEmpty(telefonoField.getText()) ||
                    ValidacionUtil.isEmpty(password)) {
                mostrarMensaje("⚠️ Todos los campos son obligatorios", "orange");
                return;
            }

            if (!ValidacionUtil.isEmailValid(emailField.getText())) {
                mostrarMensaje("❌ Formato de email inválido", "red");
                return;
            }

            if (!ValidacionUtil.isPhoneValid(telefonoField.getText())) {
                mostrarMensaje("❌ Formato de teléfono inválido", "red");
                return;
            }

            // Actualizar DTO (no se puede cambiar el rol de admin)
            usuarioDTO.setNombre(nombreField.getText().trim());
            usuarioDTO.setEmail(ValidacionUtil.validarYLimpiarEmail(emailField.getText()));
            usuarioDTO.setTelefono(telefonoField.getText().trim());
            usuarioDTO.setPassword(password);

            // Actualizar direcciones
            List<DireccionDTO> direccionesDTO = new ArrayList<>();
            String zonaOrigen = zonaOrigenCombo.getValue();
            String zonaDestino = zonaDestinoCombo.getValue();

            if (zonaOrigen != null && !ValidacionUtil.isEmpty(detalle1Field.getText())) {
                // crearDireccion(String nombre, String calle, String ciudad, String coordenadas)
                DireccionDTO d1 = facade.crearDireccion(
                        alias1Field.getText().isEmpty() ? "Origen" : alias1Field.getText(),
                        detalle1Field.getText(),
                        zonaOrigen,
                        ""
                );
                direccionesDTO.add(d1);
            }

            if (zonaDestino != null && !ValidacionUtil.isEmpty(detalle2Field.getText())) {
                // crearDireccion(String nombre, String calle, String ciudad, String coordenadas)
                DireccionDTO d2 = facade.crearDireccion(
                        alias2Field.getText().isEmpty() ? "Destino" : alias2Field.getText(),
                        detalle2Field.getText(),
                        zonaDestino,
                        ""
                );
                direccionesDTO.add(d2);
            }

            usuarioDTO.setDirecciones(direccionesDTO);

            // Actualizar usuario usando Facade (trabaja con DTOs)
            facade.actualizarUsuario(usuarioDTO);

            mostrarMensaje("✅ Usuario actualizado correctamente", "green");

            if (onUsuarioEditado != null) onUsuarioEditado.run();

            // Cerrar ventana
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("⚠️ Error al actualizar usuario: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/User.fxml"));
            Parent root = loader.load();
            UserController userController = loader.getController();
            userController.setUsuario(Sesion.getUsuarioActual());
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Usuario");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Alterna entre mostrar y ocultar la contraseña.
     */
    @FXML
    private void togglePasswordVisibility() {
        if (passwordField.isVisible()) {
            // Cambiar a mostrar contraseña
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            // Cambiar a ocultar contraseña
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            togglePasswordButton.setText("👁");
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
