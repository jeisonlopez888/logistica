package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.RepartidorDTO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de Historial de Envíos para Administradores - Solo valida y usa DTOs
 */
public class HistorialEnviosAdminController extends HistorialEnviosController {

    @FXML
    protected TableColumn<EnvioDTO, String> colRepartidor;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        tablaEnvios.setEditable(true);

        // ComboBox para cambiar el estado del envío (usando EnvioDTO.EstadoEnvio)
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(EnvioDTO.EstadoEnvio.values())
        ));

        colEstado.setOnEditCommit(event -> {
            EnvioDTO envioDTO = event.getRowValue();
            EnvioDTO.EstadoEnvio nuevo = event.getNewValue();

            // Actualizar DTO según el nuevo estado
            switch (nuevo) {
                case CONFIRMADO -> {
                    envioDTO.setEstado(EnvioDTO.EstadoEnvio.CONFIRMADO);
                    envioDTO.setFechaConfirmacion(LocalDateTime.now());
                    facade.asignarRepartidor(envioDTO.getId());
                }
                case EN_RUTA -> envioDTO.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));
                case ENTREGADO -> envioDTO.setFechaEntrega(LocalDateTime.now());
                case INCIDENCIA -> {
                    String desc = solicitarIncidenciaDialog(envioDTO.getIncidenciaDescripcion());
                    if (desc != null && !desc.trim().isEmpty()) {
                        envioDTO.setIncidenciaDescripcion(desc);
                        envioDTO.setFechaIncidencia(LocalDateTime.now());
                        envioDTO.setEstado(EnvioDTO.EstadoEnvio.INCIDENCIA);
                    } else {
                        return; // No actualizar si se cancela
                    }
                }
                default -> envioDTO.setEstado(nuevo);
            }

            // Registrar cambios a través de Facade (con DTOs)
            facade.registrarEnvio(envioDTO);
            tablaEnvios.refresh();
        });

        // ComboBox para reasignar repartidor
        List<String> nombresRepartidores = facade.listarRepartidores().stream()
                .map(RepartidorDTO::getNombre)
                .collect(Collectors.toList());

        colRepartidor.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(nombresRepartidores)
        ));

        colRepartidor.setOnEditCommit(event -> {
            EnvioDTO envioDTO = event.getRowValue();
            String nombreRepartidor = event.getNewValue();

            if (nombreRepartidor == null || nombreRepartidor.isBlank()) return;

            RepartidorDTO repartidorDTO = facade.buscarRepartidorPorNombre(nombreRepartidor);
            if (repartidorDTO != null) {
                envioDTO.setIdRepartidor(repartidorDTO.getId());
                envioDTO.setEstado(EnvioDTO.EstadoEnvio.ASIGNADO);
                envioDTO.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));
                // Permitir múltiples envíos por repartidor
                // facade.marcarRepartidorNoDisponible(repartidorDTO.getId());
                facade.registrarEnvio(envioDTO);
                mostrarAlerta("Asignado", "Repartidor '" + repartidorDTO.getNombre() + "' asignado correctamente.");
                cargarHistorial();
            } else {
                mostrarAlerta("Error", "No se encontró el repartidor seleccionado.");
            }
        });
    }

    @FXML
    @Override
    protected void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al panel anterior.");
        }
    }

    @FXML
    @Override
    protected void editarEnvio(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        abrirEditorPara(envioDTO, "/fxml/editar_envio.fxml");
    }

    @FXML
    @Override
    protected void eliminarEnvio(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioDTO == null) {
            mostrarAlerta("Selecciona un envío", "Debes seleccionar un envío para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que deseas eliminar el envío #" + envioDTO.getId() + "?");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                facade.eliminarEnvio(envioDTO.getId());
                cargarHistorial();
                mostrarAlerta("Eliminado", "El envío fue eliminado correctamente.");
            }
        });
    }

    @FXML
    @Override
    protected void verIncidencia(ActionEvent event) {
        EnvioDTO envioDTO = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioDTO == null) {
            mostrarAlerta("Selecciona un envío", "Debes seleccionar un envío primero.");
            return;
        }

        String incidencia = facade.obtenerIncidencia(envioDTO.getId());
        mostrarAlerta("Incidencia", incidencia);
    }
}
