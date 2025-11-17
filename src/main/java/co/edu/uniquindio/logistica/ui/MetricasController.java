package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.service.MetricsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller para el panel de métricas operativas
 * RF-013, RF-014: Panel de métricas con visualización con JavaFX Charts
 */
public class MetricasController {

    @FXML private LineChart<String, Number> tiemposChart;
    @FXML private BarChart<String, Number> serviciosChart;
    @FXML private PieChart serviciosPieChart;
    @FXML private BarChart<String, Number> ingresosChart;
    @FXML private BarChart<String, Number> incidenciasChart;
    @FXML private VBox metricasContainer;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private Label tiempoPromedioLabel;
    @FXML private Label ingresosTotalesLabel;
    @FXML private Label totalEnviosLabel;
    @FXML private Label totalIncidenciasLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private final MetricsService metricsService = new MetricsService();

    @FXML
    private void initialize() {
        // Establecer fechas por defecto (último mes)
        fechaInicio.setValue(LocalDate.now().minusMonths(1));
        fechaFin.setValue(LocalDate.now());

        // Inicializar gráficos con ejes visibles
        if (tiemposChart != null) {
            tiemposChart.setAnimated(false);
            tiemposChart.setLegendVisible(true);
        }
        if (serviciosChart != null) {
            serviciosChart.setAnimated(false);
            serviciosChart.setLegendVisible(true);
        }
        if (serviciosPieChart != null) {
            serviciosPieChart.setAnimated(false);
            serviciosPieChart.setLegendVisible(true);
        }
        if (ingresosChart != null) {
            ingresosChart.setAnimated(false);
            ingresosChart.setLegendVisible(true);
        }
        if (incidenciasChart != null) {
            incidenciasChart.setAnimated(false);
            incidenciasChart.setLegendVisible(true);
        }

        // Cargar métricas iniciales
        cargarMetricas();
    }

    @FXML
    private void handleActualizar() {
        cargarMetricas();
    }

    private void cargarMetricas() {
        try {
            LocalDate inicio = fechaInicio.getValue() != null ? fechaInicio.getValue() : LocalDate.now().minusMonths(1);
            LocalDate fin = fechaFin.getValue() != null ? fechaFin.getValue() : LocalDate.now();

            // Limpiar gráficos anteriores
            tiemposChart.getData().clear();
            serviciosChart.getData().clear();
            serviciosPieChart.getData().clear();
            ingresosChart.getData().clear();
            incidenciasChart.getData().clear();

            // 1. Tiempos promedio por zona (Line Chart)
            cargarTiemposPromedioPorZona();

            // 2. Servicios adicionales más usados (Bar Chart)
            cargarServiciosAdicionales();

            // 3. Servicios adicionales (Pie Chart)
            cargarServiciosPieChart();

            // 4. Ingresos por período (Bar Chart)
            cargarIngresosPorPeriodo(inicio, fin);

            // 5. Incidencias por zona (Bar Chart)
            cargarIncidenciasPorZona();

            // 6. Métricas generales
            actualizarMetricasGenerales(inicio, fin);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar métricas: " + e.getMessage());
        }
    }

    private void cargarTiemposPromedioPorZona() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiempo Promedio (días)");

        Map<String, Double> tiempos = metricsService.calcularTiemposPromedioPorZona();
        
        if (tiempos.isEmpty()) {
            // Datos de ejemplo si no hay datos reales
            series.getData().add(new XYChart.Data<>("Norte", 2.5));
            series.getData().add(new XYChart.Data<>("Centro", 1.8));
            series.getData().add(new XYChart.Data<>("Sur", 3.2));
        } else {
            for (Map.Entry<String, Double> entry : tiempos.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }

        if (!series.getData().isEmpty()) {
            tiemposChart.getData().add(series);
        }
        tiemposChart.setTitle("Tiempos Promedio de Entrega por Zona");
        tiemposChart.setLegendVisible(true);
    }

    private void cargarServiciosAdicionales() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cantidad de Usos");

        Map<String, Long> servicios = metricsService.contarServiciosAdicionales();
        
        // Siempre mostrar todos los servicios, incluso si son 0
        if (servicios.isEmpty()) {
            servicios.put("Prioridad", 0L);
            servicios.put("Seguro", 0L);
            servicios.put("Fragil", 0L);
            servicios.put("Firma Requerida", 0L);
        }
        
        for (Map.Entry<String, Long> entry : servicios.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        if (!series.getData().isEmpty()) {
            serviciosChart.getData().add(series);
        }
        serviciosChart.setTitle("Servicios Adicionales Más Usados");
        serviciosChart.setLegendVisible(true);
    }

