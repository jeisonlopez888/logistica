package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Repartidor;
import co.edu.uniquindio.logistica.util.Sesion;
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

public class RepartidoresController {

    @FXML private TableView<Repartidor> tablaRepartidores;
    @FXML private TableColumn<Repartidor, Long> idCol;
    @FXML private TableColumn<Repartidor, String> nombreCol;
    @FXML private TableColumn<Repartidor, String> telefonoCol;
    @FXML private TableColumn<Repartidor, String> zonaCol;
    @FXML private TableColumn<Repartidor, Boolean> disponibleCol;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
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
        abrirFormulario(null); // null = nuevo repartidor
    }

    @FXML
    private void handleEditar() {
        Repartidor seleccionado = tablaRepartidores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un repartidor para editar", "orange");
            return;
        }
        abrirFormulario(seleccionado);
    }

    private void abrirFormulario(Repartidor repartidor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_repartidor.fxml"));
            Parent root = loader.load();

            CrearRepartidorController ctrl = loader.getController();
            ctrl.setOnRepartidorCreado(this::cargarRepartidores);

            if (repartidor != null) {
                ctrl.setRepartidor(repartidor); // cargar datos existentes
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(repartidor == null ? "Registrar Repartidor" : "Editar Repartidor");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir formulario de repartidor", "red");
        }
    }

    @FXML
    private void handleEliminar() {
        Repartidor seleccionado = tablaRepartidores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un repartidor para eliminar", "orange");
            return;
        }

        facade.eliminarRepartidor(seleccionado);
        cargarRepartidores();
        mostrarMensaje("✅ Repartidor eliminado correctamente", "green");
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al login.");
        }
    }


    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill:" + color + ";");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
