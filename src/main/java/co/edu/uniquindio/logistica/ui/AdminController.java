package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
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

    private UsuarioDTO usuario;

    private LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
        Sesion.setUsuarioActual(usuario);
    }

    // ---------------- BOTONES NUEVOS ----------------

    @FXML
    private void handleVerUsuarios(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usuarios.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Pagos");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de Pagoss", "red");
        }
    }

    @FXML
    private void handleVerReportes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Lista Completa de Administradores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    @FXML
    private void handleVerMetricas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/metricas.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Métricas Operativas");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir el panel de métricas", "red");
        }
    }

    @FXML
    private void handleVerTarifas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tarifas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Tarifas");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir las tarifas", "red");
        }
    }

    @FXML
    private void handleVerTodosLosEnvios(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historial_envios_admin.fxml"));
            Parent root = loader.load();

            HistorialEnviosAdminController controller = loader.getController();
            controller.setUsuario(null); // null = ver todos los envíos

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Todos los Envíos del Sistema");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir el historial de envíos", "red");
        }
    }

    @FXML
    private void handleRastrearEnvio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rastreo.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Rastrear Envío");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir rastreo de envío", "red");
        }
    }

    @FXML
    private void handleVerPanelUsuario(ActionEvent event) {
        try {
            // Obtener el usuario actual de la sesión
            UsuarioDTO usuarioActual = usuario != null ? usuario : Sesion.getUsuarioActual();
            
            if (usuarioActual == null) {
                mostrarAlerta("Error", "No hay usuario en sesión. Por favor, inicia sesión nuevamente.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_admin.fxml"));
            Parent root = loader.load();

            UserAdminController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Usuario - " + usuarioActual.getNombre());
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el panel de usuario.");
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
