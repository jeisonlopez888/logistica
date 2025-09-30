package co.edu.uniquindio.logistica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Usuarios en DataStore: " +
                co.edu.uniquindio.logistica.store.DataStore.getInstance().getUsuarios().size());

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Plataforma de Logística");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
