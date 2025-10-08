package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.util.ReportUtil;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdminController {

    @FXML private TableView<Usuario> usuariosTable;
    @FXML private TableColumn<Usuario, Long> idCol;
    @FXML private TableColumn<Usuario, String> nombreCol;
    @FXML private TableColumn<Usuario, String> emailCol;
    @FXML private TableColumn<Usuario, String> telefonoCol;
    @FXML private TableColumn<Usuario, String> passwordCol;
    @FXML private TableColumn<Usuario, Boolean> adminCol;
    @FXML private Label mensajeLabel;

    private LogisticaFacade facade = LogisticaFacade.getInstance();

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminCol.setCellValueFactory(new PropertyValueFactory<>("admin"));
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        usuariosTable.setItems(FXCollections.observableArrayList(facade.listarUsuarios()));
        usuariosTable.refresh();
    }

    @FXML
    private void handleEliminarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            facade.listarUsuarios().remove(seleccionado);
            cargarUsuarios();
            mostrarMensaje("✅ Usuario eliminado correctamente", "green");
        } else {
            mostrarMensaje("❌ Selecciona un usuario para eliminar", "red");
        }
    }

    @FXML
    private void handleEditarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("❌ Selecciona un usuario para editar", "red");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar_usuario.fxml"));
            Parent root = loader.load();

            EditarUsuarioController ctrl = loader.getController();
            ctrl.setUsuario(seleccionado);
            ctrl.setFacade(facade);
            ctrl.setOnUsuarioEditado(this::cargarUsuarios);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Usuario");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir el editor de usuario", "red");
        }
    }

    @FXML
    private void handleCrearUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_usuario.fxml"));
            Parent root = loader.load();

            CrearUsuarioController ctrl = loader.getController();
            ctrl.setFacade(facade);
            ctrl.setOnUsuarioCreado(this::cargarUsuarios);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Usuario");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la ventana de creación de usuario", "red");
        }
    }

    @FXML
    private void handleUser() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("❌ Selecciona un usuario para abrir su panel", "red");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
            Parent root = loader.load();

            UserController ctrl = loader.getController();
            ctrl.setUsuario(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Usuario - " + seleccionado.getNombre());
            stage.show();

            mostrarMensaje("✅ Panel abierto para " + seleccionado.getNombre(), "green");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir el panel de usuario", "red");
        }
    }

    @FXML
    private void handleVerPagos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pagos.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Pagos");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la gestión de pagos", "red");
        }
    }

    @FXML
    private void handleVerReportes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Reportes");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la gestión de reportes", "red");
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
