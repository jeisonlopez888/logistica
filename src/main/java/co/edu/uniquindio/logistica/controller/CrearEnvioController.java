package co.edu.uniquindio.logistica.controller;


import co.edu.uniquindio.logistica.model.*;
import co.edu.uniquindio.logistica.store.DataStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CrearEnvioController {

    @FXML
    private TextField origenField;

    @FXML
    private TextField destinoField;

    @FXML
    private TextField pesoField;

    @FXML
    private Label mensajeLabel;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void handleCrearEnvio() {
        try {
            Direccion origen = new Direccion(
                    DataStore.getInstance().nextId(),
                    "Origen", origenField.getText(), "Armenia", "0,0"
            );
            Direccion destino = new Direccion(
                    DataStore.getInstance().nextId(),
                    "Destino", destinoField.getText(), "Armenia", "0,0"
            );

            double peso = Double.parseDouble(pesoField.getText());

            Envio envio = new EnvioBuilder()
                    .usuario(usuario)
                    .origen(origen)
                    .destino(destino)
                    .peso(peso)
                    .build();

            DataStore.getInstance().getEnvios().put(envio.getId(), envio);

            mensajeLabel.setText("✅ Envío creado con ID: " + envio.getId());
            mensajeLabel.setStyle("-fx-text-fill: green;");

        } catch (Exception e) {
            mensajeLabel.setText("❌ Error: " + e.getMessage());
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
