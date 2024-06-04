package com.boisbarganhados;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Utils {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Mat image2Mat(BufferedImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        int r, g, b;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);

                r = (rgb >> 16) & 0xFF;
                g = (rgb >> 8) & 0xFF;
                b = (rgb) & 0xFF;

                mat.put(y, x, new byte[] { (byte) b, (byte) g, (byte) r });
            }
        }
        return mat;
    }

    public static BufferedImage javafxImageToBufferedImage(Image javafxImage) {
        if (javafxImage == null) {
            return null;
        }
        int width = (int) javafxImage.getWidth();
        int height = (int) javafxImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        SwingFXUtils.fromFXImage(javafxImage, bufferedImage);
        return bufferedImage;
    }

    public static Image bufferedImageToJavafxImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static BufferedImage mat2Image(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }
}