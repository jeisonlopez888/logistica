package co.edu.uniquindio.logistica.ui;

import co.edu.uniquindio.logistica.facade.LogisticaFacade;
import co.edu.uniquindio.logistica.model.DTO.UsuarioDTO;
import co.edu.uniquindio.logistica.util.ValidacionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Clase base abstracta con la lógica común de creación de usuarios.
 * Se extiende por controladores como CrearUsuarioAdminController y RegistroUsuarioController.
 */
public abstract class CrearUsuarioController {

    @FXML protected TextField idField;
    @FXML protected TextField nombreField;
    @FXML protected TextField emailField;
    @FXML protected TextField telefonoField;
    @FXML protected PasswordField passwordField;
    @FXML protected Label mensajeLabel;

    // Campos opcionales (direcciones)
    @FXML protected TextField alias1Field;
    @FXML protected TextField detalle1Field;
    @FXML protected TextField alias2Field;
    @FXML protected TextField detalle2Field;
    @FXML protected ComboBox<String> zonaOrigenCombo;
    @FXML protected ComboBox<String> zonaDestinoCombo;

    // Solo los controladores que lo tengan (admin) inicializarán este campo
    @FXML protected CheckBox adminCheck;

    protected final LogisticaFacade facade = LogisticaFacade.getInstance();
    protected Runnable onUsuarioCreado;

    public void setOnUsuarioCreado(Runnable onUsuarioCreado) {
        this.onUsuarioCreado = onUsuarioCreado;
    }

    @FXML
    protected void initialize() {
        if (zonaOrigenCombo != null)
            zonaOrigenCombo.getItems().addAll("Sur", "Centro", "Norte");
        if (zonaDestinoCombo != null)
            zonaDestinoCombo.getItems().addAll("Sur", "Centro", "Norte");
    }

    /** 🔹 Lógica genérica de creación de usuario */
    @FXML
    protected void handleRegistrar() {
        try {
            Long id = Long.parseLong(idField.getText().trim());
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String password = passwordField.getText();

            // cada subclase decide si puede o no marcar admin
            boolean admin = getAdminFlag();

            // Validaciones básicas
            if (ValidacionUtil.isEmpty(nombre) || ValidacionUtil.isEmpty(email) || ValidacionUtil.isEmpty(password)) {
                mostrarMensaje("❌ Nombre, correo y contraseña son obligatorios", "red");
                return;
            }
            if (!ValidacionUtil.isEmailValid(email)) {
                mostrarMensaje("❌ Formato de correo inválido", "red");
                return;
            }
            if (!ValidacionUtil.isEmpty(telefono) && !ValidacionUtil.isPhoneValid(telefono)) {
                mostrarMensaje("❌ Formato de teléfono inválido", "red");
                return;
            }

            // Validar duplicado
            boolean existe = facade.listarUsuarios().stream()
                    .anyMatch(u -> u.getId().equals(id) || u.getEmail().equalsIgnoreCase(email));
            if (existe) {
                mostrarMensaje("⚠️ Ya existe un usuario con esa cédula o correo", "orange");
                return;
            }

            // Crear usuario usando Facade (retorna UsuarioDTO)
            UsuarioDTO nuevoDTO = facade.crearUsuario(id, nombre, email, telefono, password, admin);

            // Crear direcciones usando Facade (retorna DireccionDTO)
            java.util.List<co.edu.uniquindio.logistica.model.DTO.DireccionDTO> direccionesDTO = new java.util.ArrayList<>();
            String zonaOrigen = zonaOrigenCombo != null ? zonaOrigenCombo.getValue() : null;
            String zonaDestino = zonaDestinoCombo != null ? zonaDestinoCombo.getValue() : null;

            if (zonaOrigen != null && !ValidacionUtil.isEmpty(detalle1Field.getText())) {
                co.edu.uniquindio.logistica.model.DTO.DireccionDTO d1 = facade.crearDireccion(
                        alias1Field.getText().isEmpty() ? "Origen" : alias1Field.getText(),
                        detalle1Field.getText(),
                        "",
                        zonaOrigen
                );
                direccionesDTO.add(d1);
            }

            if (zonaDestino != null && !ValidacionUtil.isEmpty(detalle2Field.getText())) {
                co.edu.uniquindio.logistica.model.DTO.DireccionDTO d2 = facade.crearDireccion(
                        alias2Field.getText().isEmpty() ? "Destino" : alias2Field.getText(),
                        detalle2Field.getText(),
                        "",
                        zonaDestino
                );
                direccionesDTO.add(d2);
            }

            // Actualizar usuario con direcciones
            nuevoDTO.setDirecciones(direccionesDTO);
            facade.registrarUsuario(nuevoDTO);
            mostrarMensaje("✅ Usuario creado correctamente", "green");

            limpiarCampos();
            if (onUsuarioCreado != null) onUsuarioCreado.run();

        } catch (NumberFormatException e) {
            mostrarMensaje("❌ El ID debe ser numérico", "red");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("⚠️ Error al crear usuario: " + e.getMessage(), "red");
        }
    }

    /** 🔸 Metodo que define si el usuario puede marcar admin (cada subclase lo implementa) */
    protected abstract boolean getAdminFlag();

    /** 🔸 Metodo para volver según la vista (cada subclase lo implementa) */
    @FXML
    protected abstract void handleVolver(ActionEvent event);

    protected void mostrarMensaje(String texto, String color) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    protected void limpiarCampos() {
        idField.clear();
        nombreField.clear();
        emailField.clear();
        telefonoField.clear();
        passwordField.clear();
        if (adminCheck != null) adminCheck.setSelected(false);
        if (alias1Field != null) alias1Field.clear();
        if (detalle1Field != null) detalle1Field.clear();
        if (alias2Field != null) alias2Field.clear();
        if (detalle2Field != null) detalle2Field.clear();
        if (zonaOrigenCombo != null) zonaOrigenCombo.getSelectionModel().clearSelection();
        if (zonaDestinoCombo != null) zonaDestinoCombo.getSelectionModel().clearSelection();
    }
}
