package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.TarifaDTO;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador de Tarifas - Solo valida datos y se comunica con Facade usando DTOs
 */
public class TarifasController {

    @FXML private TableView<TarifaDTO> tarifasTable;
    @FXML private TableColumn<TarifaDTO, Long> idCol;
    @FXML private TableColumn<TarifaDTO, String> descripcionCol;
    @FXML private TableColumn<TarifaDTO, Double> baseCol;
    @FXML private TableColumn<TarifaDTO, Double> porKiloCol;
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
            // Validación
            if (ValidacionUtil.isEmpty(desc)) {
                mostrarMensaje("❌ La descripción es requerida", "red");
                return;
            }

            double base = pedirNumero("Costo base:");
            if (base <= 0) {
                mostrarMensaje("❌ El costo base debe ser mayor a 0", "red");
                return;
            }

            double porKilo = pedirNumero("Costo por kilo:");
            if (porKilo <= 0) {
                mostrarMensaje("❌ El costo por kilo debe ser mayor a 0", "red");
                return;
            }

            double porVolumen = pedirNumero("Costo por volumen (m³):");
            if (porVolumen < 0) {
                porVolumen = 0;
            }

            double recargoSeguro = pedirNumero("Recargo por seguro (0-1 para porcentaje, >1 para valor fijo):");
            if (recargoSeguro < 0) {
                recargoSeguro = 0;
            }

            // Crear DTO y registrar
            TarifaDTO nuevaDTO = new TarifaDTO(DataStore.getInstance().nextId(), desc, base, porKilo, porVolumen, recargoSeguro);
            facade.addTarifa(nuevaDTO);
            cargarTarifas();
            mostrarMensaje("✅ Tarifa agregada correctamente. Las ventanas de envío se actualizarán automáticamente.", "green");
        });
    }

    @FXML
    private void handleEditar() {
        TarifaDTO seleccionada = tarifasTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("❌ Seleccione una tarifa para editar", "red");
            return;
        }

        // Editar descripción
        TextInputDialog dialogDesc = new TextInputDialog(seleccionada.getDescripcion());
        dialogDesc.setHeaderText("Editar descripción de tarifa");
        dialogDesc.setContentText("Descripción:");
        dialogDesc.showAndWait().ifPresent(desc -> {
            if (ValidacionUtil.isEmpty(desc)) {
                mostrarMensaje("❌ La descripción es requerida", "red");
                return;
            }

            // Editar costo base
            double base = pedirNumeroConValor("Costo base:", seleccionada.getCostoBase());
            if (base <= 0) {
                mostrarMensaje("❌ El costo base debe ser mayor a 0", "red");
                return;
            }

            // Editar costo por kilo
            double porKilo = pedirNumeroConValor("Costo por kilo:", seleccionada.getCostoPorKilo());
            if (porKilo <= 0) {
                mostrarMensaje("❌ El costo por kilo debe ser mayor a 0", "red");
                return;
            }

            // Editar costo por volumen
            double porVolumen = pedirNumeroConValor("Costo por volumen (m³):", seleccionada.getCostoPorVolumen());
            if (porVolumen < 0) {
                porVolumen = 0;
            }

            // Editar recargo por seguro
            double recargoSeguro = pedirNumeroConValor("Recargo por seguro (0-1 para porcentaje, >1 para valor fijo):", seleccionada.getRecargoSeguro());
            if (recargoSeguro < 0) {
                recargoSeguro = 0;
            }

            // Actualizar DTO
            seleccionada.setDescripcion(desc);
            seleccionada.setCostoBase(base);
            seleccionada.setCostoPorKilo(porKilo);
            seleccionada.setCostoPorVolumen(porVolumen);
            seleccionada.setRecargoSeguro(recargoSeguro);

            // Actualizar en el store
            facade.actualizarTarifa(seleccionada);
            cargarTarifas();
            mostrarMensaje("✅ Tarifa actualizada correctamente. Los cambios se reflejarán en las nuevas cotizaciones.", "green");
        });
    }

    private double pedirNumeroConValor(String prompt, double valorActual) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(valorActual));
        dialog.setHeaderText(prompt);
        dialog.setContentText("Ingrese valor:");
        return dialog.showAndWait()
                .map(s -> {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException e) {
                        return valorActual;
                    }
                })
                .orElse(valorActual);
    }

    private double pedirNumero(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(prompt);
        dialog.setContentText("Ingrese valor:");
        return dialog.showAndWait().map(Double::parseDouble).orElse(0.0);
    }

    @FXML
    private void handleEliminar() {
        TarifaDTO seleccionada = tarifasTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("❌ Seleccione una tarifa", "red");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que deseas eliminar la tarifa '" + seleccionada.getDescripcion() + "'?");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                facade.eliminarTarifa(seleccionada.getId());
                cargarTarifas();
                mostrarMensaje("🗑️ Tarifa eliminada", "green");
            }
        });
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
