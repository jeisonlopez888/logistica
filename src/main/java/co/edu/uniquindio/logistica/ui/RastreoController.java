package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RastreoController {

    @FXML private TextField idEnvioField;
    @FXML private Label resultadoLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleBuscar() {
        try {
            long id = Long.parseLong(idEnvioField.getText());
            Envio envio = facade.buscarEnvioPorId(id);
            if (envio != null) {
                resultadoLabel.setText("📦 Estado: " + envio.getEstado());
                resultadoLabel.setStyle("-fx-text-fill:green;");
            } else {
                resultadoLabel.setText("❌ Envío no encontrado");
                resultadoLabel.setStyle("-fx-text-fill:red;");
            }
        } catch (NumberFormatException e) {
            resultadoLabel.setText("❌ ID inválido");
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
}

