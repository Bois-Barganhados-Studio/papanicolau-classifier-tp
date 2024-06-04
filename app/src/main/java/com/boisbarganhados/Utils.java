package com.boisbarganhados;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.bytedeco.opencv.opencv_core.Mat;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Utils {

    public static Mat image2Mat(BufferedImage image) {
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), org.bytedeco.opencv.global.opencv_core.CV_8UC3);
        mat.data().put(data);
        return mat;
    }

    public static BufferedImage javafxImageToBufferedImage(Image javafxImage) throws IllegalArgumentException {
        if (javafxImage == null) {
            throw new IllegalArgumentException("javafxImage is null");
        }
        int width = (int) javafxImage.getWidth();
        int height = (int) javafxImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        SwingFXUtils.fromFXImage(javafxImage, bufferedImage);
        return bufferedImage;
    }

    public static Image bufferedImageToJavafxImage(BufferedImage bufferedImage) throws IllegalArgumentException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static BufferedImage mat2Image(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();
        byte[] data = new byte[width * height * channels];
        mat.data().get(data);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, data);
        return image;
    }
}