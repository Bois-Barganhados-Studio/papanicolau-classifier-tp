package com.boisbarganhados.view;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Scalar4i;
import org.opencv.imgproc.Imgproc;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

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
        Image image = new Image("file:crop.jpg");
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
        Image image = this.imageView.getImage();
        var bufferedImage = Utils.javafxImageToBufferedImage(image);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            Mat mat = openCVFrameConverter.convert(java2DFrameConverter.convert(bufferedImage));
            opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
            // opencv_imgproc.threshold(mat, mat, 127, 255, opencv_imgproc.THRESH_BINARY);
            // write image at root folder
            opencv_imgcodecs.imwrite("bw.jpg", mat);
            var bwBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(mat));
            Image bwImage = Utils.bufferedImageToJavafxImage(bwBufferedImage);
            imageView.setImage(bwImage);
        }
    }

    @FXML
    public void histogramChart() throws Exception {
        Image image = this.imageView.getImage();
        var bufferedImage = Utils.javafxImageToBufferedImage(image);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            Mat mat = openCVFrameConverter.convert(java2DFrameConverter.convert(bufferedImage));
            opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_RGB2BGR);
            
            // set red channel as zero and set green channel as zero
            for (int i = 0; i < mat.rows(); i++) {
                for (int j = 0; j < mat.cols(); j++) {
                    mat.put(new Scalar4i(0, 0, 0, 0));
                    mat.put(new Scalar4i(0, 0, 0, 1));
                }
            }

            Utils.saveImage(bufferedImage, "buff.jpg");
            opencv_imgcodecs.imwrite("im.jpg", mat);

            Mat grayMat = new Mat();
            opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);
            opencv_imgcodecs.imwrite("gray.jpg", grayMat);
            Mat binarizedMat = new Mat();
            // opencv_imgproc.threshold(grayMat, binarizedMat, 127, 255,
            // opencv_imgproc.THRESH_BINARY);
            Mat hist = new Mat();
            var channels = new IntPointer(1).put(0);
            var histSize = new IntPointer(1).put(256);
            var ranges = new FloatPointer(2).put(new float[] { 0.0f, 255.0f });
            opencv_imgproc.calcHist(new MatVector(grayMat), channels, new Mat(), hist,
                    histSize, ranges);
            float[] histogram = new float[256];
            var histIndexer = ((FloatRawIndexer) hist.createIndexer());
            for (int i = 0; i < 255; i++) {
                histogram[i] = histIndexer.get(i);
            }
            // // Print histogram data
            // for (int i = 0; i < 256; i++) {
            // System.out.println("histogram[" + i + "] = " + histogram[i]);
            // }
            var loader = com.boisbarganhados.Main.showDialog("Histogram.fxml");
            var controller = ((HistogramController) loader.getController());
            controller.setHistogram(histogram);
            controller.start();
        }
    }

    private void zoom(ImageView imageView, double scaleFactor) {
        scale *= scaleFactor;
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }
}