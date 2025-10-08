package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.EnvioBuilder;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CrearEnvioController {

    @FXML private TextField origenDireccionField;
    @FXML private TextField origenCiudadField;
    @FXML private TextField destinoDireccionField;
    @FXML private TextField destinoCiudadField;
    @FXML private TextField pesoField;
    @FXML private Label mensajeLabel;
    @FXML private Button crearBtn;
    @FXML private Button cancelarBtn;

    private Usuario usuario;
    private Runnable onEnvioCreado;
    private Envio envioEdit;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setOnEnvioCreado(Runnable callback) {
        this.onEnvioCreado = callback;
    }

    public void setEnvioToEdit(Envio envio) {
        this.envioEdit = envio;
        if (envio != null) {
            origenDireccionField.setText(envio.getOrigen().getCalle());
            origenCiudadField.setText(envio.getOrigen().getCiudad());
            destinoDireccionField.setText(envio.getDestino().getCalle());
            destinoCiudadField.setText(envio.getDestino().getCiudad());
            pesoField.setText(String.valueOf(envio.getPeso()));
            crearBtn.setText("Actualizar Envío");
        }
    }

    @FXML
    private void handleCrear() {
        try {
            double peso = Double.parseDouble(pesoField.getText().trim());

            String origenDir = origenDireccionField.getText().trim();
            String origenCiudad = origenCiudadField.getText().trim();
            String destinoDir = destinoDireccionField.getText().trim();
            String destinoCiudad = destinoCiudadField.getText().trim();

            if (origenDir.isEmpty() || origenCiudad.isEmpty() ||
                    destinoDir.isEmpty() || destinoCiudad.isEmpty()) {
                mostrarMensaje("❌ Todos los campos son obligatorios", "red");
                return;
            }

            Direccion origen = new Direccion(facade.generarId(), "Origen", origenDir, origenCiudad, "0,0");
            Direccion destino = new Direccion(facade.generarId(), "Destino", destinoDir, destinoCiudad, "0,0");

            if (envioEdit != null) {
                envioEdit.setOrigen(origen);
                envioEdit.setDestino(destino);
                envioEdit.setPeso(peso);
                mostrarMensaje("✅ Envío actualizado correctamente", "green");
            } else {
                Usuario usuarioActual = (usuario != null) ? usuario : Sesion.getUsuarioActual();

                Envio envio = new EnvioBuilder()
                        .usuario(usuarioActual)
                        .origen(origen)
                        .destino(destino)
                        .peso(peso)
                        .build();

                facade.registrarEnvio(envio);
                mostrarMensaje("✅ Envío creado con ID: " + envio.getId(), "green");
            }

            if (onEnvioCreado != null) onEnvioCreado.run();
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ El peso debe ser un número válido", "red");
        }
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) crearBtn.getScene().getWindow();
        stage.close();
    }
}
