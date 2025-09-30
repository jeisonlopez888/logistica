package co.edu.uniquindio.logistica.controller;


import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.store.DataStore;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class HistorialEnviosController {

    @FXML
    private TableView<Envio> tablaEnvios;

    @FXML
    private TableColumn<Envio, Long> colId;

    @FXML
    private TableColumn<Envio, String> colOrigen;

    @FXML
    private TableColumn<Envio, String> colDestino;

    @FXML
    private TableColumn<Envio, Double> colPeso;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarHistorial();
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrigen.setCellValueFactory(cellData ->
                javafx.beans.property.SimpleStringProperty.stringExpression(
                        javafx.beans.binding.Bindings.createStringBinding(
                                () -> cellData.getValue().getOrigen().getCalle()
                        )
                )
        );
        colDestino.setCellValueFactory(cellData ->
                javafx.beans.property.SimpleStringProperty.stringExpression(
                        javafx.beans.binding.Bindings.createStringBinding(
                                () -> cellData.getValue().getDestino().getCalle()
                        )
                )
        );
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
    }

    private void cargarHistorial() {
        List<Envio> enviosUsuario = DataStore.getInstance().getEnvios().values().stream()
                .filter(e -> e.getUsuario().equals(usuario))
                .collect(Collectors.toList());

        tablaEnvios.setItems(FXCollections.observableArrayList(enviosUsuario));
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) tablaEnvios.getScene().getWindow();
        stage.close();
    }
}

