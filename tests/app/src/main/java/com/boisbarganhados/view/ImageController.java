package com.boisbarganhados.view;

import java.util.Map;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;

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
        Image image = new Image("file:bogo.jpg");
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
    public void bwFilter() throws Exception {
        var image = this.imageView.getImage();
        var mat = Utils.toMat(image);
        opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bwBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(mat));
            Image bwImage = Utils.bufferedImageToJavafxImage(bwBufferedImage);
            imageView.setImage(bwImage);
        }
    }

    @FXML
    public void histogramChart() throws Exception {
        var image = this.imageView.getImage();
        var mat = Utils.toMat(image);


        int[] distances = {1, 2, 4, 8, 16, 32};
        Map<Integer, int[][]> coOccurrenceMatrices = Utils.calculateCoOccurrenceMatrices(mat, distances);

        for (int distance : distances) {
            System.out.println("Co-Occurrence Matrix for distance " + distance + ":");
            int[][] matrix = coOccurrenceMatrices.get(distance);
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
        }



        var grayMat = new Mat();
        opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgcodecs.imwrite("im.jpg", grayMat);
        // 16 tons de cinza
        var histogram = Utils.hist(grayMat, 16, new float[] { 0, 255 });
        var hsvHistogram = Utils.hsvHist(mat);
        var loader = com.boisbarganhados.Main.showDialog("Histogram.fxml");
        var controller = ((HistogramController) loader.getController());
        controller.setHistogram(histogram);
        controller.setHsvHistogram(hsvHistogram);
        controller.start();
        controller.startHsv();
    }

    private void zoom(ImageView imageView, double scaleFactor) {
        scale *= scaleFactor;
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }
}