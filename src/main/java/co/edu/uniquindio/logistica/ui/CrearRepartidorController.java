package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.RepartidorDTO;
import co.edu.uniquindio.logistica.store.DataStore;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador de Crear Repartidor - Solo valida y usa DTOs
 */
public class CrearRepartidorController {

    @FXML private TextField idField;
    @FXML private TextField documentoField;
    @FXML private TextField nombreField;
    @FXML private TextField telefonoField;
    @FXML private TextField zonaField;
    @FXML private CheckBox disponibleCheck;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private Runnable onRepartidorCreado;
    private RepartidorDTO repartidorEditandoDTO;

    public void setOnRepartidorCreado(Runnable onRepartidorCreado) {
        this.onRepartidorCreado = onRepartidorCreado;
    }

    public void setRepartidor(RepartidorDTO rDTO) {
        this.repartidorEditandoDTO = rDTO;
        idField.setText(String.valueOf(rDTO.getId()));
        documentoField.setText(rDTO.getDocumento() != null ? rDTO.getDocumento() : "");
        nombreField.setText(rDTO.getNombre());
        telefonoField.setText(rDTO.getTelefono());
        zonaField.setText(rDTO.getZona());
        disponibleCheck.setSelected(rDTO.isDisponible());

        idField.setDisable(true);
        documentoField.setDisable(true);
    }

    @FXML
    private void handleGuardar() {
        try {
            String documento = documentoField.getText();
            String nombre = nombreField.getText();
            String telefono = telefonoField.getText();
            String zona = zonaField.getText();
            boolean disponible = disponibleCheck.isSelected();

            // Validación
            if (ValidacionUtil.isEmpty(nombre) || ValidacionUtil.isEmpty(telefono) || ValidacionUtil.isEmpty(zona)) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            if (!ValidacionUtil.esTelefonoValido(telefono)) {
                mostrarMensaje("❌ Teléfono inválido (debe tener 10 dígitos)", "red");
                return;
            }

            if (repartidorEditandoDTO == null) {
                // Validar documento si está presente
                if (!ValidacionUtil.isEmpty(documento) && !documento.matches("\\d+")) {
                    mostrarMensaje("❌ El documento debe ser numérico", "red");
                    return;
                }

                Long id = DataStore.getInstance().nextId();
                RepartidorDTO nuevoDTO = new RepartidorDTO();
                nuevoDTO.setId(id);
                nuevoDTO.setDocumento(documento);
                nuevoDTO.setNombre(nombre);
                nuevoDTO.setTelefono(telefono);
                nuevoDTO.setZona(zona);
                nuevoDTO.setDisponible(disponible);

                facade.registrarRepartidor(nuevoDTO);
                mostrarMensaje("✅ Repartidor registrado correctamente", "green");
            } else {
                // Editar datos
                RepartidorDTO actualizadoDTO = new RepartidorDTO();
                actualizadoDTO.setId(repartidorEditandoDTO.getId());
                actualizadoDTO.setDocumento(repartidorEditandoDTO.getDocumento());
                actualizadoDTO.setNombre(nombre);
                actualizadoDTO.setTelefono(telefono);
                actualizadoDTO.setZona(zona);
                actualizadoDTO.setDisponible(disponible);

                facade.eliminarRepartidor(repartidorEditandoDTO.getId());
                facade.registrarRepartidor(actualizadoDTO);
                mostrarMensaje("✅ Repartidor actualizado correctamente", "green");
            }

            if (onRepartidorCreado != null) onRepartidorCreado.run();

            Stage stage = (Stage) mensajeLabel.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            mostrarMensaje("⚠️ Error al guardar repartidor", "red");
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
