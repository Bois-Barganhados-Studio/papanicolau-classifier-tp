package com.boisbarganhados.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TestController {

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        backButton.setOnAction(event -> navigateBack());
    }

    private void navigateBack() {
        try {
            com.boisbarganhados.Main.setRoot("Home.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
