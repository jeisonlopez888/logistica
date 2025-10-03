package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.EnvioBuilder;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CrearEnvioController {

    @FXML private TextField origenField;
    @FXML private TextField destinoField;
    @FXML private TextField pesoField;
    @FXML private Label mensajeLabel;
    @FXML private Button crearBtn;
    @FXML private Button cancelarBtn;

    private Usuario usuario;
    private Runnable onEnvioCreado;

    private Envio envioEdit; // nuevo: referencia al envío a editar

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
            origenField.setText(envio.getOrigen().getCalle());   // usamos calle
            destinoField.setText(envio.getDestino().getCalle()); // usamos calle
            pesoField.setText(String.valueOf(envio.getPeso()));
            crearBtn.setText("Actualizar Envío"); // cambia el botón
        }
    }



    @FXML
    private void handleCrear() {
        try {
            double peso = Double.parseDouble(pesoField.getText());

            Direccion origen = new Direccion(facade.generarId(), "Origen", origenField.getText(), "Armenia", "0,0");
            Direccion destino = new Direccion(facade.generarId(), "Destino", destinoField.getText(), "Armenia", "0,0");

            if (envioEdit != null) {
                // actualizar el envío existente
                envioEdit.setOrigen(origen);
                envioEdit.setDestino(destino);
                envioEdit.setPeso(peso);

                mensajeLabel.setText("✅ Envío actualizado: " + envioEdit.getId());
                mensajeLabel.setStyle("-fx-text-fill: green;");
            } else {
                // crear uno nuevo
                Envio envio = new EnvioBuilder()
                        .usuario(usuario)
                        .origen(origen)
                        .destino(destino)
                        .peso(peso)
                        .build();

                facade.registrarEnvio(envio);

                mensajeLabel.setText("✅ Envío creado con ID: " + envio.getId());
                mensajeLabel.setStyle("-fx-text-fill: green;");
            }

            if (onEnvioCreado != null) {
                onEnvioCreado.run();
            }

            cerrarVentana();

        } catch (NumberFormatException e) {
            mensajeLabel.setText("❌ Peso inválido");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
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
