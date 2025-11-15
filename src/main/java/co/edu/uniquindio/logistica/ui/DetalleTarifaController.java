package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador de Detalle de Tarifa - Solo muestra información
 */
public class DetalleTarifaController {

    @FXML private Label baseLabel;
    @FXML private Label pesoLabel;
    @FXML private Label volumenLabel;
    @FXML private Label zonaLabel;
    @FXML private Label prioridadLabel;
    @FXML private Label seguroLabel;
    @FXML private Label fragilLabel;
    @FXML private Label firmaLabel;
    @FXML private Label totalLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void mostrarDetalle(EnvioDTO envioDTO) {
        // Obtener desglose usando Facade (trabaja con DTOs)
        co.edu.uniquindio.logistica.service.TarifaService.TarifaDetalle detalle = facade.desglosarTarifa(envioDTO);

        baseLabel.setText(String.format("$ %,.2f", detalle.getBase()));
        pesoLabel.setText(String.format("$ %,.2f", detalle.getPorPeso()));
        volumenLabel.setText(String.format("$ %,.2f", detalle.getPorVolumen()));
        zonaLabel.setText(String.format("$ %,.2f", detalle.getRecargoZona()));
        prioridadLabel.setText(String.format("$ %,.2f", detalle.getRecargoPrioridad()));
        seguroLabel.setText(String.format("$ %,.2f", detalle.getRecargoSeguro()));
        if (fragilLabel != null) {
            fragilLabel.setText(String.format("$ %,.2f", detalle.getRecargoFragil()));
        }
        if (firmaLabel != null) {
            firmaLabel.setText(String.format("$ %,.2f", detalle.getRecargoFirma()));
        }
        totalLabel.setText(String.format("$ %,.2f", detalle.getTotal()));
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) stage.close();
    }
}
