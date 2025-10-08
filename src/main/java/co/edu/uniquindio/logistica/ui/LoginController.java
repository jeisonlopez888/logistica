package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
}
