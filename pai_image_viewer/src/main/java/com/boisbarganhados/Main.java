package com.boisbarganhados;


import com.boisbarganhados.view.TestSemFxml;

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
        TestSemFxml testSemFxml = new TestSemFxml();
        testSemFxml.initialize(null, null);
        Scene scene = new Scene(testSemFxml.getRootPane(), 800, 600);
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
        // applyJMetro(scene);
        primaryStage.setScene(scene);
    }

    private static void applyJMetro(Scene scene) {
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
