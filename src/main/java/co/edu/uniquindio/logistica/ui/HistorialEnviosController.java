package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador abstracto base para historial de envíos - Trabaja con DTOs
 */
public abstract class HistorialEnviosController {

    @FXML protected TableView<EnvioDTO> tablaEnvios;
    @FXML protected TableColumn<EnvioDTO, Long> colId;
    @FXML protected TableColumn<EnvioDTO, String> colUsuario;
    @FXML protected TableColumn<EnvioDTO, String> colOrigenDireccion;
    @FXML protected TableColumn<EnvioDTO, String> colOrigenCiudad;
    @FXML protected TableColumn<EnvioDTO, String> colDestinoDireccion;
    @FXML protected TableColumn<EnvioDTO, String> colDestinoCiudad;
    @FXML protected TableColumn<EnvioDTO, Double> colPeso;
    @FXML protected TableColumn<EnvioDTO, Double> colVolumen;
    @FXML protected TableColumn<EnvioDTO, String> colPrioridad;
    @FXML protected TableColumn<EnvioDTO, String> colSeguro;
    @FXML protected TableColumn<EnvioDTO, String> colFragil;
    @FXML protected TableColumn<EnvioDTO, String> colFirmaRequerida;
    @FXML protected TableColumn<EnvioDTO, EnvioDTO.EstadoEnvio> colEstado;
    @FXML protected TableColumn<EnvioDTO, Double> colCostoEstimado;
    @FXML protected TableColumn<EnvioDTO, String> colFechaCreacion;
    @FXML protected TableColumn<EnvioDTO, String> colFechaConfirmacion;
    @FXML protected TableColumn<EnvioDTO, String> colFechaEntrega;
    @FXML protected TableColumn<EnvioDTO, String> colFechaEntregaEstimada;
    @FXML protected TableColumn<EnvioDTO, String> colRepartidor;
    
    // Filtros
    @FXML protected javafx.scene.control.DatePicker fechaInicioFilter;
    @FXML protected javafx.scene.control.DatePicker fechaFinFilter;
    @FXML protected javafx.scene.control.ComboBox<String> estadoFilter;
    
    // Lista completa de envíos (sin filtrar)
    private java.util.List<EnvioDTO> enviosCompletos;

    protected final LogisticaFacade facade = LogisticaFacade.getInstance();
    protected UsuarioDTO usuario;

    @FXML
    protected void initialize() {
        // Inicializar filtros
        if (estadoFilter != null) {
            estadoFilter.getItems().addAll("SOLICITADO", "CONFIRMADO", "ASIGNADO", "EN_RUTA", "ENTREGADO", "CANCELADO", "INCIDENCIA");
        }
        
        if (fechaFinFilter != null) {
            fechaFinFilter.setValue(java.time.LocalDate.now());
        }
        // ID
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        
        // Mostrar nombre del usuario, no solo ID
        colUsuario.setCellValueFactory(c -> {
            if (c.getValue().getIdUsuario() != null) {
                String nombre = facade.obtenerNombreUsuario(c.getValue().getIdUsuario());
                return new javafx.beans.property.SimpleStringProperty(nombre);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        // Origen - Dirección completa
        colOrigenDireccion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getOrigen() != null ? c.getValue().getOrigen().getCalle() : ""));
        
        // Origen - Zona (Sur, Centro, Norte)
        colOrigenCiudad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getOrigen() != null ? c.getValue().getOrigen().getCiudad() : ""));
        
