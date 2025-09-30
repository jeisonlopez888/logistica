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

    // Paquetes abiertos a JavaFX
    opens co.edu.uniquindio.logistica to javafx.fxml;
    opens co.edu.uniquindio.logistica.controller to javafx.fxml;
    opens co.edu.uniquindio.logistica.model to javafx.base; // 👈 ESTA LÍNEA ES LA CLAVE

    // Paquetes exportados
    exports co.edu.uniquindio.logistica;
    exports co.edu.uniquindio.logistica.controller;
    exports co.edu.uniquindio.logistica.model; // opcional, si usas fuera del módulo
}




