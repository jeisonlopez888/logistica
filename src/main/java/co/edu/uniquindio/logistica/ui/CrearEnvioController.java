package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.service.TarifaService;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class CrearEnvioController {

    @FXML private TextField origenDireccionField;
    @FXML private ComboBox<String> zonaOrigenCombo;
    @FXML private TextField destinoDireccionField;
    @FXML private ComboBox<String> zonaDestinoCombo;
    @FXML private TextField pesoField;
    @FXML private CheckBox prioridadCheck;
    @FXML private CheckBox seguroCheck;
    @FXML private Label costoLabel;
    @FXML private Label mensajeLabel;
    @FXML private Button crearBtn;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private final TarifaService tarifaService = new TarifaService();

    private Usuario usuarioActual;
    private Runnable onEnvioCreado;

    private double costoEstimadoActual = 0.0;

    // ✅ Método actual
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    // ✅ Método adicional para compatibilidad con controladores antiguos
    public void setUsuario(Usuario usuario) {
        setUsuarioActual(usuario);
    }

    public void setOnEnvioCreado(Runnable onEnvioCreado) {
        this.onEnvioCreado = onEnvioCreado;
    }

    @FXML
    private void initialize() {
        zonaOrigenCombo.getItems().addAll("Sur", "Centro", "Norte");
        zonaDestinoCombo.getItems().addAll("Sur", "Centro", "Norte");

        // ✅ Recuperar automáticamente el usuario logueado desde Sesion
        if (Sesion.getUsuarioActual() != null) {
            this.usuarioActual = Sesion.getUsuarioActual();
        }
    }


    /** 🔹 Paso 1: Calcular tarifa estimada antes de crear */
    @FXML
    private void handleCotizar() {
        try {
            if (usuarioActual == null) {
                mostrarMensaje("⚠️ No se ha asignado el usuario actual", "orange");
                return;
            }

            String dirOrigen = origenDireccionField.getText();
            String zonaOrigen = zonaOrigenCombo.getValue();
            String dirDestino = destinoDireccionField.getText();
            String zonaDestino = zonaDestinoCombo.getValue();
            String pesoStr = pesoField.getText();

            if (dirOrigen.isEmpty() || zonaOrigen == null ||
                    dirDestino.isEmpty() || zonaDestino == null || pesoStr.isEmpty()) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            double peso = Double.parseDouble(pesoStr);

            Direccion origen = new Direccion(DataStore.getInstance().nextId(), "Origen", dirOrigen, "", zonaOrigen);
            Direccion destino = new Direccion(DataStore.getInstance().nextId(), "Destino", dirDestino, "", zonaDestino);

            Envio envioTemp = new Envio();
            envioTemp.setOrigen(origen);
            envioTemp.setDestino(destino);
            envioTemp.setPeso(peso);
            envioTemp.setUsuario(usuarioActual);
            envioTemp.setPrioridad(prioridadCheck.isSelected());
            envioTemp.setSeguro(seguroCheck.isSelected());

            double total = tarifaService.calcularTarifa(envioTemp);
            costoEstimadoActual = total;

            costoLabel.setText(String.format("$ %.2f", total));
            crearBtn.setDisable(false);
            mostrarMensaje("💰 Costo estimado calculado correctamente", "green");

        } catch (NumberFormatException e) {
            mostrarMensaje("⚠️ El peso debe ser numérico", "orange");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al calcular la tarifa", "red");
        }
    }

    /** 🔹 Paso 2: Crear envío, registrar y confirmar pago */
    @FXML
    private void handleCrear() {
        try {
            if (costoEstimadoActual <= 0) {
                mostrarMensaje("⚠️ Primero calcule el costo antes de confirmar", "orange");
                return;
            }

            Alert pagoDialog = new Alert(Alert.AlertType.CONFIRMATION);
            pagoDialog.setTitle("Confirmar Pago");
            pagoDialog.setHeaderText("Total a pagar: $" + costoEstimadoActual);
            pagoDialog.setContentText("¿Desea confirmar el pago y registrar el envío?");
            pagoDialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    procesarEnvioYPago();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al registrar el envío", "red");
        }
    }



    private void procesarEnvioYPago() {
        try {
            String dirOrigen = origenDireccionField.getText();
            String zonaOrigen = zonaOrigenCombo.getValue();
            String dirDestino = destinoDireccionField.getText();
            String zonaDestino = zonaDestinoCombo.getValue();
            double peso = Double.parseDouble(pesoField.getText());

            Direccion origen = new Direccion(DataStore.getInstance().nextId(), "Origen", dirOrigen, "", zonaOrigen);
            Direccion destino = new Direccion(DataStore.getInstance().nextId(), "Destino", dirDestino, "", zonaDestino);

            Envio envio = facade.crearEnvio(usuarioActual, origen, destino, peso);
            envio.setPrioridad(prioridadCheck.isSelected());
            envio.setSeguro(seguroCheck.isSelected());
            envio.setCostoEstimado(costoEstimadoActual);

            Pago pago = facade.registrarPagoSimulado(envio, costoEstimadoActual, MetodoPago.TARJETA_CREDITO);
            facade.confirmarPago(pago.getId());

            mostrarMensaje("✅ Envío registrado y pago confirmado.\nRepartidor asignado si hay disponible.", "green");
            limpiarCampos();

            if (onEnvioCreado != null) onEnvioCreado.run();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al procesar envío y pago", "red");
        }
    }

    @FXML
    private void handleCancelar() {
        limpiarCampos();
        mostrarMensaje("ℹ️ Creación cancelada", "#1976D2");
    }

    private void limpiarCampos() {
        origenDireccionField.clear();
        destinoDireccionField.clear();
        pesoField.clear();
        zonaOrigenCombo.getSelectionModel().clearSelection();
        zonaDestinoCombo.getSelectionModel().clearSelection();
        prioridadCheck.setSelected(false);
        seguroCheck.setSelected(false);
        costoLabel.setText("—");
        crearBtn.setDisable(true);
    }

    /**
     * Método de compatibilidad para controladores que intentan editar un envío existente.
     * Actualmente no se usa en esta versión, pero se deja para evitar errores de compilación.
     */
    public void setEnvioToEdit(Envio envio) {
        // Opcionalmente podrías cargar datos en el formulario si decides permitir edición.
        if (envio != null) {
            origenDireccionField.setText(envio.getOrigenDireccion());
            destinoDireccionField.setText(envio.getDestinoDireccion());
            pesoField.setText(String.valueOf(envio.getPeso()));

            if (envio.getOrigen() != null) zonaOrigenCombo.setValue(envio.getOrigen().getCoordenadas());
            if (envio.getDestino() != null) zonaDestinoCombo.setValue(envio.getDestino().getCoordenadas());

            prioridadCheck.setSelected(envio.isPrioridad());
            seguroCheck.setSelected(envio.isSeguro());
            costoLabel.setText(String.format("$ %.2f", envio.getCostoEstimado()));
        }
    }

    @FXML
    private void handleGuardarEnvio() {
        try {
            String dirOrigen = origenDireccionField.getText();
            String zonaOrigen = zonaOrigenCombo.getValue();
            String dirDestino = destinoDireccionField.getText();
            String zonaDestino = zonaDestinoCombo.getValue();
            double peso = Double.parseDouble(pesoField.getText());

            Direccion origen = new Direccion(DataStore.getInstance().nextId(), "Origen", dirOrigen, "", zonaOrigen);
            Direccion destino = new Direccion(DataStore.getInstance().nextId(), "Destino", dirDestino, "", zonaDestino);

            Envio envio = new Envio();
            // No asignar ID manualmente, DataStore lo hará automáticamente
            envio.setOrigen(origen);
            envio.setDestino(destino);
            envio.setPeso(peso);
            envio.setUsuario(usuarioActual);
            envio.setPrioridad(prioridadCheck.isSelected());
            envio.setSeguro(seguroCheck.isSelected());
            envio.setEstado(Envio.EstadoEnvio.PENDIENTE);

            // Registrar envío, DataStore asigna ID automáticamente
            facade.registrarEnvio(envio);

            mostrarMensaje("✅ Envío guardado correctamente con estado PENDIENTE", "green");
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ Peso inválido, ingresa un número válido", "red");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error desconocido al guardar envío", "red");
        }
    }



    @FXML
    private void handleVerDesglose() {
        try {
            String dirOrigen = origenDireccionField.getText();
            String zonaOrigen = zonaOrigenCombo.getValue();
            String dirDestino = destinoDireccionField.getText();
            String zonaDestino = zonaDestinoCombo.getValue();
            String pesoStr = pesoField.getText();

            if (dirOrigen.isEmpty() || zonaOrigen == null ||
                    dirDestino.isEmpty() || zonaDestino == null || pesoStr.isEmpty()) {
                mostrarMensaje("⚠️ Completa los datos del envío antes de ver el desglose.", "orange");
                return;
            }

            double peso = Double.parseDouble(pesoStr);

            Direccion origen = new Direccion(DataStore.getInstance().nextId(), "Origen", dirOrigen, "", zonaOrigen);
            Direccion destino = new Direccion(DataStore.getInstance().nextId(), "Destino", dirDestino, "", zonaDestino);

            Envio envioTemp = new Envio();
            envioTemp.setOrigen(origen);
            envioTemp.setDestino(destino);
            envioTemp.setPeso(peso);
            envioTemp.setUsuario(usuarioActual);
            envioTemp.setPrioridad(prioridadCheck.isSelected());
            envioTemp.setSeguro(seguroCheck.isSelected());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalle_tarifa.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Detalle de Tarifa");
            stage.setScene(new Scene(root));

            DetalleTarifaController controller = loader.getController();
            controller.setStage(stage);
            controller.mostrarDetalle(envioTemp);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir el detalle de tarifa.", "red");
        } catch (NumberFormatException e) {
            mostrarMensaje("⚠️ El peso debe ser numérico para calcular el desglose.", "orange");
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void handleVolverMenu(ActionEvent event) {
        try {
            Sesion.cerrarSesion();

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/user.fxml"));
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
