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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * Controlador de Historial de Envíos para Usuarios - Solo valida y usa DTOs
 */
public class HistorialEnviosUserController extends HistorialEnviosController {

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    
    @FXML
    private Button imprimirFacturaBtn;

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        
        // Inicializar filtros
        if (estadoFilter != null) {
            estadoFilter.getItems().addAll("SOLICITADO", "CONFIRMADO", "ASIGNADO", "EN_RUTA", "ENTREGADO", "CANCELADO", "INCIDENCIA");
        }
        
        if (fechaFinFilter != null) {
            fechaFinFilter.setValue(java.time.LocalDate.now());
        }
        
        // Inicialmente deshabilitar el botón
        if (imprimirFacturaBtn != null) {
            imprimirFacturaBtn.setDisable(true);
        }
        
        // Habilitar/deshabilitar botón de imprimir factura según selección y estado
        tablaEnvios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (imprimirFacturaBtn != null) {
                boolean habilitado = newVal != null && 
                    (newVal.getEstado() == EnvioDTO.EstadoEnvio.CONFIRMADO ||
                     newVal.getEstado() == EnvioDTO.EstadoEnvio.ASIGNADO ||
                     newVal.getEstado() == EnvioDTO.EstadoEnvio.EN_RUTA ||
                     newVal.getEstado() == EnvioDTO.EstadoEnvio.ENTREGADO);
                imprimirFacturaBtn.setDisable(!habilitado);
            }
        });
    }

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
    
    @FXML
    private void imprimirFactura(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioDTO == null) {
            mostrarAlerta("Selecciona un envío", "Debes seleccionar un envío para imprimir la factura.");
            return;
        }
        
        // Solo permitir imprimir factura si el envío está confirmado
        if (envioDTO.getEstado() != EnvioDTO.EstadoEnvio.CONFIRMADO && 
            envioDTO.getEstado() != EnvioDTO.EstadoEnvio.ASIGNADO &&
            envioDTO.getEstado() != EnvioDTO.EstadoEnvio.EN_RUTA &&
            envioDTO.getEstado() != EnvioDTO.EstadoEnvio.ENTREGADO) {
            mostrarAlerta("Envío no confirmado", "Solo se puede imprimir la factura de envíos confirmados.");
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Factura de Envío");
            fileChooser.setInitialFileName("factura_envio_" + envioDTO.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            Stage stage = (Stage) tablaEnvios.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                facade.generarFacturaEnvio(envioDTO.getId(), file.getAbsolutePath());
                mostrarAlerta("Factura Generada", "La factura se ha guardado exitosamente en:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar la factura: " + e.getMessage());
        }
    }
}
