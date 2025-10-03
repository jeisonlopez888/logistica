module co.edu.uniquindio.logistica {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // abrir paquetes para reflexión / FXML
    opens co.edu.uniquindio.logistica to javafx.fxml;
    opens co.edu.uniquindio.logistica.ui to javafx.fxml;
    opens co.edu.uniquindio.logistica.model to javafx.base, javafx.fxml;

    exports co.edu.uniquindio.logistica;
    exports co.edu.uniquindio.logistica.ui;
    exports co.edu.uniquindio.logistica.model;
}





