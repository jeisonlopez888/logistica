package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    private LogisticaFacade facade;

    public void setFacade(LogisticaFacade facade) {
        this.facade = facade;
        cargarUsuarios();
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminCol.setCellValueFactory(new PropertyValueFactory<>("admin"));
    }

    private void cargarUsuarios() {
        if (facade != null) {
            usuariosTable.setItems(FXCollections.observableArrayList(facade.listarUsuarios()));
        }
    }

    @FXML
    private void handleEliminarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            facade.listarUsuarios().remove(seleccionado);
            cargarUsuarios();
            mensajeLabel.setText("✅ Usuario eliminado correctamente");
            mensajeLabel.setStyle("-fx-text-fill: green;");
        } else {
            mensajeLabel.setText("❌ Selecciona un usuario para eliminar");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleEditarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
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
            }
        } else {
            mensajeLabel.setText("❌ Selecciona un usuario para editar");
            mensajeLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
