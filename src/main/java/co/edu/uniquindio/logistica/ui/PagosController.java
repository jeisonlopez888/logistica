package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.PagoDTO;
import co.edu.uniquindio.logistica.model.MetodoPago;
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
 * Controlador de Pagos - Solo valida datos y se comunica con Facade usando DTOs
 */
public class PagosController {

    @FXML private TableView<PagoDTO> pagosTable;
    @FXML private TableColumn<PagoDTO, Long> idCol;
    @FXML private TableColumn<PagoDTO, Long> envioCol;
    @FXML private TableColumn<PagoDTO, Double> montoPagadoCol;
    @FXML private TableColumn<PagoDTO, String> metodoCol;
    @FXML private TableColumn<PagoDTO, String> fechaCol;
    @FXML private TableColumn<PagoDTO, String> estadoCol;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        envioCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getIdEnvio()));
        montoPagadoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getMontoPagado()));
        metodoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getMetodo() != null ? cell.getValue().getMetodo().name() : ""));
        fechaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFecha()));
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
                if (ValidacionUtil.isEmpty(envioIdStr)) {
                    mostrarMensaje("❌ ID de envío requerido", "red");
                    return;
                }

                Long envioId = Long.parseLong(envioIdStr);
                
                if (!ValidacionUtil.esIdValido(envioId)) {
                    mostrarMensaje("❌ ID inválido", "red");
                    return;
                }

                // Comunicación con Facade (usa DTOs)
                EnvioDTO envioDTO = facade.buscarEnvioPorId(envioId);
                if (envioDTO == null) {
                    mostrarMensaje("❌ Envío no encontrado", "red");
                    return;
                }

                double montoCalculado = facade.calcularTarifa(envioDTO);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Monto del envío");
                info.setHeaderText("Costo calculado del envío:");
                info.setContentText(String.format("El costo estimado es: $%.2f", montoCalculado));
                info.showAndWait();

                double montoPagado = pedirNumero("Ingrese el monto pagado:");
                
                if (!ValidacionUtil.esMontoValido(montoPagado)) {
                    mostrarMensaje("❌ Monto debe ser mayor a 0", "red");
                    return;
                }

                // Mostrar diálogo para elegir método de pago
                MetodoPago metodoSeleccionado = mostrarDialogoMetodoPago("Seleccione el método de pago");
                if (metodoSeleccionado == null) {
                    return; // Usuario canceló
                }

                // Registrar pago usando Facade (con DTOs)
                PagoDTO pagoDTO = facade.registrarPagoSimulado(envioId, montoPagado, metodoSeleccionado);

                if (pagoDTO != null) {
                    cargarPagos();
                    boolean confirmado = pagoDTO.isConfirmado();
                    mostrarMensaje(confirmado
                            ? "✅ Pago registrado y completado correctamente"
                            : "⚠️ Pago registrado pero pendiente (incompleto)", confirmado ? "green" : "orange");
                } else {
                    mostrarMensaje("❌ Error al registrar el pago", "red");
                }

            } catch (NumberFormatException e) {
                mostrarMensaje("❌ ID debe ser un número válido", "red");
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
        PagoDTO seleccionado = pagosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("❌ Selecciona un pago para eliminar", "red");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que deseas eliminar el pago #" + seleccionado.getId() + "?");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                facade.eliminarPago(seleccionado.getId());
                cargarPagos();
                mostrarMensaje("🗑️ Pago eliminado correctamente", "green");
            }
        });
    }

    @FXML
    private void handleCompletarPago() {
        PagoDTO seleccionado = pagosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("❌ Selecciona un pago para completar", "red");
            return;
        }

        if (seleccionado.isConfirmado()) {
            mostrarMensaje("⚠️ Este pago ya está completado", "orange");
            return;
        }

        // Mostrar diálogo para elegir método de pago
        MetodoPago metodoSeleccionado = mostrarDialogoMetodoPago("Seleccione el método de pago para confirmar");
        if (metodoSeleccionado == null) {
            return; // Usuario canceló
        }

        // Actualizar método de pago si es diferente
        if (seleccionado.getMetodo() != metodoSeleccionado) {
            facade.actualizarMetodoPago(seleccionado.getId(), metodoSeleccionado);
        }

        String resultado = facade.confirmarPago(seleccionado.getId());
        cargarPagos();
        mostrarMensaje(resultado, resultado.contains("✅") ? "green" : "red");
    }

    /**
     * Muestra un diálogo para seleccionar el método de pago
     * @param mensaje Mensaje a mostrar en el diálogo
     * @return MetodoPago seleccionado o null si se canceló
     */
    private MetodoPago mostrarDialogoMetodoPago(String mensaje) {
        Alert metodoPagoDialog = new Alert(Alert.AlertType.CONFIRMATION);
        metodoPagoDialog.setTitle("Método de Pago");
        metodoPagoDialog.setHeaderText(mensaje);
        metodoPagoDialog.getButtonTypes().setAll(
                new ButtonType("Tarjeta Crédito"),
                new ButtonType("Efectivo"),
                new ButtonType("PSE"),
                new ButtonType("Transferencia"),
                ButtonType.CANCEL
        );

        final MetodoPago[] metodoSeleccionado = {null};
        metodoPagoDialog.showAndWait().ifPresent(opcion -> {
            if (opcion == ButtonType.CANCEL) {
                metodoSeleccionado[0] = null;
                return;
            }

            metodoSeleccionado[0] = switch (opcion.getText()) {
                case "Efectivo" -> MetodoPago.EFECTIVO;
                case "PSE" -> MetodoPago.PSE;
                case "Transferencia" -> MetodoPago.TRANSFERENCIA;
                default -> MetodoPago.TARJETA_CREDITO;
            };
        });

        return metodoSeleccionado[0];
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
