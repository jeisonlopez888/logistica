package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminsController {

    @FXML private TableView<Usuario> usuariosTable;
    @FXML private TableColumn<Usuario, Long> idCol;
    @FXML private TableColumn<Usuario, String> nombreCol;
    @FXML private TableColumn<Usuario, String> emailCol;
    @FXML private TableColumn<Usuario, String> telefonoCol;
    @FXML private TableColumn<Usuario, String> passwordCol;
    @FXML private TableColumn<Usuario, Boolean> adminCol;
    @FXML private Label mensajeLabel;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        nombreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        telefonoCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        passwordCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPassword()));
        adminCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().isAdmin()));

        cargarUsuarios();
    }

    /** 🔹 Cargar solo usuarios administradores */
    private void cargarUsuarios() {
        listaUsuarios.clear();
        listaUsuarios.addAll(facade.listarUsuarios().stream().filter(Usuario::isAdmin).toList());
        usuariosTable.setItems(listaUsuarios);
    }

    /** 🔹 Mostrar mensajes */
    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    /** 🔹 Crear usuario nuevo */
    @FXML
    private void handleCrearUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_usuario.fxml"));
            Parent root = loader.load();

            CrearUsuarioController controller = loader.getController();
            controller.setFacade(facade);
            controller.setOnUsuarioCreado(this::cargarUsuarios);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Usuario");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("⚠️ Error al abrir la ventana de creación", "red");
        }
    }

    /** 🔹 Editar usuario seleccionado */
    @FXML
    private void handleEditarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un usuario para editar", "orange");
            return;
        }
        mostrarMensaje("✏️ Función de edición en desarrollo", "blue");
    }

    /** 🔹 Eliminar usuario seleccionado */
    @FXML
    private void handleEliminarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un usuario para eliminar", "orange");
            return;
        }

    }

    /** 🔹 Ir al panel de usuario */
    @FXML
    private void handleUser() {
        mostrarMensaje("👤 Abriendo panel de usuario...", "#1976D2");
        // Aquí podrías cargar user.fxml si es necesario
    }

    /** 🔹 Volver al login */
    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesión");
            stage.show();

            // cerrar la ventana actual
            Stage current = (Stage) usuariosTable.getScene().getWindow();
            current.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al volver al Admin", "red");
        }
    }
}
