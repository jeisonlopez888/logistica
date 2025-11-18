package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.DireccionDTO;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.PagoDTO;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * Controlador base para crear/editar envíos - Solo valida datos y usa DTOs
 */
public abstract class CrearEnvioController {

    @FXML protected ComboBox<String> origenDireccionCombo;
    @FXML protected TextField origenDireccionField;
    @FXML protected ComboBox<String> zonaOrigenCombo;
    @FXML protected ComboBox<String> destinoDireccionCombo;
    @FXML protected TextField destinoDireccionField;
    @FXML protected ComboBox<String> zonaDestinoCombo;
    @FXML protected TextField pesoField;
    @FXML protected ComboBox<String> tipoPaqueteCombo;
    @FXML protected ComboBox<String> tipoTarifaCombo;
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
    
    // Labels para valores en tiempo real
    @FXML protected Label tarifaValorLabel;
    @FXML protected Label volumenValorLabel;
    @FXML protected Label prioridadValorLabel;
    @FXML protected Label seguroValorLabel;
    @FXML protected Label fragilValorLabel;
    @FXML protected Label firmaValorLabel;
    
    // Labels para desglose
    @FXML protected Label baseValorLabel;
    @FXML protected Label pesoValorLabel;
    @FXML protected Label volumenDesgloseLabel;
    @FXML protected Label tipoTarifaDesgloseLabel;
    @FXML protected Label zonaValorLabel;
    @FXML protected GridPane desgloseContainer;

    protected final LogisticaFacade facade = LogisticaFacade.getInstance();

