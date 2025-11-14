package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Controlador de Historial de Envíos para Usuarios - Solo valida y usa DTOs
 */
public class HistorialEnviosUserController extends HistorialEnviosController {

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    @Override
    protected void editarEnvio(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioDTO == null) {
            mostrarAlerta("Selecciona un envío", "Debes seleccionar un envío.");
            return;
        }

        if (envioDTO.getEstado() != EnvioDTO.EstadoEnvio.SOLICITADO) {
            mostrarAlerta("No editable", "Solo puedes editar envíos solicitados.");
            return;
        }

        abrirEditorPara(envioDTO, "/fxml/editar_envio.fxml");
    }

    @FXML
    @Override
    protected void eliminarEnvio(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioDTO == null) {
            mostrarAlerta("Selecciona un envío", "Selecciona un envío para cancelar.");
            return;
        }

        if (envioDTO.getEstado() != EnvioDTO.EstadoEnvio.SOLICITADO) {
            mostrarAlerta("No permitido", "Solo puedes cancelar envíos solicitados.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar envío");
        confirm.setHeaderText("¿Deseas cancelar el envío con ID " + envioDTO.getId() + "?");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                envioDTO.setEstado(EnvioDTO.EstadoEnvio.CANCELADO);
                facade.registrarEnvio(envioDTO);
                cargarHistorial();
            }
        });
    }

    @FXML
    @Override
    protected void verIncidencia(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioDTO == null) {
            mostrarAlerta("Selecciona un envío", "Selecciona un envío para ver la incidencia.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Incidencia del envío");
        alert.setHeaderText("Detalles de la incidencia");
        String desc = envioDTO.getIncidenciaDescripcion() != null && !envioDTO.getIncidenciaDescripcion().isEmpty() 
                ? envioDTO.getIncidenciaDescripcion() 
                : "No hay descripción registrada.";
        String fecha = envioDTO.getFechaIncidencia() != null 
                ? envioDTO.getFechaIncidencia().toString() 
                : "Sin fecha registrada.";
        alert.setContentText("📅 Fecha: " + fecha + "\n\n📝 Descripción:\n" + desc);
        alert.showAndWait();
    }

    @FXML
    @Override
    protected void handleVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/User.fxml"));
            Parent root = loader.load();
            UserController userController = loader.getController();
            userController.setUsuario(Sesion.getUsuarioActual());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al panel de usuario.");
        }
    }
}
