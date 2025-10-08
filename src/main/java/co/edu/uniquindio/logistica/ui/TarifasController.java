package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Tarifa;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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

            Tarifa nueva = new Tarifa(facade.generarId(), desc, base, porKilo);
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
    private void handleCerrar() {
        Stage stage = (Stage) tarifasTable.getScene().getWindow();
        stage.close();
    }
}

