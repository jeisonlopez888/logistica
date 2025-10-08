package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Pago;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PagosController {

    @FXML private TableView<Pago> pagosTable;
    @FXML private TableColumn<Pago, Long> idCol;
    @FXML private TableColumn<Pago, Long> envioCol;
    @FXML private TableColumn<Pago, Double> montoPagadoCol;
    @FXML private TableColumn<Pago, Double> montoCalculadoCol;
    @FXML private TableColumn<Pago, String> estadoCol;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        envioCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(
                cell.getValue().getEnvio() != null ? cell.getValue().getEnvio().getId() : null
        ));
        montoPagadoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getMontoPagado()));
        montoCalculadoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getMontoCalculado()));
        estadoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().isCompletado() ? "Completado" : "Pendiente"
        ));

        cargarPagos();
    }

    private void cargarPagos() {
        pagosTable.setItems(FXCollections.observableArrayList(facade.getPagos()));
    }

    @FXML
    private void handleRegistrarPago() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Registrar nuevo pago");
        dialog.setContentText("Ingrese ID del envío:");
        dialog.showAndWait().ifPresent(envioIdStr -> {
            try {
                Long envioId = Long.parseLong(envioIdStr);
                Envio envio = facade.buscarEnvioPorId(envioId);
                if (envio == null) {
                    mensajeLabel.setText("❌ Envío no encontrado");
                    mensajeLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                // 🔹 Calculamos primero el costo real
                double montoCalculado = facade.calcularTarifa(envio.getPeso());

                // 🔹 Mostramos el valor al usuario antes de pedir el pago
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Monto del envío");
                info.setHeaderText("Costo calculado del envío:");
                info.setContentText(String.format("El costo total es: $%.2f", montoCalculado));
                info.showAndWait();

                // 🔹 Ahora pedimos el monto pagado
                double montoPagado = pedirNumero("Ingrese el monto pagado (debe ser >= " + montoCalculado + "):");

                // 🔹 Redondeamos para comparar correctamente
                double pagadoRedondeado = Math.round(montoPagado * 100.0) / 100.0;
                double calculadoRedondeado = Math.round(montoCalculado * 100.0) / 100.0;

                boolean completado = pagadoRedondeado >= calculadoRedondeado;

                Pago pago = new Pago(facade.generarId(), envio, montoPagado, montoCalculado, completado);
                facade.addPago(pago);

                cargarPagos();
                mensajeLabel.setText(completado
                        ? "✅ Pago completado correctamente"
                        : "⚠️ Pago registrado pero pendiente (incompleto)");
                mensajeLabel.setStyle("-fx-text-fill: " + (completado ? "green" : "orange") + ";");

            } catch (NumberFormatException e) {
                mensajeLabel.setText("❌ ID inválido");
                mensajeLabel.setStyle("-fx-text-fill: red;");
            }
        });
    }


    private double pedirNumero(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(prompt);
        dialog.setContentText("Ingrese valor:");
        return dialog.showAndWait().map(Double::parseDouble).orElse(0.0);
    }

    @FXML
    private void handleEliminarPago() {
        Pago seleccionado = pagosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            facade.eliminarPago(seleccionado);
            cargarPagos();
            mostrarMensaje("🗑️ Pago eliminado correctamente", "green");
        } else {
            mostrarMensaje("❌ Selecciona un pago para eliminar", "red");
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void handleCompletarPago() {
        Pago seleccionado = pagosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("❌ Selecciona un pago para completar");
            mensajeLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (seleccionado.isCompletado()) {
            mensajeLabel.setText("⚠️ Este pago ya está completado");
            mensajeLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        seleccionado.setCompletado(true);
        pagosTable.refresh();
        mensajeLabel.setText("✅ Pago marcado como completado");
        mensajeLabel.setStyle("-fx-text-fill: green;");
    }


    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) pagosTable.getScene().getWindow();
        stage.close();
    }
}
