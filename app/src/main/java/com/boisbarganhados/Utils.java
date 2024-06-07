package com.boisbarganhados;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.opencv.core.CvType;
import org.bytedeco.opencv.global.opencv_imgproc;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public final class Utils {

    public static int HIST_SIZE = 256;
    public static float[] RANGE = new float[] { 0.0f, 255.0f };

    public static Mat image2Mat(BufferedImage image) {
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), org.bytedeco.opencv.global.opencv_core.CV_8UC3);
        mat.data().put(data);
        return mat;
    }

    public BufferedImage matToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.arrayWidth(), frame.arrayHeight(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.data().get(data);
        return image;
    }

    public static Mat toMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4]; 
        PixelReader reader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);
        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.ptr(0).put(buffer);
        return mat;
    }

    public static Image bufferedImageToJavafxImage(BufferedImage bufferedImage) throws IllegalArgumentException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
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

    public static float[] hist(Mat mat, int histSize, float[] range) {
        Mat hist = new Mat();
        var images = new MatVector(mat);
        try (IntPointer channels = new IntPointer(1);
                IntPointer histSizes = new IntPointer(1);
                FloatPointer ranges = new FloatPointer(range)) {
            channels.put(0, 0);
            histSizes.put(0, histSize);
            opencv_imgproc.calcHist(images, channels, new Mat(), hist, histSizes, ranges, false);
        }
        float[] histogram = new float[(int) range[1]];
        var histIndexer = ((FloatRawIndexer) hist.createIndexer());
        for (int i = 0; i < histSize; i++) {
            histogram[i] = histIndexer.get(i);
        }
        return histogram;
    }
}