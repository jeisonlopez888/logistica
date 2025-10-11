package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UserAdminController {


    private Usuario usuario;
    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void handleCrearEnvio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_envio.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            CrearEnvioController ctrl = loader.getController();
            // ✅ Corregido: el método ahora es setUsuarioActual()
            ctrl.setUsuarioActual(usuario);

            // ✅ Si tienes una callback en CrearEnvioController (opcional)
            ctrl.setOnEnvioCreado(() -> {
                mostrarAlerta("Éxito", "El envío se registró correctamente.");
            });


            stage.setScene(new Scene(root));
            stage.setTitle("Crear Envío");
            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial_envios.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            HistorialEnviosController ctrl = loader.getController();
            ctrl.setUsuario(usuario);

            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Envíos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
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
            mostrarAlerta("Error", "No se pudo volver al login.");
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
