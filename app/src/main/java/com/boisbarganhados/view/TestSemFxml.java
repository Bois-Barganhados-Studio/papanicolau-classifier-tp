package com.boisbarganhados.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.boisbarganhados.Main;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestSemFxml implements Initializable {

    private Button navigateButton, chartButton;

    private Label label;

    private BorderPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootPane = new BorderPane();
        rootPane.setPrefSize(800, 600);
        rootPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        label = new Label("This is a test screen without FXML");
        navigateButton = new Button("Go to Home Screen");
        navigateButton.setOnAction(event -> navigateToHomeScreen());
        chartButton = new Button("Go to Chart Screen");
        chartButton.setOnAction(event -> navigateToChartScreen());
        rootPane.setTop(label);
        rootPane.setCenter(navigateButton);
        rootPane.setBottom(chartButton);
        // alling chartButton to the center
        BorderPane.setAlignment(chartButton, javafx.geometry.Pos.CENTER);
        BorderPane.setAlignment(label, javafx.geometry.Pos.CENTER);
    }

    private void navigateToHomeScreen() {
        try {
            Main.setRoot("Home.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToChartScreen() {
        try {
            Main.setRoot("ChartTest.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