    protected UsuarioDTO usuarioActual;
    protected Runnable onEnvioCreado;
    protected double costoEstimadoActual = 0.0;

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        this.usuarioActual = usuarioDTO;
    }

    public void setUsuario(UsuarioDTO usuarioDTO) {
        setUsuarioActual(usuarioDTO);
        cargarDireccionesUsuario();
        // Refrescar tarifas cuando se establece el usuario (por si cambiaron)
        cargarTarifasDisponibles();
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
            actualizarVolumen();
        });

        // Cargar tipos de tarifa dinámicamente desde el DataStore
        cargarTarifasDisponibles();
        
        // Inicializar valores de tarifa
        actualizarValorTarifa();

        // Listener para recalcular costo cuando cambie la tarifa
        if (tipoTarifaCombo != null) {
            tipoTarifaCombo.setOnAction(e -> {
                actualizarValorTarifa();
                // Si ya hay un costo calculado, recalcular con la nueva tarifa
                if (costoEstimadoActual > 0 && !pesoField.getText().isEmpty()) {
                    handleCotizar();
                }
            });
        }
        
        // Listeners para actualizar valores en tiempo real
        if (prioridadCheck != null) {
            prioridadCheck.setOnAction(e -> actualizarValoresOpciones());
        }
        if (seguroCheck != null) {
            seguroCheck.setOnAction(e -> actualizarValoresOpciones());
        }
        if (fragilCheck != null) {
            fragilCheck.setOnAction(e -> actualizarValoresOpciones());
        }
        if (firmaRequeridaCheck != null) {
            firmaRequeridaCheck.setOnAction(e -> actualizarValoresOpciones());
        }
        
        // Listener para actualizar volumen cuando cambien las dimensiones
        if (largoField != null) {
            largoField.textProperty().addListener((obs, oldVal, newVal) -> actualizarVolumen());
        }
        if (anchoField != null) {
            anchoField.textProperty().addListener((obs, oldVal, newVal) -> actualizarVolumen());
        }
        if (altoField != null) {
            altoField.textProperty().addListener((obs, oldVal, newVal) -> actualizarVolumen());
        }
        if (tipoPaqueteCombo != null) {
            tipoPaqueteCombo.setOnAction(e -> actualizarVolumen());
        }

        largoField.setDisable(true);
        anchoField.setDisable(true);
        altoField.setDisable(true);

        if (Sesion.getUsuarioActual() != null) {
            this.usuarioActual = Sesion.getUsuarioActual();
            cargarDireccionesUsuario();
        }
        
        // Configurar listeners para los ComboBox de direcciones
        if (origenDireccionCombo != null) {
            origenDireccionCombo.setOnAction(e -> {
                String seleccion = origenDireccionCombo.getValue();
                if (seleccion != null && !seleccion.equals("Nueva dirección")) {
                    cargarDireccionSeleccionada(seleccion, true);
                } else if (seleccion != null && seleccion.equals("Nueva dirección")) {
                    origenDireccionField.setDisable(false);
                    origenDireccionField.clear();
                    zonaOrigenCombo.setValue(null);
                }
            });
        }
        
        if (destinoDireccionCombo != null) {
            destinoDireccionCombo.setOnAction(e -> {
                String seleccion = destinoDireccionCombo.getValue();
                if (seleccion != null && !seleccion.equals("Nueva dirección")) {
                    cargarDireccionSeleccionada(seleccion, false);
                } else if (seleccion != null && seleccion.equals("Nueva dirección")) {
                    destinoDireccionField.setDisable(false);
                    destinoDireccionField.clear();
                    zonaDestinoCombo.setValue(null);
                }
            });
        }
    }
    
    void cargarDireccionesUsuario() {
        if (usuarioActual == null || usuarioActual.getDirecciones() == null) {
            return;
        }
        
        // Cargar direcciones en ComboBox de origen
        // Mostrar solo la calle o carrera (dir.getCalle()) y la zona
        if (origenDireccionCombo != null) {
            origenDireccionCombo.getItems().clear();
            origenDireccionCombo.getItems().add("Nueva dirección");
            for (DireccionDTO dir : usuarioActual.getDirecciones()) {
                String calle = dir.getCalle() != null ? dir.getCalle() : "";
                String zona = dir.getCiudad() != null ? dir.getCiudad() : "";
                origenDireccionCombo.getItems().add(calle + " (" + zona + ")");
            }
        }
        
        // Cargar direcciones en ComboBox de destino
        // Mostrar solo la calle o carrera (dir.getCalle()) y la zona
        if (destinoDireccionCombo != null) {
            destinoDireccionCombo.getItems().clear();
            destinoDireccionCombo.getItems().add("Nueva dirección");
            for (DireccionDTO dir : usuarioActual.getDirecciones()) {
                String calle = dir.getCalle() != null ? dir.getCalle() : "";
                String zona = dir.getCiudad() != null ? dir.getCiudad() : "";
                destinoDireccionCombo.getItems().add(calle + " (" + zona + ")");
            }
        }
    }
    
    private void cargarDireccionSeleccionada(String seleccion, boolean esOrigen) {
        if (usuarioActual == null || usuarioActual.getDirecciones() == null) {
            return;
        }
        
        // Buscar la dirección que coincida
        // El formato ahora es: "calle (zona)"
        for (DireccionDTO dir : usuarioActual.getDirecciones()) {
            String calle = dir.getCalle() != null ? dir.getCalle() : "";
            String zona = dir.getCiudad() != null ? dir.getCiudad() : "";
            String textoCombo = calle + " (" + zona + ")";
            
            if (seleccion.equals(textoCombo)) {
                if (esOrigen) {
                    origenDireccionField.setText(dir.getCalle());
                    origenDireccionField.setDisable(true);
                    zonaOrigenCombo.setValue(dir.getCiudad());
                } else {
                    destinoDireccionField.setText(dir.getCalle());
                    destinoDireccionField.setDisable(true);
                    zonaDestinoCombo.setValue(dir.getCiudad());
                }
                break;
            }
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

            // Crear DTOs de direcciones (nombre, calle, ciudad, coordenadas)
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), "");
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), "");

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
            envioTempDTO.setTipoTarifa(tipoTarifaCombo != null && tipoTarifaCombo.getValue() != null 
                    ? tipoTarifaCombo.getValue() : "Normal");

            // Calcular tarifa usando Facade (trabaja con DTOs)
            double total = facade.calcularTarifa(envioTempDTO);
            costoEstimadoActual = total;
            
            // Obtener desglose detallado
            co.edu.uniquindio.logistica.service.TarifaService.TarifaDetalle detalle = facade.desglosarTarifa(envioTempDTO);
            
            // Actualizar todos los labels con los valores del desglose
            actualizarDesglose(detalle);
            
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

            // Crear DTOs (nombre, calle, ciudad, coordenadas)
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), "");
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), "");

            // Crear envío usando Facade con el usuario actual (trabaja con DTOs)
            EnvioDTO envioDTO = facade.crearEnvio(usuarioActual, origenDTO, destinoDTO, peso);
            envioDTO.setIdUsuario(usuarioActual.getId()); // Asegurar que tiene el ID del usuario
            envioDTO.setVolumen(volumen);
            envioDTO.setPrioridad(prioridadCheck.isSelected());
            envioDTO.setSeguro(seguroCheck.isSelected());
            envioDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());
            envioDTO.setTipoTarifa(tipoTarifaCombo != null && tipoTarifaCombo.getValue() != null 
                    ? tipoTarifaCombo.getValue() : "Normal");
            envioDTO.setCostoEstimado(costoEstimadoActual);

            // Registrar envío
            facade.registrarEnvio(envioDTO);

            // Mostrar diálogo para elegir método de pago
            MetodoPago metodoSeleccionado = mostrarDialogoMetodoPago("Seleccione el método de pago para el envío");
            if (metodoSeleccionado == null) {
                mostrarMensaje("⚠️ Operación cancelada. El envío fue registrado pero sin pago.", "orange");
                return;
            }

            // Registrar pago simulado
            facade.registrarPagoSimulado(envioDTO.getId(), costoEstimadoActual, metodoSeleccionado);

            // Buscar el pago recién creado y confirmarlo (esto asignará automáticamente un repartidor)
            PagoDTO pagoDTO = facade.buscarPagoPorEnvio(envioDTO.getId());
            if (pagoDTO != null) {
                // Confirmar pago (esto asignará automáticamente un repartidor disponible en la zona de destino)
                String resultado = facade.confirmarPago(pagoDTO.getId());
                mostrarMensaje("✅ Envío #" + envioDTO.getId() + " creado para " + usuarioActual.getNombre() + ". " + resultado, "green");
                
                // Ofrecer imprimir factura
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Factura de Envío");
                alert.setHeaderText("¿Desea imprimir la factura/guía de envío?");
                alert.setContentText("El envío ha sido confirmado. Puede generar la factura ahora.");
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(bt -> {
                    if (bt == ButtonType.YES) {
                        imprimirFactura(envioDTO.getId());
                    }
                });
            } else {
                mostrarMensaje("✅ Envío #" + envioDTO.getId() + " creado para " + usuarioActual.getNombre() + ". Pago registrado pero no confirmado.", "green");
            }
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

            // Crear DTOs (nombre, calle, ciudad, coordenadas)
            DireccionDTO origenDTO = facade.crearDireccion("Origen",
                    origenDireccionField.getText(), zonaOrigenCombo.getValue(), "");
            DireccionDTO destinoDTO = facade.crearDireccion("Destino",
                    destinoDireccionField.getText(), zonaDestinoCombo.getValue(), "");

            // Crear envío usando Facade con el usuario actual (trabaja con DTOs)
            EnvioDTO envioDTO = facade.crearEnvio(usuarioActual, origenDTO, destinoDTO, peso);
            envioDTO.setIdUsuario(usuarioActual.getId()); // Asegurar que tiene el ID del usuario
            envioDTO.setVolumen(volumen);
            envioDTO.setPrioridad(prioridadCheck.isSelected());
            envioDTO.setSeguro(seguroCheck.isSelected());
            envioDTO.setFragil(fragilCheck != null && fragilCheck.isSelected());
            envioDTO.setFirmaRequerida(firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected());
            envioDTO.setTipoTarifa(tipoTarifaCombo != null && tipoTarifaCombo.getValue() != null 
                    ? tipoTarifaCombo.getValue() : "Normal");
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
                tipoPaqueteCombo.getValue() == null ||
                (tipoTarifaCombo != null && tipoTarifaCombo.getValue() == null)) {
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
    /**
     * Actualiza el volumen mostrado cuando cambian las dimensiones
     */
    protected void actualizarVolumen() {
        if (volumenValorLabel == null) return;
        try {
            double volumen = calcularVolumen();
            volumenValorLabel.setText(String.format("Volumen: %.4f m³", volumen));
        } catch (Exception e) {
            volumenValorLabel.setText("Volumen: 0.0000 m³");
        }
    }
    
    /**
     * Actualiza el valor de la tarifa seleccionada
     */
    protected void actualizarValorTarifa() {
        if (tarifaValorLabel == null || tipoTarifaCombo == null) return;
        String tarifaSeleccionada = tipoTarifaCombo.getValue();
        if (tarifaSeleccionada != null) {
            try {
                java.util.List<co.edu.uniquindio.logistica.model.DTO.TarifaDTO> tarifas = facade.getTarifas();
                co.edu.uniquindio.logistica.model.DTO.TarifaDTO tarifa = tarifas.stream()
                    .filter(t -> t.getDescripcion() != null && t.getDescripcion().equals(tarifaSeleccionada))
                    .findFirst()
                    .orElse(null);
                if (tarifa != null) {
                    tarifaValorLabel.setText(String.format("$ %.2f", tarifa.getCostoBase()));
                } else {
                    tarifaValorLabel.setText("");
                }
            } catch (Exception e) {
                tarifaValorLabel.setText("");
            }
        } else {
            tarifaValorLabel.setText("");
        }
    }
    
    /**
     * Actualiza los valores de las opciones cuando se marcan/desmarcan
     */
    protected void actualizarValoresOpciones() {
        // Solo actualizar si ya hay un costo calculado
        if (costoEstimadoActual > 0 && !pesoField.getText().isEmpty()) {
            handleCotizar();
        } else {
            // Limpiar valores si no hay cálculo
            if (prioridadValorLabel != null) prioridadValorLabel.setText("");
            if (seguroValorLabel != null) seguroValorLabel.setText("");
            if (fragilValorLabel != null) fragilValorLabel.setText("");
            if (firmaValorLabel != null) firmaValorLabel.setText("");
        }
    }
    
    /**
     * Actualiza todos los labels del desglose con los valores calculados
     */
    protected void actualizarDesglose(co.edu.uniquindio.logistica.service.TarifaService.TarifaDetalle detalle) {
        if (detalle == null) return;
        
        // Actualizar desglose principal
        if (baseValorLabel != null) {
            baseValorLabel.setText(String.format("$ %,.2f", detalle.getBase()));
        }
        if (pesoValorLabel != null) {
            pesoValorLabel.setText(String.format("$ %,.2f", detalle.getPorPeso()));
        }
        if (volumenDesgloseLabel != null) {
            volumenDesgloseLabel.setText(String.format("$ %,.2f", detalle.getPorVolumen()));
        }
        if (tipoTarifaDesgloseLabel != null && tipoTarifaCombo != null) {
            String tarifa = tipoTarifaCombo.getValue() != null ? tipoTarifaCombo.getValue() : "Normal";
            tipoTarifaDesgloseLabel.setText(tarifa);
        }
        if (zonaValorLabel != null) {
            zonaValorLabel.setText(String.format("$ %,.2f", detalle.getRecargoZona()));
        }
        
        // Actualizar valores de opciones individuales
        if (prioridadValorLabel != null) {
            if (prioridadCheck != null && prioridadCheck.isSelected()) {
                prioridadValorLabel.setText(String.format("$ %,.2f", detalle.getRecargoPrioridad()));
            } else {
                prioridadValorLabel.setText("");
            }
        }
        if (seguroValorLabel != null) {
            if (seguroCheck != null && seguroCheck.isSelected()) {
                seguroValorLabel.setText(String.format("$ %,.2f", detalle.getRecargoSeguro()));
            } else {
                seguroValorLabel.setText("");
            }
        }
        if (fragilValorLabel != null) {
            if (fragilCheck != null && fragilCheck.isSelected()) {
                fragilValorLabel.setText(String.format("$ %,.2f", detalle.getRecargoFragil()));
            } else {
                fragilValorLabel.setText("");
            }
        }
        if (firmaValorLabel != null) {
            if (firmaRequeridaCheck != null && firmaRequeridaCheck.isSelected()) {
                firmaValorLabel.setText(String.format("$ %,.2f", detalle.getRecargoFirma()));
            } else {
                firmaValorLabel.setText("");
            }
        }
        
        // Actualizar total - usar costoLabel si existe, sino buscar costoTotalLabel
        if (costoLabel != null) {
            costoLabel.setText(String.format("$ %,.2f", detalle.getTotal()));
        }
        // También actualizar el label del desglose si existe
        try {
            javafx.scene.Node node = desgloseContainer != null ? 
                desgloseContainer.lookup("#costoTotalLabel") : null;
            if (node instanceof Label) {
                ((Label) node).setText(String.format("$ %,.2f", detalle.getTotal()));
            }
        } catch (Exception e) {
            // Ignorar si no existe
        }
    }

    protected void limpiarCampos() {
        if (origenDireccionCombo != null) {
            origenDireccionCombo.getSelectionModel().clearSelection();
        }
        if (destinoDireccionCombo != null) {
            destinoDireccionCombo.getSelectionModel().clearSelection();
        }
        origenDireccionField.clear();
        origenDireccionField.setDisable(false);
        destinoDireccionField.clear();
        destinoDireccionField.setDisable(false);
        pesoField.clear();
        largoField.clear();
        anchoField.clear();
        altoField.clear();
        zonaOrigenCombo.getSelectionModel().clearSelection();
        zonaDestinoCombo.getSelectionModel().clearSelection();
        tipoPaqueteCombo.getSelectionModel().clearSelection();
        if (tipoTarifaCombo != null) {
            tipoTarifaCombo.setValue("Normal");
        }
        prioridadCheck.setSelected(false);
        seguroCheck.setSelected(false);
        if (fragilCheck != null) fragilCheck.setSelected(false);
        if (firmaRequeridaCheck != null) firmaRequeridaCheck.setSelected(false);
        costoLabel.setText("—");
        if (crearBtn != null) crearBtn.setDisable(true);
    }

    /**
     * Carga las tarifas disponibles desde el DataStore y las actualiza en el ComboBox
     */
    protected void cargarTarifasDisponibles() {
        if (tipoTarifaCombo != null) {
            tipoTarifaCombo.getItems().clear();
            java.util.List<String> nombresTarifas = facade.getNombresTarifas();
            if (!nombresTarifas.isEmpty()) {
                tipoTarifaCombo.getItems().addAll(nombresTarifas);
                // Establecer el primer valor como predeterminado si no hay selección
                if (tipoTarifaCombo.getValue() == null) {
                    tipoTarifaCombo.setValue(nombresTarifas.get(0));
                }
            } else {
                // Fallback si no hay tarifas
                tipoTarifaCombo.getItems().addAll("Normal", "Express");
                tipoTarifaCombo.setValue("Normal");
            }
        }
        // Actualizar valor de tarifa después de cargar
        actualizarValorTarifa();
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

    /**
     * Muestra un diálogo para seleccionar el canal de notificación
     * @return CanalNotificacion seleccionado o null si se canceló
     */
    protected co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion mostrarDialogoCanalNotificacion() {
        Alert canalDialog = new Alert(Alert.AlertType.CONFIRMATION);
        canalDialog.setTitle("Canal de Notificación");
        canalDialog.setHeaderText("Seleccione cómo desea recibir las notificaciones sobre su envío");
        canalDialog.getButtonTypes().setAll(
            new ButtonType("📱 WhatsApp", ButtonBar.ButtonData.YES),
            new ButtonType("📧 Correo Electrónico", ButtonBar.ButtonData.NO),
            new ButtonType("💬 SMS", ButtonBar.ButtonData.CANCEL_CLOSE),
            ButtonType.CANCEL
        );
        
        final co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion[] canalSeleccionado = {null};
        canalDialog.showAndWait().ifPresent(opcion -> {
            if (opcion.getButtonData() == ButtonBar.ButtonData.YES) {
                canalSeleccionado[0] = co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion.WHATSAPP;
            } else if (opcion.getButtonData() == ButtonBar.ButtonData.NO) {
                canalSeleccionado[0] = co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion.EMAIL;
            } else if (opcion.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                canalSeleccionado[0] = co.edu.uniquindio.logistica.service.NotificationService.CanalNotificacion.SMS;
            } else {
                canalSeleccionado[0] = null;
            }
        });
        return canalSeleccionado[0];
    }
    
    /**
     * Muestra un diálogo para seleccionar el método de pago
     * @param mensaje Mensaje a mostrar en el diálogo
     * @return MetodoPago seleccionado o null si se canceló
     */
    protected MetodoPago mostrarDialogoMetodoPago(String mensaje) {
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
    
    /**
     * Imprime la factura/guía de envío en PDF
     */
    protected void imprimirFactura(Long envioId) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Factura de Envío");
            fileChooser.setInitialFileName("factura_envio_" + envioId + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            Stage stage = (Stage) (pesoField != null ? pesoField.getScene().getWindow() : null);
            if (stage == null) {
                mostrarMensaje("❌ Error: No se pudo obtener la ventana", "red");
                return;
            }
            
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                facade.generarFacturaEnvio(envioId, file.getAbsolutePath());
                mostrarMensaje("✅ Factura generada exitosamente: " + file.getName(), "green");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Factura Generada");
                alert.setHeaderText("Factura generada exitosamente");
                alert.setContentText("La factura se ha guardado en:\n" + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al generar la factura: " + e.getMessage(), "red");
        }
    }

    @FXML
    public abstract void setEnvioToEdit(EnvioDTO envioDTO);
}