        // Destino - Dirección completa
        colDestinoDireccion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDestino() != null ? c.getValue().getDestino().getCalle() : ""));
        
        // Destino - Zona (Sur, Centro, Norte)
        colDestinoCiudad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDestino() != null ? c.getValue().getDestino().getCiudad() : ""));
        
        // Peso y Volumen
        colPeso.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPeso()));
        colVolumen.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getVolumen()));
        
        // Opciones adicionales - Convertir true/false a SI/NO
        colPrioridad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().isPrioridad() ? "SÍ" : "NO"));
        colSeguro.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().isSeguro() ? "SÍ" : "NO"));
        
        // Columnas opcionales de opciones adicionales
        if (colFragil != null) {
            colFragil.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                    c.getValue().isFragil() ? "SÍ" : "NO"));
        }
        if (colFirmaRequerida != null) {
            colFirmaRequerida.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                    c.getValue().isFirmaRequerida() ? "SÍ" : "NO"));
        }
        
        // Costo
        colCostoEstimado.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCostoEstimado()));
        
        // Fechas
        colFechaCreacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaCreacionStr()));
        colFechaConfirmacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaConfirmacionStr()));
        colFechaEntrega.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaEntregaStr()));
        colFechaEntregaEstimada.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getFechaEntregaEstimada() != null ? c.getValue().getFechaEntregaEstimada().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
        
        // Mostrar nombre del repartidor, no solo ID
        colRepartidor.setCellValueFactory(c -> {
            if (c.getValue().getIdRepartidor() != null) {
                String nombre = facade.obtenerNombreRepartidor(c.getValue().getIdRepartidor());
                return new javafx.beans.property.SimpleStringProperty(nombre);
            }
            return new javafx.beans.property.SimpleStringProperty("Sin asignar");
        });

        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getEstado()));

        tablaEnvios.setEditable(false);
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
        cargarHistorial();
    }

    protected void cargarHistorial() {
        java.util.List<EnvioDTO> lista = (usuario == null)
                ? facade.listarTodosEnvios()
                : facade.listarEnviosPorUsuario(usuario.getId());

        enviosCompletos = new java.util.ArrayList<>(lista);
        tablaEnvios.setItems(FXCollections.observableArrayList(lista));
        tablaEnvios.refresh();
    }
    
    @FXML
    protected void aplicarFiltros(ActionEvent event) {
        // Obtener valores de los filtros
        java.time.LocalDate fechaInicio = fechaInicioFilter != null ? fechaInicioFilter.getValue() : null;
        java.time.LocalDate fechaFin = fechaFinFilter != null ? fechaFinFilter.getValue() : null;
        EnvioDTO.EstadoEnvio estado = null;
        if (estadoFilter != null && estadoFilter.getValue() != null && !estadoFilter.getValue().isEmpty()) {
            try {
                estado = EnvioDTO.EstadoEnvio.valueOf(estadoFilter.getValue());
            } catch (IllegalArgumentException e) {
                // Estado inválido, ignorar
            }
        }
        
        // Usar la fachada para filtrar (delega al servicio)
        Long idUsuario = usuario != null ? usuario.getId() : null;
        java.util.List<EnvioDTO> filtrados = facade.filtrarEnvios(idUsuario, fechaInicio, fechaFin, estado);
        
        tablaEnvios.setItems(FXCollections.observableArrayList(filtrados));
        tablaEnvios.refresh();
    }
    
    @FXML
    protected void limpiarFiltros(ActionEvent event) {
        if (fechaInicioFilter != null) fechaInicioFilter.setValue(null);
        if (fechaFinFilter != null) fechaFinFilter.setValue(null);
        if (estadoFilter != null) estadoFilter.setValue(null);
        cargarHistorial();
    }

    protected void abrirEditorPara(EnvioDTO envioDTO, String fxmlPath) {
        try {
            if (envioDTO == null) {
                mostrarAlerta("Selecciona un envío", "Debes seleccionar un envío para editar.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            EditarEnvioController ctrl = loader.getController();
            ctrl.setUsuarioActual(usuario);
            ctrl.setEnvioToEdit(envioDTO);
            ctrl.setOnEnvioCreado(this::cargarHistorial);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Envío");
            stage.showAndWait();

            cargarHistorial();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el editor de envío.");
        }
    }

    protected String solicitarIncidenciaDialog(String textoActual) {
        TextArea area = new TextArea();
        if (textoActual != null) area.setText(textoActual);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Registrar Incidencia");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(area);
        dialog.setResultConverter(bt -> bt == ButtonType.OK ? area.getText().trim() : null);
        dialog.initModality(Modality.APPLICATION_MODAL);
        return dialog.showAndWait().orElse(null);
    }

    protected void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    @FXML protected abstract void editarEnvio(ActionEvent event);
    @FXML protected abstract void eliminarEnvio(ActionEvent event);
    @FXML protected abstract void verIncidencia(ActionEvent event);
    @FXML protected abstract void handleVolver(ActionEvent event);
}
