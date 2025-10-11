package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private Label mensajeLabel;

    private LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
    }

    // ---------------- BOTONES NUEVOS ----------------

    @FXML
    private void handleVerUsuarios(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usuarios.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer nueva escena con la vista usuarios
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Usuarios");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }



    @FXML
    private void handleVerAdministradores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admins.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer nueva escena con la vista usuarios
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Administradores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    @FXML
    private void handleVerRepartidores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/repartidores.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer nueva escena con la vista usuarios
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Administradores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    @FXML
    private void handleVerPagos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pagos.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer nueva escena con la vista usuarios
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Administradores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    @FXML
    private void handleVerReportes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer nueva escena con la vista usuarios
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Administradores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    @FXML
    private void handleVerTarifas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tarifas.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage) desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer nueva escena con la vista usuarios
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Administradores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al login.");
        }
    }

    @FXML
    private void handleVerUserAdmin(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/user_admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo ver user.");
        }
    }

    // ---------------- UTIL ----------------
    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
