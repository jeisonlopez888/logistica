package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.model.MetodoPago;
import co.edu.uniquindio.logistica.util.Sesion;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Controlador base para crear/editar envíos - Solo valida datos y usa DTOs
 */
public abstract class CrearEnvioController {

    @FXML protected TextField origenDireccionField;
    @FXML protected ComboBox<String> zonaOrigenCombo;
    @FXML protected TextField destinoDireccionField;
    @FXML protected ComboBox<String> zonaDestinoCombo;
    @FXML protected TextField pesoField;
    @FXML protected ComboBox<String> tipoPaqueteCombo;
    @FXML protected TextField largoField;
    @FXML protected TextField anchoField;
    @FXML protected TextField altoField;
    @FXML protected CheckBox prioridadCheck;
    @FXML protected CheckBox seguroCheck;
    @FXML protected CheckBox fragilCheck;
    @FXML protected CheckBox firmaRequeridaCheck;
    @FXML protected Label costoLabel;
    @FXML protected Label mensajeLabel;
    @FXML protected Button crearBtn;
    @FXML protected HBox dimensionesBox;

    protected final LogisticaFacade facade = LogisticaFacade.getInstance();

    protected UsuarioDTO usuarioActual;
    protected Runnable onEnvioCreado;
    protected double costoEstimadoActual = 0.0;

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        this.usuarioActual = usuarioDTO;
    }

    public void setUsuario(UsuarioDTO usuarioDTO) {
        setUsuarioActual(usuarioDTO);
    }

    public void setOnEnvioCreado(Runnable onEnvioCreado) {
        this.onEnvioCreado = onEnvioCreado;
    }

    @FXML
    protected void initialize() {
        zonaOrigenCombo.getItems().addAll("Sur", "Centro", "Norte");
        zonaDestinoCombo.getItems().addAll("Sur", "Centro", "Norte");

        tipoPaqueteCombo.getItems().addAll("Sobre", "Paquete", "Caja");
        tipoPaqueteCombo.setOnAction(e -> {
            boolean esCaja = "Caja".equals(tipoPaqueteCombo.getValue());
            largoField.setDisable(!esCaja);
            anchoField.setDisable(!esCaja);
            altoField.setDisable(!esCaja);
        });

        largoField.setDisable(true);
        anchoField.setDisable(true);
        altoField.setDisable(true);

        if (Sesion.getUsuarioActual() != null) {
            this.usuarioActual = Sesion.getUsuarioActual();
        }
    }

    /** Calcular tarifa estimada usando DTOs */
    @FXML
    protected void handleCotizar() {
        try {
            if (usuarioActual == null) usuarioActual = Sesion.getUsuarioActual();

            if (!validarCampos()) return;

            // Validación de datos
            double peso = Double.parseDouble(pesoField.getText());
            if (!ValidacionUtil.esPesoValido(peso)) {
                mostrarMensaje("⚠️ El peso debe ser mayor a 0 y menor a 1000 kg", "orange");
                return;
            }

            double volumen = calcularVolumen();
            if (!ValidacionUtil.esVolumenValido(volumen)) {
                mostrarMensaje("⚠️ El volumen excede el máximo permitido", "orange");
                return;
            }

            // Crear DTOs de direcciones
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), zonaOrigenCombo.getValue());
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), zonaDestinoCombo.getValue());

            // Crear DTO de envío temporal para cálculo
            EnvioDTO envioTempDTO = new EnvioDTO();
            envioTempDTO.setOrigen(origenDTO);
            envioTempDTO.setDestino(destinoDTO);
            envioTempDTO.setPeso(peso);
            envioTempDTO.setVolumen(volumen);
            envioTempDTO.setPrioridad(prioridadCheck.isSelected());
            envioTempDTO.setSeguro(seguroCheck.isSelected());
            envioTempDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioTempDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());

            // Calcular tarifa usando Facade (trabaja con DTOs)
            double total = facade.calcularTarifa(envioTempDTO);
            costoEstimadoActual = total;
            costoLabel.setText(String.format("$ %.2f", total));
            if (crearBtn != null) crearBtn.setDisable(false);
            mostrarMensaje("💰 Costo estimado calculado correctamente", "green");

        } catch (NumberFormatException e) {
            mostrarMensaje("⚠️ El peso o las dimensiones deben ser numéricos", "orange");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al calcular la tarifa", "red");
        }
    }

    /** Crear y pagar usando DTOs */
    @FXML
    protected void handleCrear() {
        try {
            if (usuarioActual == null) usuarioActual = Sesion.getUsuarioActual();

            if (costoEstimadoActual <= 0) {
                mostrarMensaje("⚠️ Primero calcule el costo antes de confirmar", "orange");
                return;
            }

            Alert pagoDialog = new Alert(Alert.AlertType.CONFIRMATION);
            pagoDialog.setTitle("Confirmar Pago");
            pagoDialog.setHeaderText("Total a pagar: $" + costoEstimadoActual);
            pagoDialog.setContentText("¿Desea confirmar el pago y registrar el envío?");
            pagoDialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) procesarEnvioYPago();
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al registrar el envío", "red");
        }
    }

    protected void procesarEnvioYPago() {
        try {
            // Asegurar que tenemos el usuario actual de sesión
            if (usuarioActual == null) {
                usuarioActual = Sesion.getUsuarioActual();
            }

            if (usuarioActual == null) {
                mostrarMensaje("❌ Error: No hay usuario en sesión", "red");
                return;
            }

            if (!validarCampos()) return;

            double peso = Double.parseDouble(pesoField.getText());
            double volumen = calcularVolumen();

            // Crear DTOs
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), zonaOrigenCombo.getValue());
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), zonaDestinoCombo.getValue());

            // Crear envío usando Facade con el usuario actual (trabaja con DTOs)
            EnvioDTO envioDTO = facade.crearEnvio(usuarioActual, origenDTO, destinoDTO, peso);
            envioDTO.setIdUsuario(usuarioActual.getId()); // Asegurar que tiene el ID del usuario
            envioDTO.setVolumen(volumen);
            envioDTO.setPrioridad(prioridadCheck.isSelected());
            envioDTO.setSeguro(seguroCheck.isSelected());
            envioDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());
            envioDTO.setCostoEstimado(costoEstimadoActual);

            // Registrar envío
            facade.registrarEnvio(envioDTO);

            // Registrar pago simulado
            facade.registrarPagoSimulado(envioDTO.getId(), costoEstimadoActual, MetodoPago.TARJETA_CREDITO);

            mostrarMensaje("✅ Envío #" + envioDTO.getId() + " creado para " + usuarioActual.getNombre() + " y repartidor asignado.", "green");
            limpiarCampos();

            if (onEnvioCreado != null) onEnvioCreado.run();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al procesar envío y pago", "red");
        }
    }

    /** Guardar envío (estado SOLICITADO) usando DTOs */
    @FXML
    protected void handleGuardarEnvio() {
        try {
            // Asegurar que tenemos el usuario actual de sesión
            if (usuarioActual == null) {
                usuarioActual = Sesion.getUsuarioActual();
            }

            if (usuarioActual == null) {
                mostrarMensaje("❌ Error: No hay usuario en sesión", "red");
                return;
            }

            if (!validarCampos()) return;

            double peso = Double.parseDouble(pesoField.getText());
            double volumen = calcularVolumen();

            // Crear DTOs
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), zonaOrigenCombo.getValue());
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), zonaDestinoCombo.getValue());

            // Crear envío usando Facade con el usuario actual (trabaja con DTOs)
            EnvioDTO envioDTO = facade.crearEnvio(usuarioActual, origenDTO, destinoDTO, peso);
            envioDTO.setIdUsuario(usuarioActual.getId()); // Asegurar que tiene el ID del usuario
            envioDTO.setVolumen(volumen);
            envioDTO.setPrioridad(prioridadCheck.isSelected());
            envioDTO.setSeguro(seguroCheck.isSelected());
            envioDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());
            envioDTO.setEstado(EnvioDTO.EstadoEnvio.SOLICITADO);

            // Registrar envío
            facade.registrarEnvio(envioDTO);
            mostrarMensaje("✅ Envío #" + envioDTO.getId() + " guardado para " + usuarioActual.getNombre() + " con estado SOLICITADO", "green");
            limpiarCampos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al guardar envío", "red");
        }
    }

    /** Validaciones de campos */
    protected boolean validarCampos() {
        if (ValidacionUtil.isEmpty(origenDireccionField.getText()) ||
                ValidacionUtil.isEmpty(destinoDireccionField.getText()) ||
                ValidacionUtil.isEmpty(pesoField.getText()) ||
                zonaOrigenCombo.getValue() == null ||
                zonaDestinoCombo.getValue() == null ||
                tipoPaqueteCombo.getValue() == null) {
            mostrarMensaje("⚠️ Todos los campos son obligatorios", "orange");
            return false;
        }
        return true;
    }

    /** Cálculo de volumen según tipo */
    protected double calcularVolumen() {
        String tipo = tipoPaqueteCombo.getValue();
        if ("Sobre".equals(tipo)) return 0.001;
        if ("Paquete".equals(tipo)) return 0.01;
        if ("Caja".equals(tipo)) {
            try {
                double largo = Double.parseDouble(largoField.getText());
                double ancho = Double.parseDouble(anchoField.getText());
                double alto = Double.parseDouble(altoField.getText());
                return (largo * ancho * alto) / 1_000_000; // cm³ → m³
            } catch (NumberFormatException e) {
                return 0.005; // Valor por defecto
            }
        }
        return 0.005;
    }

    @FXML
    protected void handleVerDesglose(ActionEvent event) {
        try {
            if (!validarCampos()) return;

            double peso = Double.parseDouble(pesoField.getText());
            double volumen = calcularVolumen();

            // Crear DTOs
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), zonaOrigenCombo.getValue());
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), zonaDestinoCombo.getValue());

            // Crear DTO temporal
            EnvioDTO envioTempDTO = new EnvioDTO();
            envioTempDTO.setOrigen(origenDTO);
            envioTempDTO.setDestino(destinoDTO);
            envioTempDTO.setPeso(peso);
            envioTempDTO.setVolumen(volumen);
            envioTempDTO.setPrioridad(prioridadCheck.isSelected());
            envioTempDTO.setSeguro(seguroCheck.isSelected());
            envioTempDTO.setCostoEstimado(facade.calcularTarifa(envioTempDTO));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detalle_tarifa.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Detalle de Tarifa");
            stage.setScene(new Scene(root));

            DetalleTarifaController controller = loader.getController();
            controller.setStage(stage);
            controller.mostrarDetalle(envioTempDTO);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir el detalle de tarifa.", "red");
        }
    }

    protected void limpiarCampos() {
        origenDireccionField.clear();
        destinoDireccionField.clear();
        pesoField.clear();
        largoField.clear();
        anchoField.clear();
        altoField.clear();
        zonaOrigenCombo.getSelectionModel().clearSelection();
        zonaDestinoCombo.getSelectionModel().clearSelection();
        tipoPaqueteCombo.getSelectionModel().clearSelection();
        prioridadCheck.setSelected(false);
        seguroCheck.setSelected(false);
        if (fragilCheck != null) fragilCheck.setSelected(false);
        if (firmaRequeridaCheck != null) firmaRequeridaCheck.setSelected(false);
        costoLabel.setText("—");
        if (crearBtn != null) crearBtn.setDisable(true);
    }

    protected void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    protected void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public abstract void setEnvioToEdit(EnvioDTO envioDTO);
}
