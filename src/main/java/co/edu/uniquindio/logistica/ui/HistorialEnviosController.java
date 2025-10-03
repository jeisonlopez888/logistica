package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class HistorialEnviosController {

    @FXML private TableView<Envio> tablaEnvios;
    @FXML private TableColumn<Envio, Long> colId;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, Double> colPeso;

    private Usuario usuario;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrigen.setCellValueFactory(new PropertyValueFactory<>("origenDireccion"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destinoDireccion"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<Envio> enviosUsuario = DataStore.getInstance().getEnvios().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().equals(usuario))
                .collect(Collectors.toList());
        tablaEnvios.setItems(FXCollections.observableArrayList(enviosUsuario));
    }

    @FXML
    private void editarEnvio() {
        Envio seleccionado = tablaEnvios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alert("Selecciona un envío");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio.fxml"));
            Parent root = loader.load();  // 👈 aquí el cambio importante

            CrearEnvioController ctrl = loader.getController();
            ctrl.setUsuario(usuario);
            ctrl.setEnvioToEdit(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Editar Envío");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void eliminarEnvio() {
        Envio seleccionado = tablaEnvios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alert("Selecciona un envío");
            return;
        }
        DataStore.getInstance().getEnvios().remove(seleccionado);
        cargarHistorial();
        alert("Envío eliminado: ID " + seleccionado.getId());
    }

    @FXML
    private void volverMenu() {
        Stage stage = (Stage) tablaEnvios.getScene().getWindow();
        stage.close();
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
