package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.PagoDTO;
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
import javafx.stage.FileChooser;
import java.io.File;

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

        // Cargar direcciones del usuario en los ComboBoxes (si ya se estableció el usuario)
        if (usuarioActual != null) {
            cargarDireccionesUsuario();
        }

        // Cargar datos del envío seleccionado
        if (envioDTO.getOrigen() != null) {
            origenDireccionField.setText(envioDTO.getOrigen().getCalle());
            zonaOrigenCombo.setValue(envioDTO.getOrigen().getCiudad());
            
            // Intentar seleccionar la dirección en el ComboBox si coincide con alguna del usuario
            // El formato ahora es: "calle (zona)"
            if (origenDireccionCombo != null && usuarioActual != null && usuarioActual.getDirecciones() != null) {
                String calleOrigen = envioDTO.getOrigen().getCalle();
                String ciudadOrigen = envioDTO.getOrigen().getCiudad();
                for (DireccionDTO dir : usuarioActual.getDirecciones()) {
                    if (dir.getCalle().equals(calleOrigen) && dir.getCiudad().equals(ciudadOrigen)) {
                        String calle = dir.getCalle() != null ? dir.getCalle() : "";
                        String zona = dir.getCiudad() != null ? dir.getCiudad() : "";
                        String textoCombo = calle + " (" + zona + ")";
                        origenDireccionCombo.setValue(textoCombo);
                        break;
                    }
                }
            }
        }
        
        if (envioDTO.getDestino() != null) {
            destinoDireccionField.setText(envioDTO.getDestino().getCalle());
            zonaDestinoCombo.setValue(envioDTO.getDestino().getCiudad());
            
            // Intentar seleccionar la dirección en el ComboBox si coincide con alguna del usuario
            // El formato ahora es: "calle (zona)"
            if (destinoDireccionCombo != null && usuarioActual != null && usuarioActual.getDirecciones() != null) {
                String calleDestino = envioDTO.getDestino().getCalle();
                String ciudadDestino = envioDTO.getDestino().getCiudad();
                for (DireccionDTO dir : usuarioActual.getDirecciones()) {
                    if (dir.getCalle().equals(calleDestino) && dir.getCiudad().equals(ciudadDestino)) {
                        String calle = dir.getCalle() != null ? dir.getCalle() : "";
                        String zona = dir.getCiudad() != null ? dir.getCiudad() : "";
                        String textoCombo = calle + " (" + zona + ")";
                        destinoDireccionCombo.setValue(textoCombo);
                        break;
                    }
                }
            }
        }
        
        pesoField.setText(String.valueOf(envioDTO.getPeso()));
        
        // Las dimensiones no se almacenan en EnvioDTO, se calculan del volumen
        // Si hay volumen, intentar estimar dimensiones (esto es aproximado)
        // En la práctica, las dimensiones se ingresan manualmente al editar
        
        prioridadCheck.setSelected(envioDTO.isPrioridad());
        seguroCheck.setSelected(envioDTO.isSeguro());
        if (fragilCheck != null) fragilCheck.setSelected(envioDTO.isFragil());
        if (firmaRequeridaCheck != null) firmaRequeridaCheck.setSelected(envioDTO.isFirmaRequerida());
        
        // Cargar tarifa
        if (tipoTarifaCombo != null) {
            tipoTarifaCombo.setValue(envioDTO.getTipoTarifa() != null ? envioDTO.getTipoTarifa() : "Normal");
        }
        
        // Actualizar costo si el label existe
        if (costoLabel != null) {
            costoLabel.setText(String.format("$ %.2f", envioDTO.getCostoEstimado()));
        }
        costoEstimadoActual = envioDTO.getCostoEstimado();

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

            // Actualizar direcciones (nombre, calle, ciudad, coordenadas)
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), "");
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), "");

            envioEditadoDTO.setOrigen(origenDTO);
            envioEditadoDTO.setDestino(destinoDTO);
            envioEditadoDTO.setPeso(peso);
            envioEditadoDTO.setVolumen(volumen);
            envioEditadoDTO.setPrioridad(prioridadCheck.isSelected());
            envioEditadoDTO.setSeguro(seguroCheck.isSelected());
            envioEditadoDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioEditadoDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());
            envioEditadoDTO.setTipoTarifa(tipoTarifaCombo != null && tipoTarifaCombo.getValue() != null 
                    ? tipoTarifaCombo.getValue() : "Normal");

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
     * Mismo flujo que CrearEnvioController: pregunta por notificación, método de pago e impresión de factura
     */
    @FXML
    @Override
    protected void handleCrear() {
        try {
            if (envioEditadoDTO == null) {
                mostrarMensaje("⚠️ Selecciona un envío primero.", "orange");
                return;
            }

            if (costoEstimadoActual <= 0) {
                mostrarMensaje("⚠️ Primero calcule el costo antes de confirmar", "orange");
                return;
            }

            // Primero preguntar por el método de notificación
            co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion canal = mostrarDialogoCanalNotificacion();
            if (canal == null) {
                mostrarMensaje("⚠️ Operación cancelada", "orange");
                return;
            }
            
            // Establecer el canal de notificación en el servicio
            facade.setCanalNotificacion(canal);

            Alert pagoDialog = new Alert(Alert.AlertType.CONFIRMATION);
            pagoDialog.setTitle("Confirmar Pago");
            pagoDialog.setHeaderText("Total a pagar: $" + costoEstimadoActual);
            pagoDialog.setContentText("¿Desea confirmar el pago y actualizar el envío?");
            pagoDialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) procesarPagoExistente();
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al confirmar el envío.", "red");
        }
    }

    /**
     * Procesar el pago sin crear un nuevo envío usando DTOs
     * Mismo flujo que CrearEnvioController: pregunta por método de pago e impresión de factura
     */
    private void procesarPagoExistente() {
        try {
            // Actualizar el envío con los valores actuales del formulario antes de procesar el pago
            if (!validarCampos()) return;

            double peso = Double.parseDouble(pesoField.getText());
            double volumen = calcularVolumen();

            // Actualizar direcciones
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), "");
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), "");

            envioEditadoDTO.setOrigen(origenDTO);
            envioEditadoDTO.setDestino(destinoDTO);
            envioEditadoDTO.setPeso(peso);
            envioEditadoDTO.setVolumen(volumen);
            envioEditadoDTO.setPrioridad(prioridadCheck.isSelected());
            envioEditadoDTO.setSeguro(seguroCheck.isSelected());
            envioEditadoDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioEditadoDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());
            envioEditadoDTO.setTipoTarifa(tipoTarifaCombo != null && tipoTarifaCombo.getValue() != null 
                    ? tipoTarifaCombo.getValue() : "Normal");
            envioEditadoDTO.setCostoEstimado(costoEstimadoActual);

            // Registrar cambios del envío
            facade.registrarEnvio(envioEditadoDTO);

            // Mostrar diálogo para elegir método de pago
            MetodoPago metodoSeleccionado = mostrarDialogoMetodoPago("Seleccione el método de pago para el envío");
            if (metodoSeleccionado == null) {
                mostrarMensaje("⚠️ Operación cancelada. El envío fue actualizado pero sin pago.", "orange");
                return;
            }

            // Registrar pago simulado
            facade.registrarPagoSimulado(envioEditadoDTO.getId(), costoEstimadoActual, metodoSeleccionado);

            // Buscar el pago recién creado y confirmarlo (esto asignará automáticamente un repartidor)
            PagoDTO pagoDTO = facade.buscarPagoPorEnvio(envioEditadoDTO.getId());
            if (pagoDTO != null) {
                // Confirmar pago (esto asignará automáticamente un repartidor disponible en la zona de destino)
                String resultado = facade.confirmarPago(pagoDTO.getId());
                mostrarMensaje("✅ Envío #" + envioEditadoDTO.getId() + " actualizado. " + resultado, "green");
                
                // Ofrecer imprimir factura
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Factura de Envío");
                alert.setHeaderText("¿Desea imprimir la factura/guía de envío?");
                alert.setContentText("El envío ha sido confirmado. Puede generar la factura ahora.");
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(bt -> {
                    if (bt == ButtonType.YES) {
                        imprimirFactura(envioEditadoDTO.getId());
                    }
                });
            } else {
                mostrarMensaje("✅ Envío #" + envioEditadoDTO.getId() + " actualizado. Pago registrado pero no confirmado.", "green");
            }

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
