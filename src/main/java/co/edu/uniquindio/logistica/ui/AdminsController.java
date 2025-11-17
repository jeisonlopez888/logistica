package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Controlador de Administradores - Solo valida y usa DTOs
 */
public class AdminsController {

    @FXML private Label mensajeLabel;
    @FXML private TableView<UsuarioDTO> usuarioTableView;
    @FXML private TableColumn<UsuarioDTO, Long> idCol;
    @FXML private TableColumn<UsuarioDTO, String> nombreCol;
    @FXML private TableColumn<UsuarioDTO, String> emailCol;
    @FXML private TableColumn<UsuarioDTO, String> telefonoCol;
    @FXML private TableColumn<UsuarioDTO, String> passwordCol;
    @FXML private TableColumn<UsuarioDTO, String> rolCol;
    @FXML private TableColumn<UsuarioDTO, String> direccionesCol;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private final ObservableList<UsuarioDTO> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Usar lambdas en lugar de PropertyValueFactory para evitar problemas de módulos
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        nombreCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        emailCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        telefonoCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefono()));
        passwordCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPassword()));
        rolCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAdmin() ? "Administrador" : "Usuario"));
        direccionesCol.setCellValueFactory(cellData -> {
            UsuarioDTO u = cellData.getValue();
            String direcciones = u.getDirecciones().stream()
                    .map(d -> d.getAlias() + " (" + d.getCoordenadas() + ")")
                    .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(direcciones.isEmpty() ? "Sin direcciones" : direcciones);
        });
        cargarUsuarios();
    }

    /** Cargar solo usuarios administradores usando DTOs */
    private void cargarUsuarios() {
        listaUsuarios.clear();
        listaUsuarios.addAll(facade.listarUsuarios().stream()
                .filter(UsuarioDTO::isAdmin)
                .toList());
        usuarioTableView.setItems(listaUsuarios);
    }

    /** Mostrar mensajes */
    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    /** Editar usuario seleccionado */
    @FXML
    private void handleEditarUsuario() {
        UsuarioDTO seleccionado = usuarioTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un usuario para editar", "orange");
            return;
        }
        mostrarMensaje("✏️ Función de edición en desarrollo", "blue");
    }

    /** Eliminar usuario seleccionado usando DTOs */
    @FXML
    private void handleEliminarUsuario(ActionEvent event) {
        try {
            UsuarioDTO seleccionado = usuarioTableView.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAlerta("Aviso", "⚠️ Debes seleccionar un usuario para eliminar.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Seguro que deseas eliminar al usuario '" + seleccionado.getNombre() + "'?");
            var resultado = confirm.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                facade.eliminarUsuario(seleccionado.getId());
                usuarioTableView.getItems().remove(seleccionado);
                mostrarAlerta("Éxito", "✅ Usuario eliminado con éxito.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "❌ No se pudo eliminar el usuario.");
        }
    }

    /** Ir al panel de usuario */
    @FXML
    private void handleVerUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la lista de usuarios", "red");
        }
    }

    /** Volver al panel de administración */
    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel de Administración");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al volver al panel de administración", "red");
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
