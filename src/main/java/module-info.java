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
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;
    requires java.desktop;

    // 🔹 abrir paquetes para reflexión / FXML
    opens co.edu.uniquindio.logistica to javafx.fxml;
    opens co.edu.uniquindio.logistica.ui to javafx.fxml;
    opens co.edu.uniquindio.logistica.model to javafx.base, javafx.fxml;
    opens co.edu.uniquindio.logistica.model.DTO to javafx.base, javafx.fxml;
    opens co.edu.uniquindio.logistica.observer to javafx.base;

    // 🔹 agregar esta línea para permitir que JavaFX acceda a tus clases de prueba
    opens co.edu.uniquindio.logistica.test to javafx.graphics;

    exports co.edu.uniquindio.logistica;
    exports co.edu.uniquindio.logistica.ui;
    exports co.edu.uniquindio.logistica.model;
    exports co.edu.uniquindio.logistica.model.DTO;
    exports co.edu.uniquindio.logistica.facade;
    exports co.edu.uniquindio.logistica.service;
    exports co.edu.uniquindio.logistica.util;
    exports co.edu.uniquindio.logistica.store;
    exports co.edu.uniquindio.logistica.factory;
}
