package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.util.Sesion;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;

/**
 * Controlador de Rastreo - Solo valida datos y se comunica con Facade usando DTOs
 */
public class RastreoController {

    @FXML private TextField idEnvioField;
    @FXML private Label resultadoLabel;
    @FXML private VBox detalleContainer;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void handleBuscar() {
        // Validación de datos de entrada
        String idTexto = idEnvioField.getText();
        if (ValidacionUtil.isEmpty(idTexto)) {
            resultadoLabel.setText("❌ Ingrese un ID de envío");
            resultadoLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            long id = Long.parseLong(idTexto);
            
            if (!ValidacionUtil.esIdValido(id)) {
                resultadoLabel.setText("❌ ID inválido");
                resultadoLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Comunicación con Facade (trabaja con DTOs)
            EnvioDTO envioDTO = facade.buscarEnvioPorId(id);
            
            if (envioDTO != null) {
                mostrarDetalleEnvio(envioDTO);
            } else {
                if (detalleContainer != null) {
                    detalleContainer.getChildren().clear();
                    Label errorLabel = new Label("❌ Envío no encontrado");
                    errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");
                    detalleContainer.getChildren().add(errorLabel);
                } else {
                    resultadoLabel.setText("❌ Envío no encontrado");
                    resultadoLabel.setStyle("-fx-text-fill: red;");
                }
            }
        } catch (NumberFormatException e) {
            if (detalleContainer != null) {
                detalleContainer.getChildren().clear();
                Label errorLabel = new Label("❌ ID debe ser un número válido");
                errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");
                detalleContainer.getChildren().add(errorLabel);
            } else {
                resultadoLabel.setText("❌ ID debe ser un número válido");
                resultadoLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (detalleContainer != null) {
                detalleContainer.getChildren().clear();
                Label errorLabel = new Label("❌ Error al buscar envío");
                errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");
                detalleContainer.getChildren().add(errorLabel);
            } else {
                resultadoLabel.setText("❌ Error al buscar envío");
                resultadoLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
    
    private void mostrarDetalleEnvio(EnvioDTO envioDTO) {
        if (detalleContainer == null) {
            // Fallback al label si no existe el container
            StringBuilder info = new StringBuilder();
            info.append(String.format("📦 Envío #%d\n\n", envioDTO.getId()));
            String estado = envioDTO.getEstado() != null ? envioDTO.getEstado().name() : "Sin estado";
            info.append(String.format("%s Estado: %s\n\n", getEstadoEmoji(estado), estado));
            resultadoLabel.setText(info.toString());
            resultadoLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13px;");
            return;
        }
        
        detalleContainer.getChildren().clear();
        
        // Título principal
        Label tituloLabel = new Label(String.format("📦 Envío #%d", envioDTO.getId()));
        tituloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #37474F;");
        detalleContainer.getChildren().add(tituloLabel);
        
        // Estado
        String estado = envioDTO.getEstado() != null ? envioDTO.getEstado().name() : "Sin estado";
        String estadoEmoji = getEstadoEmoji(estado);
        Label estadoLabel = new Label(String.format("%s Estado: %s", estadoEmoji, estado));
        estadoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #43A047;");
        detalleContainer.getChildren().add(estadoLabel);
        
        detalleContainer.getChildren().add(new Separator());
        
        // Información en dos columnas
        HBox infoPrincipal = new HBox(30);
        infoPrincipal.setPadding(new Insets(10, 0, 10, 0));
        
        // Columna izquierda: Origen y Destino
        VBox columnaIzq = new VBox(15);
        columnaIzq.setPrefWidth(300);
        
        // Origen
        VBox origenBox = new VBox(5);
        Label origenTitulo = new Label("📍 Origen:");
        origenTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37474F;");
        origenBox.getChildren().add(origenTitulo);
        if (envioDTO.getOrigen() != null) {
            Label origenDir = new Label("Dirección: " + (envioDTO.getOrigen().getCalle() != null ? envioDTO.getOrigen().getCalle() : "N/A"));
            origenDir.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            Label origenZona = new Label("Zona: " + (envioDTO.getOrigen().getCiudad() != null ? envioDTO.getOrigen().getCiudad() : "N/A"));
            origenZona.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            origenBox.getChildren().addAll(origenDir, origenZona);
        }
        columnaIzq.getChildren().add(origenBox);
        
        // Destino
        VBox destinoBox = new VBox(5);
        Label destinoTitulo = new Label("🎯 Destino:");
        destinoTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37474F;");
        destinoBox.getChildren().add(destinoTitulo);
        if (envioDTO.getDestino() != null) {
            Label destinoDir = new Label("Dirección: " + (envioDTO.getDestino().getCalle() != null ? envioDTO.getDestino().getCalle() : "N/A"));
            destinoDir.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            Label destinoZona = new Label("Zona: " + (envioDTO.getDestino().getCiudad() != null ? envioDTO.getDestino().getCiudad() : "N/A"));
            destinoZona.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            destinoBox.getChildren().addAll(destinoDir, destinoZona);
        }
        columnaIzq.getChildren().add(destinoBox);
        
        // Columna derecha: Detalles del paquete
        VBox columnaDer = new VBox(15);
        columnaDer.setPrefWidth(300);
        
        VBox detallesBox = new VBox(8);
        Label detallesTitulo = new Label("📦 Detalles del Paquete:");
        detallesTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37474F;");
        detallesBox.getChildren().add(detallesTitulo);
        
        Label pesoLabel = new Label("Peso: " + String.format("%.2f kg", envioDTO.getPeso()));
        pesoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
        Label volumenLabel = new Label("Volumen: " + String.format("%.4f m³", envioDTO.getVolumen()));
        volumenLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
        Label costoLabel = new Label("Costo: $" + String.format("%,.2f", envioDTO.getCostoEstimado()));
        costoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
        
        // Opciones adicionales
        HBox opcionesBox = new HBox(10);
        if (envioDTO.isPrioridad()) {
            Label prioridad = new Label("⚡ Prioridad");
            prioridad.setStyle("-fx-font-size: 11px; -fx-text-fill: #FF6F00;");
            opcionesBox.getChildren().add(prioridad);
        }
        if (envioDTO.isSeguro()) {
            Label seguro = new Label("🛡️ Seguro");
            seguro.setStyle("-fx-font-size: 11px; -fx-text-fill: #1976D2;");
            opcionesBox.getChildren().add(seguro);
        }
        if (envioDTO.isFragil()) {
            Label fragil = new Label("⚠️ Frágil");
            fragil.setStyle("-fx-font-size: 11px; -fx-text-fill: #F57C00;");
            opcionesBox.getChildren().add(fragil);
        }
        if (envioDTO.isFirmaRequerida()) {
            Label firma = new Label("✍️ Firma");
            firma.setStyle("-fx-font-size: 11px; -fx-text-fill: #7B1FA2;");
            opcionesBox.getChildren().add(firma);
        }
        
        detallesBox.getChildren().addAll(pesoLabel, volumenLabel, costoLabel);
        if (!opcionesBox.getChildren().isEmpty()) {
            detallesBox.getChildren().add(opcionesBox);
        }
        columnaDer.getChildren().add(detallesBox);
        
        infoPrincipal.getChildren().addAll(columnaIzq, columnaDer);
        detalleContainer.getChildren().add(infoPrincipal);
        
        detalleContainer.getChildren().add(new Separator());
        
        // Fechas en dos columnas
        GridPane fechasGrid = new GridPane();
        fechasGrid.setHgap(20);
        fechasGrid.setVgap(10);
        fechasGrid.setPadding(new Insets(10, 0, 10, 0));
        
        int row = 0;
        if (envioDTO.getFechaCreacion() != null) {
            Label fechaCreacion = new Label("📅 Creación: " + envioDTO.getFechaCreacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            fechaCreacion.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            fechasGrid.add(fechaCreacion, 0, row);
        }
        if (envioDTO.getFechaConfirmacion() != null) {
            Label fechaConfirmacion = new Label("✅ Confirmación: " + envioDTO.getFechaConfirmacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            fechaConfirmacion.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            fechasGrid.add(fechaConfirmacion, 1, row++);
        }
        if (envioDTO.getFechaEntregaEstimada() != null) {
            Label fechaEstimada = new Label("⏰ Est. Entrega: " + envioDTO.getFechaEntregaEstimada().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            fechaEstimada.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
            fechasGrid.add(fechaEstimada, 0, row);
        }
        if (envioDTO.getFechaEntrega() != null) {
            Label fechaEntrega = new Label("🎉 Entrega Real: " + envioDTO.getFechaEntrega().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            fechaEntrega.setStyle("-fx-font-size: 12px; -fx-text-fill: #43A047; -fx-font-weight: bold;");
            fechasGrid.add(fechaEntrega, 1, row);
        }
        
        if (fechasGrid.getChildren().size() > 0) {
            detalleContainer.getChildren().add(fechasGrid);
        }
        
        // Repartidor
        if (envioDTO.getIdRepartidor() != null) {
            detalleContainer.getChildren().add(new Separator());
            VBox repartidorBox = new VBox(5);
            Label repartidorTitulo = new Label("🚚 Repartidor Asignado:");
            repartidorTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37474F;");
            repartidorBox.getChildren().add(repartidorTitulo);
            
            try {
                co.edu.uniquindio.logistica.model.DTO.RepartidorDTO repartidor = facade.obtenerRepartidorCompleto(envioDTO.getIdRepartidor());
                if (repartidor != null) {
                    Label repartidorNombre = new Label("Nombre: " + (repartidor.getNombre() != null ? repartidor.getNombre() : "N/A"));
                    repartidorNombre.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
                    Label repartidorTel = new Label("Teléfono: " + (repartidor.getTelefono() != null ? repartidor.getTelefono() : "N/A"));
                    repartidorTel.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
                    Label repartidorZona = new Label("Zona: " + (repartidor.getZona() != null ? repartidor.getZona() : "N/A"));
                    repartidorZona.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
                    repartidorBox.getChildren().addAll(repartidorNombre, repartidorTel, repartidorZona);
                }
            } catch (Exception e) {
                Label errorRepartidor = new Label("No se pudo cargar información del repartidor");
                errorRepartidor.setStyle("-fx-font-size: 12px; -fx-text-fill: #F57C00;");
                repartidorBox.getChildren().add(errorRepartidor);
            }
            detalleContainer.getChildren().add(repartidorBox);
        }
        
        // Incidencia
        if (envioDTO.getIncidenciaDescripcion() != null && !envioDTO.getIncidenciaDescripcion().isEmpty()) {
            detalleContainer.getChildren().add(new Separator());
            VBox incidenciaBox = new VBox(5);
            Label incidenciaTitulo = new Label("⚠️ INCIDENCIA:");
            incidenciaTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
            incidenciaBox.getChildren().add(incidenciaTitulo);
            
            Label incidenciaDesc = new Label(envioDTO.getIncidenciaDescripcion());
            incidenciaDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A; -fx-wrap-text: true;");
            incidenciaDesc.setMaxWidth(600);
            incidenciaBox.getChildren().add(incidenciaDesc);
            
            if (envioDTO.getFechaIncidencia() != null) {
                Label fechaIncidencia = new Label("Fecha: " + envioDTO.getFechaIncidencia().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                fechaIncidencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");
                incidenciaBox.getChildren().add(fechaIncidencia);
            }
            detalleContainer.getChildren().add(incidenciaBox);
        }
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            // Verificar si hay un usuario en sesión para determinar a dónde volver
            if (Sesion.getUsuarioActual() != null) {
                UsuarioDTO usuarioActual = Sesion.getUsuarioActual();
                FXMLLoader loader;
                Parent root;
                
                if (usuarioActual.isAdmin()) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
                    root = loader.load();
                    AdminController controller = loader.getController();
                    controller.setUsuario(usuarioActual);
                } else {
                    loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
                    root = loader.load();
                    UserController controller = loader.getController();
                    controller.setUsuario(usuarioActual);
                }
                
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(usuarioActual.isAdmin() ? "Panel de Administración" : "Panel de Usuario");
                stage.show();
            } else {
                Sesion.cerrarSesion();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver.");
        }
    }

    private String getEstadoEmoji(String estado) {
        if (estado == null) return "❓";
        return switch (estado.toUpperCase()) {
            case "SOLICITADO" -> "📝";
            case "CONFIRMADO" -> "✅";
            case "ASIGNADO" -> "👤";
            case "EN_RUTA" -> "🚚";
            case "ENTREGADO" -> "🎉";
            case "CANCELADO" -> "❌";
            case "INCIDENCIA" -> "⚠️";
            default -> "❓";
        };
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
