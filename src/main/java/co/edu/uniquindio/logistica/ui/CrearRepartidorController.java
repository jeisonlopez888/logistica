package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Repartidor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CrearRepartidorController {

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField telefonoField;
    @FXML private TextField zonaField;
    @FXML private CheckBox disponibleCheck;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private Runnable onRepartidorCreado;
    private Repartidor repartidorEditando;

    public void setOnRepartidorCreado(Runnable onRepartidorCreado) {
        this.onRepartidorCreado = onRepartidorCreado;
    }

    public void setRepartidor(Repartidor r) {
        this.repartidorEditando = r;
        idField.setText(String.valueOf(r.getId()));
        nombreField.setText(r.getNombre());
        telefonoField.setText(r.getTelefono());
        zonaField.setText(r.getZona());
        disponibleCheck.setSelected(r.isDisponible());

        idField.setDisable(true); // No se permite cambiar la cédula
    }

    @FXML
    private void handleGuardar() {
        try {
            String nombre = nombreField.getText();
            String telefono = telefonoField.getText();
            String zona = zonaField.getText();
            boolean disponible = disponibleCheck.isSelected();

            if (nombre.isEmpty() || telefono.isEmpty() || zona.isEmpty()) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            if (repartidorEditando == null) {
                Long cedula = Long.parseLong(idField.getText());

                boolean existe = facade.listarRepartidores().stream()
                        .anyMatch(r -> r.getId().equals(cedula));
                if (existe) {
                    mostrarMensaje("⚠️ Ya existe un repartidor con esa cédula", "orange");
                    return;
                }

                Repartidor nuevo = new Repartidor(cedula, nombre, telefono, zona, disponible);
                facade.registrarRepartidor(nuevo);
                mostrarMensaje("✅ Repartidor registrado correctamente", "green");
            } else {
                // Editar datos
                facade.eliminarRepartidor(repartidorEditando);
                Repartidor actualizado = new Repartidor(repartidorEditando.getId(), nombre, telefono, zona, disponible);
                facade.registrarRepartidor(actualizado);
                mostrarMensaje("✅ Repartidor actualizado correctamente", "green");
            }

            if (onRepartidorCreado != null) onRepartidorCreado.run();

            Stage stage = (Stage) mensajeLabel.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ La cédula debe ser numérica", "red");
        } catch (Exception e) {
            mostrarMensaje("⚠️ Error al guardar repartidor", "red");
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill:" + color + ";");
    }
}
