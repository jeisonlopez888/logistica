package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialEnviosController {

    @FXML private TableView<Envio> tablaEnvios;
    @FXML private TableColumn<Envio, Long> colId;
    @FXML private TableColumn<Envio, String> colUsuario;
    @FXML private TableColumn<Envio, String> colOrigenDireccion;
    @FXML private TableColumn<Envio, String> colOrigenCiudad;
    @FXML private TableColumn<Envio, String> colDestinoDireccion;
    @FXML private TableColumn<Envio, String> colDestinoCiudad;
    @FXML private TableColumn<Envio, Double> colPeso;
    @FXML private TableColumn<Envio, Envio.EstadoEnvio> colEstado;
    @FXML private TableColumn<Envio, String> colFechaCreacion;
    @FXML private TableColumn<Envio, String> colFechaConfirmacion; // nueva columna
    @FXML private TableColumn<Envio, String> colFechaEntrega;
    @FXML private TableColumn<Envio, String> colRepartidor; // nueva columna

    private Usuario usuario;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colUsuario.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getUsuario() != null ? cellData.getValue().getUsuario().getNombre() : ""
                )
        );

        colOrigenDireccion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getOrigen() != null ? cellData.getValue().getOrigen().getCalle() : ""
                )
        );

        colOrigenCiudad.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getOrigen() != null ? cellData.getValue().getOrigen().getCiudad() : ""
                )
        );

        colDestinoDireccion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDestino() != null ? cellData.getValue().getDestino().getCalle() : ""
                )
        );

        colDestinoCiudad.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDestino() != null ? cellData.getValue().getDestino().getCiudad() : ""
                )
        );

        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        // Columna repartidor (nombre)
        colRepartidor.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getRepartidor() != null ? cellData.getValue().getRepartidor().getNombre() : ""
                )
        );

        // Estado editable
        colEstado.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEstado()));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Envio.EstadoEnvio.values())
        ));
        colEstado.setOnEditCommit(event -> {
            Envio envio = event.getRowValue();
            Envio.EstadoEnvio nuevoEstado = event.getNewValue();

            envio.setEstado(nuevoEstado);

            // Si el envío fue entregado, asignar fecha actual
            if (nuevoEstado == Envio.EstadoEnvio.ENTREGADO) {
                envio.setFechaEntrega(LocalDateTime.now());
            }

            // Actualizar DataStore: reemplazar el envío por id
            DataStore.getInstance().getEnvios().replaceAll(e ->
                    e.getId().equals(envio.getId()) ? envio : e
            );

            tablaEnvios.refresh();
            alert("✅ Estado actualizado a: " + nuevoEstado);
        });

        // Fechas
        colFechaCreacion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaCreacionStr()));
        colFechaConfirmacion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaConfirmacionStr()));
        colFechaEntrega.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaEntregaStr()));

        tablaEnvios.setEditable(true);
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<Envio> enviosUsuario;
        if (usuario == null) {
            // si usuario == null mostramos todos (por ejemplo para admin)
            enviosUsuario = DataStore.getInstance().getEnvios();
        } else {
            enviosUsuario = DataStore.getInstance().getEnvios().stream()
                    .filter(e -> e.getUsuario() != null && e.getUsuario().equals(usuario))
                    .collect(Collectors.toList());
        }

        tablaEnvios.setItems(FXCollections.observableArrayList(enviosUsuario));
        tablaEnvios.refresh();
    }

    @FXML
    private void editarEnvio() {
        Envio seleccionado = tablaEnvios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alert("Selecciona un envío para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio.fxml"));
            Parent root = loader.load();

            CrearEnvioController ctrl = loader.getController();
            // compatibilidad ambos setters
            ctrl.setUsuario(seleccionado.getUsuario());
            ctrl.setEnvioToEdit(seleccionado);
            ctrl.setOnEnvioCreado(() -> {
                cargarHistorial();
                tablaEnvios.refresh();
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Envío");
            stage.showAndWait();

            cargarHistorial();
        } catch (Exception e) {
            e.printStackTrace();
            alert("Error al cargar la ventana de edición.");
        }
    }

    @FXML
    private void eliminarEnvio() {
        Envio seleccionado = tablaEnvios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alert("Selecciona un envío para eliminar.");
            return;
        }

        DataStore.getInstance().getEnvios().remove(seleccionado);
        cargarHistorial();
        tablaEnvios.refresh();
        alert("✅ Envío eliminado (ID " + seleccionado.getId() + ")");
    }

    @FXML
    private void handleVolverMenu(ActionEvent event) {
        try {
            Sesion.cerrarSesion();

            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/user.fxml"));
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

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
