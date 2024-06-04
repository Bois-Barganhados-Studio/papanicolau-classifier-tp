package com.boisbarganhados.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {

    @FXML
    private Button navigateButton;
    
    @FXML
    private Button imageButton;

    @FXML
    private void initialize() {
        navigateButton.setOnAction(event -> navigateToTestScreen());
        imageButton.setOnAction(event -> navigateToImageScreen());
    }

    private void navigateToTestScreen() {
        try {
            com.boisbarganhados.Main.setRoot("Test.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToImageScreen() {
        try {
            com.boisbarganhados.Main.setRoot("ImageZoomAndMove.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
