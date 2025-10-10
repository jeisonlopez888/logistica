package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DireccionFormController {

    @FXML private TextField aliasField;
    @FXML private TextField calleField;
    @FXML private TextField ciudadField;
    @FXML private TextField coordenadasField;
    @FXML private Label mensajeLabel;
    @FXML private Button guardarBtn;

    private Usuario usuario;
    private Direccion direccionToEdit;
    private Runnable onGuardado;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setDireccionToEdit(Direccion direccion) {
        this.direccionToEdit = direccion;
        if (direccion != null) {
            aliasField.setText(direccion.getAlias());
            calleField.setText(direccion.getCalle());
            ciudadField.setText(direccion.getCiudad());
            coordenadasField.setText(direccion.getCoordenadas());
            guardarBtn.setText("Actualizar");
        }
    }

    public void setOnGuardado(Runnable onGuardado) {
        this.onGuardado = onGuardado;
    }

    @FXML
    private void handleGuardar() {
        String alias = aliasField.getText().trim();
        String calle = calleField.getText().trim();
        String ciudad = ciudadField.getText().trim();
        String coord = coordenadasField.getText().trim();

        if (calle.isEmpty() || ciudad.isEmpty()) {
            mostrarMensaje("Calle y ciudad son obligatorias", "red");
            return;
        }

        try {
            if (direccionToEdit == null) {
                facade.crearDireccion(usuario, alias, calle, ciudad, coord.isEmpty() ? "0,0" : coord);
                mostrarMensaje("Dirección creada", "green");
            } else {
                facade.actualizarDireccion(usuario, direccionToEdit, alias, calle, ciudad, coord);
                mostrarMensaje("Dirección actualizada", "green");
            }

            if (onGuardado != null) onGuardado.run();
            cerrar();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("No se pudo guardar", "red");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) guardarBtn.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
