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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de Historial de Envíos para Administradores - Solo valida y usa DTOs
 */
public class HistorialEnviosAdminController extends HistorialEnviosController {

    @FXML
    protected TableColumn<EnvioDTO, String> colRepartidor;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private Button imprimirFacturaBtn;
    
    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        tablaEnvios.setEditable(true);
        
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
                    // Intentar asignar repartidor disponible en la zona de destino
                    boolean asignado = facade.asignarRepartidor(envioDTO.getId());
                    if (asignado) {
                        // Si se asignó repartidor, obtener el envío actualizado para reflejar el estado ASIGNADO
                        EnvioDTO envioActualizado = facade.buscarEnvioPorId(envioDTO.getId());
                        if (envioActualizado != null) {
                            envioDTO.setEstado(envioActualizado.getEstado());
                            envioDTO.setIdRepartidor(envioActualizado.getIdRepartidor());
                            envioDTO.setFechaEntregaEstimada(envioActualizado.getFechaEntregaEstimada());
                        }
                    }
                }
                case EN_RUTA -> {
                    envioDTO.setEstado(EnvioDTO.EstadoEnvio.EN_RUTA);
                    envioDTO.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));
                }
                case ENTREGADO -> {
                    envioDTO.setEstado(EnvioDTO.EstadoEnvio.ENTREGADO);
                    envioDTO.setFechaEntrega(LocalDateTime.now());
                }
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
            // Recargar el historial desde el DataStore para reflejar los cambios
            cargarHistorial();
        });

        // ComboBox para reasignar repartidor
        List<String> nombresRepartidores = facade.obtenerNombresRepartidores();

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
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar Factura de Envío");
            fileChooser.setInitialFileName("factura_envio_" + envioDTO.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            Stage stage = (Stage) tablaEnvios.getScene().getWindow();
            java.io.File file = fileChooser.showSaveDialog(stage);
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
