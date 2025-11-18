package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
    @FXML private Label tipoTarifaLabel;
    @FXML private Label totalLabel;
    @FXML private javafx.scene.layout.GridPane gridPane;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private EnvioDTO envioDTO;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void initialize() {
        // Inicialización si es necesaria
    }

    private void ocultarFila(int rowIndex) {
        if (gridPane != null) {
            gridPane.getChildren().stream()
                    .filter(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) == rowIndex)
                    .forEach(n -> {
                        n.setVisible(false);
                        n.setManaged(false);
                    });
        }
    }

    private void mostrarFila(int rowIndex) {
        if (gridPane != null) {
            gridPane.getChildren().stream()
                    .filter(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) == rowIndex)
                    .forEach(n -> {
                        n.setVisible(true);
                        n.setManaged(true);
                    });
        }
    }

    public void mostrarDetalle(EnvioDTO envioDTO) {
        this.envioDTO = envioDTO;
        // Obtener desglose usando Facade (trabaja con DTOs)
        co.edu.uniquindio.logistica.service.TarifaService.TarifaDetalle detalle = facade.desglosarTarifa(envioDTO);

        // Siempre mostrar estos valores base
        baseLabel.setText(String.format("$ %,.2f", detalle.getBase()));
        pesoLabel.setText(String.format("$ %,.2f", detalle.getPorPeso()));
        volumenLabel.setText(String.format("$ %,.2f", detalle.getPorVolumen()));
        
        // Tipo de tarifa (siempre visible)
        if (tipoTarifaLabel != null) {
            String tipoTarifa = envioDTO.getTipoTarifa() != null && !envioDTO.getTipoTarifa().isEmpty() 
                ? envioDTO.getTipoTarifa() 
                : "Normal";
            tipoTarifaLabel.setText(tipoTarifa);
        }
        
        // Zona solo si tiene recargo
        if (detalle.getRecargoZona() > 0) {
            zonaLabel.setText(String.format("$ %,.2f", detalle.getRecargoZona()));
            mostrarFila(4);
        } else {
            ocultarFila(4);
        }
        
        // Prioridad solo si fue seleccionada
        if (envioDTO.isPrioridad() && detalle.getRecargoPrioridad() > 0) {
            prioridadLabel.setText(String.format("$ %,.2f", detalle.getRecargoPrioridad()));
            mostrarFila(5);
        } else {
            ocultarFila(5);
        }
        
        // Seguro solo si fue seleccionado
        if (envioDTO.isSeguro() && detalle.getRecargoSeguro() > 0) {
            seguroLabel.setText(String.format("$ %,.2f", detalle.getRecargoSeguro()));
            mostrarFila(6);
        } else {
            ocultarFila(6);
        }
        
        // Frágil solo si fue seleccionado
        if (fragilLabel != null) {
            if (envioDTO.isFragil() && detalle.getRecargoFragil() > 0) {
                fragilLabel.setText(String.format("$ %,.2f", detalle.getRecargoFragil()));
                mostrarFila(7);
            } else {
                ocultarFila(7);
            }
        }
        
        // Firma solo si fue seleccionada
        if (firmaLabel != null) {
            if (envioDTO.isFirmaRequerida() && detalle.getRecargoFirma() > 0) {
                firmaLabel.setText(String.format("$ %,.2f", detalle.getRecargoFirma()));
                mostrarFila(8);
            } else {
                ocultarFila(8);
            }
        }
        
        totalLabel.setText(String.format("$ %,.2f", detalle.getTotal()));
    }

    @FXML
    private void handleCerrar() {
        // Cerrar la ventana modal (se abre desde crear_envio)
        Stage stage = (Stage) baseLabel.getScene().getWindow();
        stage.close();
    }
}
