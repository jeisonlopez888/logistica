package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.EnvioDTO;
import co.edu.uniquindio.logistica.service.MetricasService;
import co.edu.uniquindio.logistica.util.ReportUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

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
    
    // Tablas
    @FXML private TableView<MetricaTiempo> tiemposTable;
    @FXML private TableColumn<MetricaTiempo, String> zonaTiempoCol;
    @FXML private TableColumn<MetricaTiempo, String> tiempoPromedioCol;
    
    @FXML private TableView<MetricaServicio> serviciosTable;
    @FXML private TableColumn<MetricaServicio, String> servicioNombreCol;
    @FXML private TableColumn<MetricaServicio, String> servicioCantidadCol;
    
    @FXML private TableView<MetricaIngreso> ingresosTable;
    @FXML private TableColumn<MetricaIngreso, String> ingresoNombreCol;
    @FXML private TableColumn<MetricaIngreso, String> ingresoMontoCol;
    
    @FXML private TableView<MetricaIncidencia> incidenciasTable;
    @FXML private TableColumn<MetricaIncidencia, String> incidenciaZonaCol;
    @FXML private TableColumn<MetricaIncidencia, String> incidenciaCantidadCol;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private final MetricasService metricasService = new MetricasService();

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

        // Configurar tablas
        configurarTablas();
        
        // Cargar métricas iniciales
        cargarMetricas();
    }
    
    private void configurarTablas() {
        // Tabla de Tiempos
        zonaTiempoCol.setCellValueFactory(new PropertyValueFactory<>("zona"));
        tiempoPromedioCol.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
        tiemposTable.setPlaceholder(new Label("No hay datos de tiempos disponibles"));
        
        // Tabla de Servicios
        servicioNombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        servicioCantidadCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        serviciosTable.setPlaceholder(new Label("No hay datos de servicios disponibles"));
        
        // Tabla de Ingresos
        ingresoNombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ingresoMontoCol.setCellValueFactory(new PropertyValueFactory<>("monto"));
        ingresosTable.setPlaceholder(new Label("No hay datos de ingresos disponibles"));
        
        // Tabla de Incidencias
        incidenciaZonaCol.setCellValueFactory(new PropertyValueFactory<>("zona"));
        incidenciaCantidadCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        incidenciasTable.setPlaceholder(new Label("No hay datos de incidencias disponibles"));
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
            
            // 7. Cargar tablas
            cargarTablas(inicio, fin);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar métricas: " + e.getMessage());
        }
    }

    private void cargarTiemposPromedioPorZona() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiempo Promedio (días)");

        Map<String, Double> tiempos = metricasService.calcularTiemposPromedioPorZona();
        
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

        tiemposChart.getData().add(series);
        tiemposChart.setTitle("Tiempos Promedio de Entrega por Zona");
        tiemposChart.setLegendVisible(true);
        tiemposChart.setAnimated(false);
    }

    private void cargarServiciosAdicionales() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cantidad de Usos");

        Map<String, Long> servicios = metricasService.contarServiciosAdicionales();
        
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

        serviciosChart.getData().add(series);
        serviciosChart.setTitle("Servicios Adicionales Más Usados");
        serviciosChart.setLegendVisible(true);
        serviciosChart.setAnimated(false);
    }

    private void cargarServiciosPieChart() {
        Map<String, Long> servicios = metricasService.contarServiciosAdicionales();
        
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

        Map<String, Double> ingresosPorServicio = metricasService.calcularIngresosPorServicio(inicio, fin);
        
        if (!ingresosPorServicio.isEmpty()) {
            for (Map.Entry<String, Double> entry : ingresosPorServicio.entrySet()) {
                if (entry.getValue() > 0) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
        }
        
        // Si no hay ingresos por servicio, mostrar ingresos totales del período
        if (series.getData().isEmpty()) {
            double ingresosTotal = metricasService.calcularIngresosPorPeriodo(inicio, fin);
            if (ingresosTotal > 0) {
                series.getData().add(new XYChart.Data<>("Ingresos Totales", ingresosTotal));
            } else {
                // Mostrar ingresos totales de todos los pagos como alternativa
                double ingresosTodos = metricasService.calcularIngresosTotales();
                if (ingresosTodos > 0) {
                    series.getData().add(new XYChart.Data<>("Ingresos Totales", ingresosTodos));
                } else {
                    series.getData().add(new XYChart.Data<>("Sin ingresos", 0.0));
                }
            }
        }

        ingresosChart.getData().add(series);
        ingresosChart.setTitle("Ingresos por Servicio Adicional (" + inicio + " - " + fin + ")");
        ingresosChart.setLegendVisible(true);
        ingresosChart.setAnimated(false);
    }

    private void cargarIncidenciasPorZona() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cantidad de Incidencias");

        Map<String, Long> incidencias = metricasService.contarIncidenciasPorZona();
        
        // Siempre mostrar todas las zonas, incluso si son 0
        if (incidencias.isEmpty()) {
            incidencias.put("Norte", 0L);
            incidencias.put("Centro", 0L);
            incidencias.put("Sur", 0L);
        }
        
        for (Map.Entry<String, Long> entry : incidencias.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        incidenciasChart.getData().add(series);
        incidenciasChart.setTitle("Incidencias por Zona");
        incidenciasChart.setLegendVisible(true);
        incidenciasChart.setAnimated(false);
    }

    private void actualizarMetricasGenerales(LocalDate inicio, LocalDate fin) {
        // Tiempo promedio de entrega
        double tiempoPromedio = metricasService.calcularTiempoPromedioEntrega();
        if (tiempoPromedio > 0) {
            tiempoPromedioLabel.setText(String.format("%.2f días", tiempoPromedio));
        } else {
            tiempoPromedioLabel.setText("N/A");
        }

        // Ingresos totales
        double ingresosTotales = metricasService.calcularIngresosTotales();
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
    
    private void cargarTablas(LocalDate inicio, LocalDate fin) {
        // Tabla de Tiempos por Zona
        ObservableList<MetricaTiempo> tiemposData = FXCollections.observableArrayList();
        Map<String, Double> tiempos = metricasService.calcularTiemposPromedioPorZona();
        if (tiempos.isEmpty()) {
            tiemposData.add(new MetricaTiempo("Norte", "2.5 días"));
            tiemposData.add(new MetricaTiempo("Centro", "1.8 días"));
            tiemposData.add(new MetricaTiempo("Sur", "3.2 días"));
        } else {
            for (Map.Entry<String, Double> entry : tiempos.entrySet()) {
                tiemposData.add(new MetricaTiempo(entry.getKey(), String.format("%.2f días", entry.getValue())));
            }
        }
        tiemposTable.setItems(tiemposData);
        
        // Tabla de Servicios Adicionales
        ObservableList<MetricaServicio> serviciosData = FXCollections.observableArrayList();
        Map<String, Long> servicios = metricasService.contarServiciosAdicionales();
        if (servicios.isEmpty()) {
            servicios.put("Prioridad", 0L);
            servicios.put("Seguro", 0L);
            servicios.put("Fragil", 0L);
            servicios.put("Firma Requerida", 0L);
        }
        for (Map.Entry<String, Long> entry : servicios.entrySet()) {
            serviciosData.add(new MetricaServicio(entry.getKey(), String.valueOf(entry.getValue())));
        }
        serviciosTable.setItems(serviciosData);
        
        // Tabla de Ingresos por Servicio
        ObservableList<MetricaIngreso> ingresosData = FXCollections.observableArrayList();
        Map<String, Double> ingresosPorServicio = metricasService.calcularIngresosPorServicio(inicio, fin);
        if (!ingresosPorServicio.isEmpty()) {
            for (Map.Entry<String, Double> entry : ingresosPorServicio.entrySet()) {
                if (entry.getValue() > 0) {
                    ingresosData.add(new MetricaIngreso(entry.getKey(), String.format("$%,.2f COP", entry.getValue())));
                }
            }
        }
        if (ingresosData.isEmpty()) {
            double ingresosTotal = metricasService.calcularIngresosPorPeriodo(inicio, fin);
            if (ingresosTotal > 0) {
                ingresosData.add(new MetricaIngreso("Ingresos Totales", String.format("$%,.2f COP", ingresosTotal)));
            } else {
                double ingresosTodos = metricasService.calcularIngresosTotales();
                if (ingresosTodos > 0) {
                    ingresosData.add(new MetricaIngreso("Ingresos Totales", String.format("$%,.2f COP", ingresosTodos)));
                } else {
                    ingresosData.add(new MetricaIngreso("Sin ingresos", "$0.00 COP"));
                }
            }
        }
        ingresosTable.setItems(ingresosData);
        
        // Tabla de Incidencias por Zona
        ObservableList<MetricaIncidencia> incidenciasData = FXCollections.observableArrayList();
        Map<String, Long> incidencias = metricasService.contarIncidenciasPorZona();
        if (incidencias.isEmpty()) {
            incidencias.put("Norte", 0L);
            incidencias.put("Centro", 0L);
            incidencias.put("Sur", 0L);
        }
        for (Map.Entry<String, Long> entry : incidencias.entrySet()) {
            incidenciasData.add(new MetricaIncidencia(entry.getKey(), String.valueOf(entry.getValue())));
        }
        incidenciasTable.setItems(incidenciasData);
    }

    @FXML
    private void handleImprimirMetricas() {
        try {
            // Preguntar formato
            Alert formatoAlert = new Alert(Alert.AlertType.CONFIRMATION);
            formatoAlert.setTitle("Formato de Exportación");
            formatoAlert.setHeaderText("Seleccione el formato de exportación");
            formatoAlert.setContentText("¿En qué formato desea exportar las métricas?");
            
            ButtonType excelButton = new ButtonType("Excel");
            ButtonType pdfButton = new ButtonType("PDF");
            ButtonType cancelButton = new ButtonType("Cancelar");
            formatoAlert.getButtonTypes().setAll(excelButton, pdfButton, cancelButton);
            
            Optional<ButtonType> resultado = formatoAlert.showAndWait();
            if (resultado.isEmpty() || resultado.get() == cancelButton) {
                return;
            }
            
            boolean esExcel = resultado.get() == excelButton;
            
            // Seleccionar carpeta
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Seleccionar carpeta para guardar el reporte de métricas");
            File carpeta = chooser.showDialog(new Stage());
            if (carpeta == null) {
                mostrarAlerta("Cancelado", "No se seleccionó ninguna carpeta.");
                return;
            }
            
            // Obtener datos actuales
            LocalDate inicio = fechaInicio.getValue() != null ? fechaInicio.getValue() : LocalDate.now().minusMonths(1);
            LocalDate fin = fechaFin.getValue() != null ? fechaFin.getValue() : LocalDate.now();
            
            Map<String, Double> tiemposPorZona = metricasService.calcularTiemposPromedioPorZona();
            Map<String, Long> serviciosAdicionales = metricasService.contarServiciosAdicionales();
            Map<String, Double> ingresosPorServicio = metricasService.calcularIngresosPorServicio(inicio, fin);
            Map<String, Long> incidenciasPorZona = metricasService.contarIncidenciasPorZona();
            double tiempoPromedio = metricasService.calcularTiempoPromedioEntrega();
            double ingresosTotales = metricasService.calcularIngresosTotales();
            long totalEnvios = facade.listarTodosEnvios().size();
            long totalIncidencias = facade.listarTodosEnvios().stream()
                    .filter(e -> e.getEstado() == EnvioDTO.EstadoEnvio.INCIDENCIA)
                    .count();
            
            // Generar nombre de archivo
            String nombreArchivo = ReportUtil.agregarFechaNombre("metricas_operativas", esExcel);
            String ruta = carpeta.getAbsolutePath() + "/" + nombreArchivo;
            
            // Exportar
            if (esExcel) {
                ReportUtil.exportarMetricasExcel(
                        tiemposPorZona,
                        serviciosAdicionales,
                        ingresosPorServicio,
                        incidenciasPorZona,
                        tiempoPromedio,
                        ingresosTotales,
                        totalEnvios,
                        totalIncidencias,
                        ruta
                );
            } else {
                ReportUtil.exportarMetricasPDF(
                        tiemposPorZona,
                        serviciosAdicionales,
                        ingresosPorServicio,
                        incidenciasPorZona,
                        tiempoPromedio,
                        ingresosTotales,
                        totalEnvios,
                        totalIncidencias,
                        ruta
                );
            }
            
            mostrarAlerta("Éxito", "✅ Reporte de métricas generado correctamente:\n" + ruta);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "❌ Error al generar el reporte: " + e.getMessage());
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
    
    // Clases de datos para las tablas
    public static class MetricaTiempo {
        private final String zona;
        private final String tiempo;
        
        public MetricaTiempo(String zona, String tiempo) {
            this.zona = zona;
            this.tiempo = tiempo;
        }
        
        public String getZona() { return zona; }
        public String getTiempo() { return tiempo; }
    }
    
    public static class MetricaServicio {
        private final String nombre;
        private final String cantidad;
        
        public MetricaServicio(String nombre, String cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
        }
        
        public String getNombre() { return nombre; }
        public String getCantidad() { return cantidad; }
    }
    
    public static class MetricaIngreso {
        private final String nombre;
        private final String monto;
        
        public MetricaIngreso(String nombre, String monto) {
            this.nombre = nombre;
            this.monto = monto;
        }
        
        public String getNombre() { return nombre; }
        public String getMonto() { return monto; }
    }
    
    public static class MetricaIncidencia {
        private final String zona;
        private final String cantidad;
        
        public MetricaIncidencia(String zona, String cantidad) {
            this.zona = zona;
            this.cantidad = cantidad;
        }
        
        public String getZona() { return zona; }
        public String getCantidad() { return cantidad; }
    }
}


