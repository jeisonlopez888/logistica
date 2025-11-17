package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de Login - Solo valida datos y se comunica con Facade.
 * No contiene lógica de negocio.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private javafx.scene.control.Button togglePasswordButton;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleLogin() {
        // 1. Validación de datos de entrada
        String email = ValidacionUtil.validarYLimpiarEmail(emailField.getText());
        // Obtener la contraseña del campo visible (puede ser passwordField o passwordVisibleField)
        String password = passwordField.isVisible() ? 
            (passwordField.getText() != null ? passwordField.getText() : "") :
            (passwordVisibleField.getText() != null ? passwordVisibleField.getText() : "");

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

            // Si es administrador, preguntar si quiere ingresar como admin o usuario regular
            boolean ingresarComoAdmin = userDTO.isAdmin();
            
            if (userDTO.isAdmin()) {
                Optional<Boolean> resultadoIngreso = preguntarTipoIngreso();
                if (!resultadoIngreso.isPresent()) {
                    // Si el usuario canceló el diálogo, no hacer nada
                    mostrarMensaje("Ingreso cancelado", "orange");
                    return;
                }
                ingresarComoAdmin = resultadoIngreso.get();
            }

            try {
                // Crear una copia del DTO con el rol seleccionado (solo para esta sesión)
                UsuarioDTO usuarioParaSesion = crearCopiaConRol(userDTO, ingresarComoAdmin);
                Sesion.setUsuarioActual(usuarioParaSesion);

                String fxml = ingresarComoAdmin ? "/fxml/admin.fxml" : "/fxml/user.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();

                if (ingresarComoAdmin) {
                    AdminController ctrl = loader.getController();
                    ctrl.setUsuario(usuarioParaSesion);
                } else {
                    UserController ctrl = loader.getController();
                    ctrl.setUsuario(usuarioParaSesion);
                }

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(ingresarComoAdmin ? "Panel de Administrador" : "Panel de Usuario");
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

    /**
     * Pregunta al administrador si desea ingresar como administrador o como usuario regular.
     * @return Optional con true si elige administrador, false si elige usuario regular, 
     *         o Optional vacío si cancela
     */
    private Optional<Boolean> preguntarTipoIngreso() {
        List<String> opciones = Arrays.asList("Administrador", "Usuario Regular");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Administrador", opciones);
        dialog.setTitle("Seleccionar Tipo de Ingreso");
        dialog.setHeaderText("Eres un administrador");
        dialog.setContentText("¿Cómo deseas ingresar?");
        
        Optional<String> resultado = dialog.showAndWait();
        
        if (resultado.isPresent()) {
            return Optional.of(resultado.get().equals("Administrador"));
        }
        
        // Si cancela, retornar Optional vacío
        return Optional.empty();
    }

    /**
     * Crea una copia del UsuarioDTO con el rol modificado (solo para esta sesión).
     * No modifica el usuario original en la base de datos.
     */
    private UsuarioDTO crearCopiaConRol(UsuarioDTO original, boolean comoAdmin) {
        UsuarioDTO copia = new UsuarioDTO();
        copia.setId(original.getId());
        copia.setNombre(original.getNombre());
        copia.setEmail(original.getEmail());
        copia.setTelefono(original.getTelefono());
        copia.setPassword(original.getPassword());
        copia.setAdmin(comoAdmin);
        copia.setDirecciones(original.getDirecciones());
        copia.setMetodosPago(original.getMetodosPago());
        return copia;
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
