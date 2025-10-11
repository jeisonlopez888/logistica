package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Tarifa;
import co.edu.uniquindio.logistica.store.DataStore;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class TarifasController {

    @FXML private TableView<Tarifa> tarifasTable;
    @FXML private TableColumn<Tarifa, Long> idCol;
    @FXML private TableColumn<Tarifa, String> descripcionCol;
    @FXML private TableColumn<Tarifa, Double> baseCol;
    @FXML private TableColumn<Tarifa, Double> porKiloCol;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        descripcionCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescripcion()));
        baseCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCostoBase()));
        porKiloCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCostoPorKilo()));

        cargarTarifas();
    }

    private void cargarTarifas() {
        tarifasTable.setItems(FXCollections.observableArrayList(facade.getTarifas()));
    }

    @FXML
    private void handleAgregar() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Agregar nueva tarifa");
        dialog.setContentText("Descripción:");
        dialog.showAndWait().ifPresent(desc -> {
            double base = pedirNumero("Costo base:");
            double porKilo = pedirNumero("Costo por kilo:");

            Tarifa nueva = new Tarifa(DataStore.getInstance().nextId()
                    , desc, base, porKilo);
            facade.addTarifa(nueva);
            cargarTarifas();
            mensajeLabel.setText("✅ Tarifa agregada correctamente");
        });
    }

    private double pedirNumero(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(prompt);
        dialog.setContentText("Ingrese valor:");
        return dialog.showAndWait().map(Double::parseDouble).orElse(0.0);
    }

    @FXML
    private void handleEliminar() {
        Tarifa seleccionada = tarifasTable.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            facade.eliminarTarifa(seleccionada);
            cargarTarifas();
            mensajeLabel.setText("🗑️ Tarifa eliminada");
        } else {
            mensajeLabel.setText("❌ Seleccione una tarifa");
        }
    }

    @FXML
    private void handleVolverAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
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
}

