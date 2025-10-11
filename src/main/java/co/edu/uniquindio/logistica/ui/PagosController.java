package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.MetodoPago;
import co.edu.uniquindio.logistica.model.Pago;
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
import javafx.stage.Stage;

/**
 * PagosController actualizado para usar la estructura de Pago que ya existe
 * en tu DataStore (constructor: id, envio, montoPagado, montoCalculado, confirmado).
 *
 * No se modificó nada más del proyecto.
 */
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


        estadoCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().isConfirmado() ? "Completado" : "Pendiente"));

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
                    mostrarMensaje("❌ Envío no encontrado", "red");
                    return;
                }

                // calcular monto estimado (según la lógica centralizada en el facade/service)
                double montoCalculado = facade.calcularTarifa(envio.getPeso());

                // informar al usuario del monto calculado
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Monto del envío");
                info.setHeaderText("Costo calculado del envío:");
                info.setContentText(String.format("El costo estimado es: $%.2f", montoCalculado));
                info.showAndWait();

                // pedir al usuario el monto que va a pagar
                double montoPagado = pedirNumero("Ingrese el monto pagado (puede ser >= o < al calculado):");

                // determinar confirmación automática si pagó >= calculado
                boolean confirmado = Math.round(montoPagado * 100.0) / 100.0 >= Math.round(montoCalculado * 100.0) / 100.0;

                // crear Pago usando DataStore para generar el ID (tal como en tu DataStore y ejemplos)
                Pago pago = new Pago(DataStore.getInstance().nextId(), envio, montoPagado, MetodoPago.TRANSFERENCIA);

                // agregar al store a través del facade (facade delega al store)
                facade.addPago(pago);

                cargarPagos();
                mostrarMensaje(confirmado
                        ? "✅ Pago registrado y completado correctamente"
                        : "⚠️ Pago registrado pero pendiente (incompleto)", confirmado ? "green" : "orange");

            } catch (NumberFormatException e) {
                mostrarMensaje("❌ ID inválido", "red");
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarMensaje("❌ Error al registrar el pago", "red");
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

    @FXML
    private void handleCompletarPago() {
        Pago seleccionado = pagosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("❌ Selecciona un pago para completar", "red");
            return;
        }

        if (seleccionado.isConfirmado()) {
            mostrarMensaje("⚠️ Este pago ya está completado", "orange");
            return;
        }

        // Llamar a facade.confirmarPago()
        String resultado = facade.confirmarPago(seleccionado.getId());

        pagosTable.refresh();
        mostrarMensaje(resultado, resultado.contains("✅") ? "green" : "red");
    }


    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
