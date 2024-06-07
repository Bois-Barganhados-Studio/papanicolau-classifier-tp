package com.boisbarganhados;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("PAI Test");
        // use TestSemFxml to test without FXML
        var loader = new FXMLLoader(Main.class.getResource("view/ImageZoomAndMove.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        applyJMetro(scene);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        applyJMetro(scene);
        primaryStage.setScene(scene);
    }

    public static FXMLLoader showDialog(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        return loader;
    }

    private static void applyJMetro(Scene scene) {
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
