package com.boisbarganhados.view;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.boisbarganhados.Utils;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ImageController {
    @FXML
    private ImageView imageView;
    @FXML
    private StackPane stackPane;
    private double scale = 1.0;
    private double mouseX;
    private double mouseY;

    @FXML
    public void initialize() {
        Image image = new Image("file:test.png");
        imageView.setImage(image);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setPreserveRatio(true);

        imageView.setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        imageView.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseX;
            double deltaY = event.getSceneY() - mouseY;
            imageView.setTranslateX(imageView.getTranslateX() + deltaX);
            imageView.setTranslateY(imageView.getTranslateY() + deltaY);
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });
    }

    @FXML
    private void zoomIn() {
        zoom(imageView, 1.1);
    }

    @FXML
    private void zoomOut() {
        zoom(imageView, 0.9);
    }

    @FXML
    private void bwFilter() {
        var image = imageView.getImage();
        Mat mat = Utils.image2Mat(Utils.javafxImageToBufferedImage(image));
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Image bwImage = Utils.bufferedImageToJavafxImage(Utils.mat2Image(mat));
        imageView.setImage(bwImage);
    }

    private void zoom(ImageView imageView, double scaleFactor) {
        scale *= scaleFactor;
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }
}