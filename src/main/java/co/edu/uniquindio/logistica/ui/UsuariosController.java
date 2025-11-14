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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.stream.Collectors;

/**
 * Controlador de Usuarios - Solo valida datos y se comunica con Facade usando DTOs
 */
public class UsuariosController {

    @FXML private Label mensajeLabel;
    @FXML private TableView<UsuarioDTO> tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, Long> idCol;
    @FXML private TableColumn<UsuarioDTO, String> nombreCol;
    @FXML private TableColumn<UsuarioDTO, String> emailCol;
    @FXML private TableColumn<UsuarioDTO, String> telefonoCol;
    @FXML private TableColumn<UsuarioDTO, String> passwordCol;
    @FXML private TableColumn<UsuarioDTO, String> rolCol;
    @FXML private TableColumn<UsuarioDTO, String> direccionesCol;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();
    private final ObservableList<UsuarioDTO> usuariosList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
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

    private void cargarUsuarios() {
        usuariosList.setAll(facade.listarUsuarios());
        tablaUsuarios.setItems(usuariosList);
    }

    @FXML
    private void handleEditarUsuario(ActionEvent event) {
        UsuarioDTO seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("⚠️ Selecciona un usuario para editar", "orange");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar_usuario_admin.fxml"));
            Parent root = loader.load();

            EditarUsuarioAdminController controller = loader.getController();
            controller.setUsuario(seleccionado); // Ya es UsuarioDTO
            controller.setOnUsuarioEditado(this::cargarUsuarios);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Editar Usuario");
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ Error al abrir la ventana de edición", "red");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("❌ No se pudo volver al panel admin", "red");
        }
    }

    @FXML
    private void handleCrearUsuario(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/crear_usuario_admin.fxml"));
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Usuario");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo crear usuario.");
        }
    }

    @FXML
    private void handleEliminarUsuario(ActionEvent event) {
        try {
            UsuarioDTO seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

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
                tablaUsuarios.getItems().remove(seleccionado);
                mostrarAlerta("Éxito", "✅ Usuario eliminado con éxito.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "❌ No se pudo eliminar el usuario.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
