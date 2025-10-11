package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.Direccion;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.util.Sesion;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.awt.*;

public class UsuariosController {
    @FXML private Label mensajeLabel;


    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Long> idCol;
    @FXML private TableColumn<Usuario, String> nombreCol;
    @FXML private TableColumn<Usuario, String> emailCol;
    @FXML private TableColumn<Usuario, String> telefonoCol;
    @FXML private TableColumn<Usuario, String> passwordCol;
    @FXML private TableColumn<Usuario, Boolean> rolCol;
    @FXML private TableColumn<Usuario, String> direccionesCol;

    private final LogisticaFacade facade = LogisticaFacade.getInstance();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Mostrar rol como texto (Administrador / Usuario)
        rolCol.setCellValueFactory(new PropertyValueFactory<>("admin"));
        rolCol.setCellFactory(column -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean isAdmin, boolean empty) {
                super.updateItem(isAdmin, empty);
                if (empty || isAdmin == null) {
                    setText(null);
                } else {
                    setText(isAdmin ? "Administrador" : "Usuario");
                }
            }
        });

        // Mostrar direcciones concatenadas
        direccionesCol.setCellValueFactory(cellData -> {
            Usuario u = cellData.getValue();
            StringBuilder sb = new StringBuilder();
            for (Direccion d : u.getDirecciones()) {
                sb.append(d.getAlias()).append(" (").append(d.getCiudad()).append("), ");
            }
            String texto = sb.isEmpty() ? "Sin direcciones" : sb.substring(0, sb.length() - 2);
            return new javafx.beans.property.SimpleStringProperty(texto);
        });

        tablaUsuarios.setItems(FXCollections.observableArrayList(facade.listarUsuarios()));
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al login.");
        }
    }

    @FXML
    private void handleCrearUsuario(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/crear_usuario.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo Crear Usuario.");
        }
    }

    @FXML
    private void handleEditarUsuario(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/editar_usuario.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo Editar Usuario.");
        }
    }

    @FXML
    private void handleEliminarUsuario(ActionEvent event) {
        try {
            Sesion.cerrarSesion();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/usuarios.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar Usuario.");
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

