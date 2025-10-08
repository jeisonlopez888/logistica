package co.edu.uniquindio.logistica.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ReportDialogUtil {

    /**
     * Muestra un diálogo para guardar un archivo Excel o PDF.
     * @param stage Ventana base (Stage principal o ventana actual)
     * @param tipo "excel" o "pdf"
     * @return Ruta absoluta del archivo elegido o null si se cancela
     */
    public static String elegirRutaGuardado(Stage stage, String tipo) {
        FileChooser fileChooser = new FileChooser();

        if ("excel".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivo Excel (*.xlsx)", "*.xlsx")
            );
            fileChooser.setInitialFileName("reporte_envios.xlsx");
        } else if ("pdf".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf")
            );
            fileChooser.setInitialFileName("reporte_envios.pdf");
        }

        fileChooser.setTitle("Guardar reporte como...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File archivo = fileChooser.showSaveDialog(stage);
        return archivo != null ? archivo.getAbsolutePath() : null;
    }
}

