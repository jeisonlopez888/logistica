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
        
        costoLabel.setText(String.format("$ %.2f", envioDTO.getCostoEstimado()));
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

            // Buscar el pago recién creado para confirmarlo
            PagoDTO pagoDTO = facade.buscarPagoPorEnvio(envioEditadoDTO.getId());
            if (pagoDTO != null) {
                // Confirmar pago (esto asignará automáticamente un repartidor si está disponible)
                facade.confirmarPago(pagoDTO.getId());
                
                // Obtener el envío actualizado desde el DataStore para incluir el repartidor asignado
                EnvioDTO envioActualizado = facade.buscarEnvioPorId(envioEditadoDTO.getId());
                if (envioActualizado != null) {
                    // Actualizar el DTO con los datos actualizados (incluyendo repartidor si fue asignado)
                    envioEditadoDTO.setEstado(envioActualizado.getEstado());
                    envioEditadoDTO.setIdRepartidor(envioActualizado.getIdRepartidor());
                    envioEditadoDTO.setFechaConfirmacion(envioActualizado.getFechaConfirmacion());
                    envioEditadoDTO.setFechaEntregaEstimada(envioActualizado.getFechaEntregaEstimada());
                } else {
                    // Fallback si no se puede obtener el envío actualizado
                    envioEditadoDTO.setEstado(EnvioDTO.EstadoEnvio.CONFIRMADO);
                    envioEditadoDTO.setFechaConfirmacion(java.time.LocalDateTime.now());
                }
            } else {
                // Si no hay pago, solo actualizar el estado
                envioEditadoDTO.setEstado(EnvioDTO.EstadoEnvio.CONFIRMADO);
                envioEditadoDTO.setFechaConfirmacion(java.time.LocalDateTime.now());
            }
            
            // Registrar cambios del envío
            facade.registrarEnvio(envioEditadoDTO);

            mostrarMensaje("✅ Pago confirmado. Envío actualizado correctamente.", "green");
            
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
