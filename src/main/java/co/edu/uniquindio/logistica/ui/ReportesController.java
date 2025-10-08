package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.util.ReportUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ReportesController {

    @FXML private ComboBox<String> tipoReporteCombo;
    @FXML private RadioButton excelRadio;
    @FXML private RadioButton pdfRadio;
    @FXML private Label mensajeLabel;

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
            // ✅ Generar nombre automático con fecha
            String nombreArchivo = ReportUtil.agregarFechaNombre(tipo, esExcel);
            String ruta = carpeta.getAbsolutePath() + "/" + nombreArchivo;

            switch (tipo) {
                case "Usuarios" -> {
                    if (esExcel)
                        ReportUtil.exportarUsuariosExcel(facade.listarUsuarios(), ruta);
                    else
                        ReportUtil.exportarUsuariosPDF(facade.listarUsuarios(), ruta);
                }
                case "Envíos" -> {
                    if (esExcel)
                        ReportUtil.exportarEnviosExcel(facade.listarTodosEnvios(), ruta);
                    else
                        ReportUtil.exportarEnviosPDF(facade.listarTodosEnvios(), ruta);
                }
                case "Pagos" -> {
                    if (esExcel)
                        ReportUtil.exportarPagosExcel(facade.getPagos(), ruta);
                    else
                        ReportUtil.exportarPagosPDF(facade.getPagos(), ruta);
                }
                case "Repartidores" -> {
                    if (esExcel)
                        ReportUtil.exportarRepartidoresExcel(facade.getRepartidores(), ruta);
                    else
                        ReportUtil.exportarRepartidoresPDF(facade.getRepartidores(), ruta);
                }
                case "Reporte General" -> {
                    if (esExcel) {
                        ReportUtil.exportarReporteGeneralExcel(
                                facade.listarUsuarios(),
                                facade.listarTodosEnvios(),
                                facade.getPagos(),
                                facade.getRepartidores(),
                                ruta
                        );
                    } else {
                        ReportUtil.exportarReporteGeneralPDF(
                                facade.listarUsuarios(),
                                facade.listarTodosEnvios(),
                                facade.getPagos(),
                                facade.getRepartidores(),
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

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}

