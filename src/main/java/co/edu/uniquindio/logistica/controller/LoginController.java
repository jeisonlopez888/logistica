package co.edu.uniquindio.logistica.controller;

import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField telefonoField;

    @FXML
    private Label mensajeLabel;

    private final DataStore dataStore = DataStore.getInstance();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String telefono = telefonoField.getText();

        Usuario user = dataStore.getUsuarios().values().stream()
                .filter(u -> u.getEmail().equals(email) && u.getTelefono().equals(telefono))
                .findFirst()
                .orElse(null);

        if (user != null) {
            mensajeLabel.setText("Bienvenido, " + user.getNombre() + "!");
            mensajeLabel.setStyle("-fx-text-fill: green;");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
                Parent root = loader.load();

                // Pasar el usuario al UserController
                UserController userController = loader.getController();
                userController.setUsuario(user);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Panel de Usuario");
                stage.show();

                // cerrar la ventana de login
                ((Stage) emailField.getScene().getWindow()).close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mensajeLabel.setText("Credenciales incorrectas");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }



    }
}