    private void cargarServiciosPieChart() {
        Map<String, Long> servicios = metricsService.contarServiciosAdicionales();
        
        // Siempre mostrar todos los servicios, incluso si son 0
        if (servicios.isEmpty()) {
            servicios.put("Prioridad", 0L);
            servicios.put("Seguro", 0L);
            servicios.put("Fragil", 0L);
            servicios.put("Firma Requerida", 0L);
        }
        
        for (Map.Entry<String, Long> entry : servicios.entrySet()) {
            if (entry.getValue() > 0) { // Solo mostrar servicios con uso
                serviciosPieChart.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            }
        }
        
        // Si no hay servicios con uso, mostrar un mensaje
        if (serviciosPieChart.getData().isEmpty()) {
            serviciosPieChart.getData().add(new PieChart.Data("Sin servicios adicionales", 1.0));
        }
        
        serviciosPieChart.setTitle("Distribución de Servicios Adicionales");
        serviciosPieChart.setLegendVisible(true);
    }

    private void cargarIngresosPorPeriodo(LocalDate inicio, LocalDate fin) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos (COP)");

        Map<String, Double> ingresosPorServicio = metricsService.calcularIngresosPorServicio(inicio, fin);
        
        if (!ingresosPorServicio.isEmpty()) {
            for (Map.Entry<String, Double> entry : ingresosPorServicio.entrySet()) {
                if (entry.getValue() > 0) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
        }
        
        // Si no hay ingresos por servicio, mostrar ingresos totales del período
        if (series.getData().isEmpty()) {
            double ingresosTotal = metricsService.calcularIngresosPorPeriodo(inicio, fin);
            if (ingresosTotal > 0) {
                series.getData().add(new XYChart.Data<>("Ingresos Totales", ingresosTotal));
            } else {
                // Mostrar ingresos totales de todos los pagos como alternativa
                double ingresosTodos = metricsService.calcularIngresosTotales();
                if (ingresosTodos > 0) {
                    series.getData().add(new XYChart.Data<>("Ingresos Totales", ingresosTodos));
                } else {
                    series.getData().add(new XYChart.Data<>("Sin ingresos", 0.0));
                }
            }
        }

        if (!series.getData().isEmpty()) {
            ingresosChart.getData().add(series);
        }
        ingresosChart.setTitle("Ingresos por Servicio Adicional (" + inicio + " - " + fin + ")");
        ingresosChart.setLegendVisible(true);
    }

    private void cargarIncidenciasPorZona() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cantidad de Incidencias");

        Map<String, Long> incidencias = metricsService.contarIncidenciasPorZona();
        
        // Siempre mostrar todas las zonas, incluso si son 0
        if (incidencias.isEmpty()) {
            incidencias.put("Norte", 0L);
            incidencias.put("Centro", 0L);
            incidencias.put("Sur", 0L);
        }
        
        for (Map.Entry<String, Long> entry : incidencias.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        if (!series.getData().isEmpty()) {
            incidenciasChart.getData().add(series);
        }
        incidenciasChart.setTitle("Incidencias por Zona");
        incidenciasChart.setLegendVisible(true);
    }

    private void actualizarMetricasGenerales(LocalDate inicio, LocalDate fin) {
        // Tiempo promedio de entrega
        double tiempoPromedio = metricsService.calcularTiempoPromedioEntrega();
        if (tiempoPromedio > 0) {
            tiempoPromedioLabel.setText(String.format("%.2f días", tiempoPromedio));
        } else {
            tiempoPromedioLabel.setText("N/A");
        }

        // Ingresos totales
        double ingresosTotales = metricsService.calcularIngresosTotales();
        if (ingresosTotales > 0) {
            ingresosTotalesLabel.setText(String.format("$%,.2f COP", ingresosTotales));
        } else {
            ingresosTotalesLabel.setText("$0.00 COP");
        }

        // Total de envíos
        long totalEnvios = facade.listarTodosEnvios().size();
        totalEnviosLabel.setText(String.valueOf(totalEnvios));

        // Total de incidencias
        long totalIncidencias = facade.listarTodosEnvios().stream()
                .filter(e -> e.getEstado() == EnvioDTO.EstadoEnvio.INCIDENCIA)
                .count();
        totalIncidenciasLabel.setText(String.valueOf(totalIncidencias));
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
            mostrarAlerta("Error", "No se pudo volver al panel de administración.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}


