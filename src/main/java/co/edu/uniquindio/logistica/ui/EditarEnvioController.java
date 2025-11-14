package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.MetodoPago;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Controlador para editar un envío existente - Solo valida y usa DTOs
 */
public class EditarEnvioController extends CrearEnvioController {

    private EnvioDTO envioEditadoDTO;
    private String ventanaOrigen;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setVentanaOrigen(String origen) {
        this.ventanaOrigen = origen;
    }

    @Override
    public void setEnvioToEdit(EnvioDTO envioDTO) {
        if (envioDTO == null) return;
        this.envioEditadoDTO = envioDTO;

        // Cargar datos del envío seleccionado
        if (envioDTO.getOrigen() != null) {
            origenDireccionField.setText(envioDTO.getOrigen().getCalle());
            zonaOrigenCombo.setValue(envioDTO.getOrigen().getCiudad());
        }
        if (envioDTO.getDestino() != null) {
            destinoDireccionField.setText(envioDTO.getDestino().getCalle());
            zonaDestinoCombo.setValue(envioDTO.getDestino().getCiudad());
        }
        pesoField.setText(String.valueOf(envioDTO.getPeso()));
        prioridadCheck.setSelected(envioDTO.isPrioridad());
        seguroCheck.setSelected(envioDTO.isSeguro());
        if (fragilCheck != null) fragilCheck.setSelected(envioDTO.isFragil());
        if (firmaRequeridaCheck != null) firmaRequeridaCheck.setSelected(envioDTO.isFirmaRequerida());
        costoLabel.setText(String.format("$ %.2f", envioDTO.getCostoEstimado()));

        mostrarMensaje("✏️ Editando envío ID: " + envioDTO.getId(), "blue");
    }

    /**
     * Guarda los cambios realizados al envío usando DTOs
     */
    @FXML
    @Override
    protected void handleGuardarEnvio() {
        try {
            if (envioEditadoDTO == null) {
                mostrarMensaje("⚠️ Selecciona un envío primero.", "orange");
                return;
            }

            if (!validarCampos()) return;

            // Actualizar DTO con los nuevos valores
            double peso = Double.parseDouble(pesoField.getText());
            if (!ValidacionUtil.esPesoValido(peso)) {
                mostrarMensaje("⚠️ El peso debe ser mayor a 0 y menor a 1000 kg", "orange");
                return;
            }

            double volumen = calcularVolumen();

            // Actualizar direcciones
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), zonaOrigenCombo.getValue());
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), zonaDestinoCombo.getValue());

            envioEditadoDTO.setOrigen(origenDTO);
            envioEditadoDTO.setDestino(destinoDTO);
            envioEditadoDTO.setPeso(peso);
            envioEditadoDTO.setVolumen(volumen);
            envioEditadoDTO.setPrioridad(prioridadCheck.isSelected());
            envioEditadoDTO.setSeguro(seguroCheck.isSelected());
            envioEditadoDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioEditadoDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());

            // Recalcular costo
            double nuevoCosto = facade.calcularTarifa(envioEditadoDTO);
            envioEditadoDTO.setCostoEstimado(nuevoCosto);

            // Registrar cambios usando Facade (trabaja con DTOs)
            facade.registrarEnvio(envioEditadoDTO);

            mostrarMensaje("✅ Envío actualizado correctamente.", "green");

            if (onEnvioCreado != null) onEnvioCreado.run();

            // Cerrar ventana
            Stage stage = (Stage) (crearBtn != null ? crearBtn.getScene().getWindow() : null);
            if (stage != null) stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al actualizar el envío.", "red");
        }
    }

    /**
     * Confirma el envío existente y realiza el pago usando DTOs
     */
    @FXML
    @Override
    protected void handleCrear() {
        try {
            if (envioEditadoDTO == null) {
                mostrarMensaje("⚠️ Selecciona un envío primero.", "orange");
                return;
            }

            // Mostrar diálogo para elegir método de pago
            Alert metodoPagoDialog = new Alert(Alert.AlertType.CONFIRMATION);
            metodoPagoDialog.setTitle("Método de Pago");
            metodoPagoDialog.setHeaderText("Seleccione un método de pago para confirmar el envío");
            metodoPagoDialog.getButtonTypes().setAll(
                    new ButtonType("Tarjeta Crédito"),
                    new ButtonType("Efectivo"),
                    new ButtonType("PSE"),
                    new ButtonType("Transferencia"),
                    ButtonType.CANCEL
            );

            metodoPagoDialog.showAndWait().ifPresent(opcion -> {
                if (opcion == ButtonType.CANCEL) return;

                MetodoPago metodo = switch (opcion.getText()) {
                    case "Efectivo" -> MetodoPago.EFECTIVO;
                    case "PSE" -> MetodoPago.PSE;
                    case "Transferencia" -> MetodoPago.TRANSFERENCIA;
                    default -> MetodoPago.TARJETA_CREDITO;
                };

                procesarPagoExistente(metodo);
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al confirmar el envío.", "red");
        }
    }

    /**
     * Procesar el pago sin crear un nuevo envío usando DTOs
     */
    private void procesarPagoExistente(MetodoPago metodo) {
        try {
            double monto = envioEditadoDTO.getCostoEstimado();

            // Registrar pago usando Facade (trabaja con DTOs)
            facade.registrarPagoSimulado(envioEditadoDTO.getId(), monto, metodo);

            // Confirmar pago
            facade.confirmarPago(envioEditadoDTO.getId());

            // Actualizar estado del envío
            envioEditadoDTO.setEstado(EnvioDTO.EstadoEnvio.CONFIRMADO);
            facade.registrarEnvio(envioEditadoDTO);

            mostrarMensaje("✅ Pago confirmado. Envío actualizado correctamente.", "green");

            if (onEnvioCreado != null) onEnvioCreado.run();

            Stage stage = (Stage) (crearBtn != null ? crearBtn.getScene().getWindow() : null);
            if (stage != null) stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al procesar el pago.", "red");
        }
    }

    /**
     * Volver a la vista anterior
     */
    @FXML
    protected void handleVolver(ActionEvent event) {
        try {
            String rutaFXML;
            if ("user".equalsIgnoreCase(ventanaOrigen)) {
                rutaFXML = "/fxml/historial_envios_user.fxml";
            } else if ("admin".equalsIgnoreCase(ventanaOrigen)) {
                rutaFXML = "/fxml/historial_envios_admin.fxml";
            } else {
                rutaFXML = "/fxml/User.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al panel anterior.");
        }
    }
}
