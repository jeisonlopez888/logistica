package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.RepartidorDTO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controlador de Repartidores - Solo valida datos y se comunica con Facade usando DTOs
 */
public class RepartidoresController {

    @FXML private TableView<RepartidorDTO> tablaRepartidores;
    @FXML private TableColumn<RepartidorDTO, Long> idCol;
    @FXML private TableColumn<RepartidorDTO, String> nombreCol;
    @FXML private TableColumn<RepartidorDTO, String> documentoCol;
    @FXML private TableColumn<RepartidorDTO, String> telefonoCol;
    @FXML private TableColumn<RepartidorDTO, String> zonaCol;
    @FXML private TableColumn<RepartidorDTO, Boolean> disponibleCol;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        documentoCol.setCellValueFactory(new PropertyValueFactory<>("documento"));
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        zonaCol.setCellValueFactory(new PropertyValueFactory<>("zona"));
        disponibleCol.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        cargarRepartidores();
    }

    @FXML
    public void cargarRepartidores() {
        tablaRepartidores.setItems(FXCollections.observableArrayList(facade.listarRepartidores()));
        tablaRepartidores.refresh();
    }

    @FXML
    private void handleAgregar() {
        abrirFormulario(null);
    }

    @FXML
    private void handleEditar() {
        RepartidorDTO seleccionado = tablaRepartidores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un repartidor para editar", "orange");
            return;
        }
        abrirFormulario(seleccionado);
    }

    private void abrirFormulario(RepartidorDTO repartidorDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_repartidor.fxml"));
            Parent root = loader.load();

            CrearRepartidorController ctrl = loader.getController();
            ctrl.setOnRepartidorCreado(this::cargarRepartidores);

            if (repartidorDTO != null) {
                ctrl.setRepartidor(repartidorDTO);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(repartidorDTO == null ? "Registrar Repartidor" : "Editar Repartidor");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir formulario de repartidor", "red");
        }
    }

    @FXML
    private void handleEliminar() {
        RepartidorDTO seleccionado = tablaRepartidores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un repartidor para eliminar", "orange");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que deseas eliminar al repartidor '" + seleccionado.getNombre() + "'?");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                facade.eliminarRepartidor(seleccionado.getId());
                cargarRepartidores();
                mostrarMensaje("✅ Repartidor eliminado correctamente", "green");
            }
        });
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al panel admin.");
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
