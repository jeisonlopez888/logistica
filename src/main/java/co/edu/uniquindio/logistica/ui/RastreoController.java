package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
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

/**
 * Controlador de Rastreo - Solo valida datos y se comunica con Facade usando DTOs
 */
public class RastreoController {

    @FXML private TextField idEnvioField;
    @FXML private Label resultadoLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleBuscar() {
        // Validación de datos de entrada
        String idTexto = idEnvioField.getText();
        if (ValidacionUtil.isEmpty(idTexto)) {
            resultadoLabel.setText("❌ Ingrese un ID de envío");
            resultadoLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            long id = Long.parseLong(idTexto);
            
            if (!ValidacionUtil.esIdValido(id)) {
                resultadoLabel.setText("❌ ID inválido");
                resultadoLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Comunicación con Facade (trabaja con DTOs)
            EnvioDTO envioDTO = facade.buscarEnvioPorId(id);
            
            if (envioDTO != null) {
                String estado = envioDTO.getEstado() != null ? envioDTO.getEstado().name() : "Sin estado";
                String info = String.format("📦 Envío #%d\nEstado: %s\nOrigen: %s\nDestino: %s",
                        envioDTO.getId(),
                        estado,
                        envioDTO.getOrigen() != null ? envioDTO.getOrigen().toString() : "N/A",
                        envioDTO.getDestino() != null ? envioDTO.getDestino().toString() : "N/A");
                resultadoLabel.setText(info);
                resultadoLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
            } else {
                resultadoLabel.setText("❌ Envío no encontrado");
                resultadoLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            resultadoLabel.setText("❌ ID debe ser un número válido");
            resultadoLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            e.printStackTrace();
            resultadoLabel.setText("❌ Error al buscar envío");
            resultadoLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
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
