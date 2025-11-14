package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.model.DTO.*;
import co.edu.uniquindio.logistica.util.Mappers.*;
import co.edu.uniquindio.logistica.util.ReportUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de Reportes - Solo valida datos, usa DTOs y convierte a entidades para exportar
 */
public class ReportesController {

    @FXML private ComboBox<String> tipoReporteCombo;
    @FXML private RadioButton excelRadio;
    @FXML private RadioButton pdfRadio;
    @FXML private Label mensajeLabel;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private CheckBox usarFiltroFechas;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        tipoReporteCombo.getItems().addAll(
                "Usuarios",
                "Envíos",
                "Pagos",
                "Repartidores",
                "Reporte General"
        );
        tipoReporteCombo.getSelectionModel().selectFirst();

        ToggleGroup formatoGroup = new ToggleGroup();
        excelRadio.setToggleGroup(formatoGroup);
        pdfRadio.setToggleGroup(formatoGroup);
        excelRadio.setSelected(true);

        // Inicializar fechas (último mes por defecto)
        fechaInicio.setValue(LocalDate.now().minusMonths(1));
        fechaFin.setValue(LocalDate.now());
        usarFiltroFechas.setSelected(false);
    }

    @FXML
    private void handleGenerarReporte() {
        String tipo = tipoReporteCombo.getValue();
        boolean esExcel = excelRadio.isSelected();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar carpeta para guardar el reporte");
        File carpeta = chooser.showDialog(new Stage());
        if (carpeta == null) {
            mostrarMensaje("⚠️ No se seleccionó ninguna carpeta", "red");
            return;
        }

        try {
            // Generar nombre automático con fecha
            String nombreArchivo = ReportUtil.agregarFechaNombre(tipo, esExcel);
            String ruta = carpeta.getAbsolutePath() + "/" + nombreArchivo;

            // Filtrar datos por fecha si está habilitado
            List<EnvioDTO> enviosDTOFiltrados = obtenerEnviosFiltrados();
            List<PagoDTO> pagosDTOFiltrados = obtenerPagosFiltrados();

            // Convertir DTOs a entidades para ReportUtil
            List<Envio> enviosFiltrados = enviosDTOFiltrados.stream()
                    .map(EnvioMapper::toEntity)
                    .collect(Collectors.toList());
            List<Pago> pagosFiltrados = pagosDTOFiltrados.stream()
                    .map(PagoMapper::toEntity)
                    .collect(Collectors.toList());

            switch (tipo) {
                case "Usuarios" -> {
                    // Convertir DTOs a entidades
                    List<Usuario> usuarios = facade.listarUsuarios().stream()
                            .map(UsuarioMapper::toEntity)
                            .collect(Collectors.toList());
                    if (esExcel)
                        ReportUtil.exportarUsuariosExcel(usuarios, ruta);
                    else
                        ReportUtil.exportarUsuariosPDF(usuarios, ruta);
                }
                case "Envíos" -> {
                    if (esExcel)
                        ReportUtil.exportarEnviosExcel(enviosFiltrados, ruta);
                    else
                        ReportUtil.exportarEnviosPDF(enviosFiltrados, ruta);
                }
                case "Pagos" -> {
                    if (esExcel)
                        ReportUtil.exportarPagosExcel(pagosFiltrados, ruta);
                    else
                        ReportUtil.exportarPagosPDF(pagosFiltrados, ruta);
                }
                case "Repartidores" -> {
                    // Convertir DTOs a entidades
                    List<Repartidor> repartidores = facade.listarRepartidores().stream()
                            .map(RepartidorMapper::toEntity)
                            .collect(Collectors.toList());
                    if (esExcel)
                        ReportUtil.exportarRepartidoresExcel(repartidores, ruta);
                    else
                        ReportUtil.exportarRepartidoresPDF(repartidores, ruta);
                }
                case "Reporte General" -> {
                    // Convertir todos los DTOs a entidades
                    List<Usuario> usuarios = facade.listarUsuarios().stream()
                            .map(UsuarioMapper::toEntity)
                            .collect(Collectors.toList());
                    List<Repartidor> repartidores = facade.listarRepartidores().stream()
                            .map(RepartidorMapper::toEntity)
                            .collect(Collectors.toList());
                    
                    if (esExcel) {
                        ReportUtil.exportarReporteGeneralExcel(
                                usuarios,
                                enviosFiltrados,
                                pagosFiltrados,
                                repartidores,
                                ruta
                        );
                    } else {
                        ReportUtil.exportarReporteGeneralPDF(
                                usuarios,
                                enviosFiltrados,
                                pagosFiltrados,
                                repartidores,
                                ruta
                        );
                    }
                }
                default -> mostrarMensaje("⚠️ Tipo de reporte desconocido", "orange");
            }

            mostrarMensaje("✅ Reporte generado correctamente:\n" + ruta, "green");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al generar el reporte: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleVolverAdmin(ActionEvent event) {
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

    /**
     * Filtra envíos por rango de fechas si el filtro está habilitado - usa DTOs
     */
    private List<EnvioDTO> obtenerEnviosFiltrados() {
        List<EnvioDTO> envios = facade.listarTodosEnvios();
        
        if (!usarFiltroFechas.isSelected() || fechaInicio.getValue() == null || fechaFin.getValue() == null) {
            return envios;
        }

        LocalDateTime inicio = fechaInicio.getValue().atStartOfDay();
        LocalDateTime fin = fechaFin.getValue().atTime(23, 59, 59);

        return envios.stream()
                .filter(e -> e.getFechaCreacion() != null)
                .filter(e -> !e.getFechaCreacion().isBefore(inicio) && !e.getFechaCreacion().isAfter(fin))
                .collect(Collectors.toList());
    }

    /**
     * Filtra pagos por rango de fechas si el filtro está habilitado - usa DTOs
     */
    private List<PagoDTO> obtenerPagosFiltrados() {
        List<PagoDTO> pagos = facade.getPagos();
        
        if (!usarFiltroFechas.isSelected() || fechaInicio.getValue() == null || fechaFin.getValue() == null) {
            return pagos;
        }

        LocalDateTime inicio = fechaInicio.getValue().atStartOfDay();
        LocalDateTime fin = fechaFin.getValue().atTime(23, 59, 59);

        return pagos.stream()
                .filter(p -> p.getFechaPago() != null)
                .filter(p -> !p.getFechaPago().isBefore(inicio) && !p.getFechaPago().isAfter(fin))
                .collect(Collectors.toList());
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
