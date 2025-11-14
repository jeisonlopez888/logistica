package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador para usuarios - Solo valida y usa DTOs
 */
public class CrearEnvioUserController extends CrearEnvioController {
    
    @FXML
    protected void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/User.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al panel de usuario.");
        }
    }

    @Override
    public void setEnvioToEdit(EnvioDTO envioDTO) {
        if (envioDTO == null) return;

        boolean esAdmin = usuarioActual != null && usuarioActual.isAdmin();
        EnvioDTO.EstadoEnvio estado = envioDTO.getEstado();

        // El usuario solo puede editar si está SOLICITADO
        if (!esAdmin && estado != EnvioDTO.EstadoEnvio.SOLICITADO) {
            mostrarMensaje("🚫 No puedes editar un envío con estado " + estado + ".", "red");
            return;
        }

        if (envioDTO.getOrigen() != null) {
            origenDireccionField.setText(envioDTO.getOrigen().getCalle());
            zonaOrigenCombo.setValue(envioDTO.getOrigen().getCiudad());
        }
        if (envioDTO.getDestino() != null) {
            destinoDireccionField.setText(envioDTO.getDestino().getCalle());
            zonaDestinoCombo.setValue(envioDTO.getDestino().getCiudad());
        }
        pesoField.setText(String.valueOf(envioDTO.getPeso()));
        prioridadCheck.setSelected(envioDTO.isPrioridad());
        seguroCheck.setSelected(envioDTO.isSeguro());
        if (fragilCheck != null) fragilCheck.setSelected(envioDTO.isFragil());
        if (firmaRequeridaCheck != null) firmaRequeridaCheck.setSelected(envioDTO.isFirmaRequerida());
        costoLabel.setText(String.format("$ %.2f", envioDTO.getCostoEstimado()));
    }
}
