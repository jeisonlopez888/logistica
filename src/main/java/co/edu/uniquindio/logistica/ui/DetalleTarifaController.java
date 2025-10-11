package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.service.TarifaService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DetalleTarifaController {

    @FXML private Label baseLabel;
    @FXML private Label pesoLabel;
    @FXML private Label volumenLabel;
    @FXML private Label zonaLabel;
    @FXML private Label prioridadLabel;
    @FXML private Label seguroLabel;
    @FXML private Label totalLabel;

    private final TarifaService tarifaService = new TarifaService();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void mostrarDetalle(Envio envio) {
        TarifaService.TarifaDetalle detalle = tarifaService.desglosarTarifa(envio);

        baseLabel.setText(String.format("$ %.2f", detalle.getBase()));
        pesoLabel.setText(String.format("$ %.2f", detalle.getPorPeso()));
        volumenLabel.setText(String.format("$ %.2f", detalle.getPorVolumen()));
        zonaLabel.setText(String.format("$ %.2f", detalle.getRecargoZona()));
        prioridadLabel.setText(String.format("$ %.2f", detalle.getRecargoPrioridad()));
        seguroLabel.setText(String.format("$ %.2f", detalle.getRecargoSeguro()));
        totalLabel.setText(String.format("$ %.2f", detalle.getTotal()));
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) stage.close();
    }
}

