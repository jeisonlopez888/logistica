package co.edu.uniquindio.logistica.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CrearUsuarioAdminController extends CrearUsuarioController {

    @Override
    protected boolean getAdminFlag() {
        return adminCheck != null && adminCheck.isSelected();
    }

    @Override
    protected void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/usuarios.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ No se pudo volver a usuarios", "red");
        }
    }
}

