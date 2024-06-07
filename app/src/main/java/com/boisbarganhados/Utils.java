package com.boisbarganhados;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_imgproc.*;

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
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

    public static void saveImage(BufferedImage image, String name) {
        try {
            File outputfile = new File(name);
            ImageIO.write(image, "jpg", outputfile);
            System.out.println("Path:" + outputfile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[] calculateHistogram(Image image) {
        // Convert JavaFX Image to BufferedImage
        BufferedImage bufferedImage = Utils.javafxImageToBufferedImage(image);

        // Convert BufferedImage to OpenCV Mat using JavaCV
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            Mat mat = openCVFrameConverter.convert(java2DFrameConverter.convert(bufferedImage));

            // Convert the image to grayscale
            Mat grayMat = new Mat();
            opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);

            // Apply binary threshold to get a binary image
            Mat binaryMat = new Mat();
            opencv_imgproc.threshold(grayMat, binaryMat, 128, 255, opencv_imgproc.THRESH_BINARY);

            Mat hist = new Mat();
            int histSize = 256; // Number of bins
            float[] range = { 0, 256 }; // Range of values

            // Prepare the required data structures
            MatVector images = new MatVector(binaryMat);
            try (IntPointer channels = new IntPointer().put(0);
                    IntPointer histSizes = new IntPointer(1).put(histSize);
                    FloatPointer ranges = new FloatPointer(range)) {
                // Correct calcHist method call
                opencv_imgproc.calcHist(images, channels, new Mat(), hist, histSizes, ranges);
            }

            // Convert histogram to int array
            int[] histogram = new int[histSize];
            for (int i = 0; i < histSize; i++) {
                histogram[i] = (int) hist.ptr(i).get();
            }

            return histogram;
        }
    }

    public static int[] hist(Mat binaryMat) {
        Mat hist = new Mat();
        int histSize = 256; // Number of bins
        float[] range = { 0, 256 }; // Range of values

        // Prepare the required data structures
        MatVector images = new MatVector(binaryMat);
        try (IntPointer channels = new IntPointer(1).put(0);
                IntPointer histSizes = new IntPointer(1).put(histSize);
                FloatPointer ranges = new FloatPointer(range)) {
            // Correct calcHist method call
            opencv_imgproc.calcHist(images, channels, new Mat(), hist, histSizes, ranges, false);
        }

        // Convert histogram to int array
        int[] histogram = new int[histSize];
        for (int i = 0; i < histSize; i++) {
            histogram[i] = hist.ptr(i).getUnsigned();
        }

        for (int i = 0; i < histSize; i++) {
            System.out.println(histogram[i]);
        }

        return histogram;
    }
}