package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DireccionesController {

    @FXML private TableView<Direccion> direccionesTable;
    @FXML private TableColumn<Direccion, String> aliasColumn;
    @FXML private TableColumn<Direccion, String> calleColumn;
    @FXML private TableColumn<Direccion, String> ciudadColumn;
    @FXML private Label mensajeLabel;

    private Usuario usuario;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        aliasColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getAlias()));
        calleColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCalle()));
        ciudadColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCiudad()));
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        refrescarTabla();
    }

    @FXML
    private void handleNuevaDireccion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/direccion_form.fxml"));
            Parent root = loader.load();

            DireccionFormController ctrl = loader.getController();
            ctrl.setUsuario(usuario);
            ctrl.setOnGuardado(this::refrescarTabla);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Nueva dirección");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error abriendo formulario", "red");
        }
    }

    @FXML
    private void handleEditarDireccion() {
        Direccion seleccionada = direccionesTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Seleccione una dirección para editar", "red");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/direccion_form.fxml"));
            Parent root = loader.load();

            DireccionFormController ctrl = loader.getController();
            ctrl.setUsuario(usuario);
            ctrl.setDireccionToEdit(seleccionada);
            ctrl.setOnGuardado(this::refrescarTabla);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar dirección");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error abriendo formulario", "red");
        }
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) direccionesTable.getScene().getWindow();
        stage.close();
    }

    private void refrescarTabla() {
        if (usuario != null) {
            direccionesTable.getItems().setAll(usuario.getDirecciones());
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
