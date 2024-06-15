package boisbarganhados;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Moments;
import org.opencv.core.CvType;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

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
        mat.convertTo(mat, CvType.CV_8UC3);
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

    // Function to calculate 2D HSV histogram with quantization of 16 values for H
    // and 8 values for V
    public static float[][] hsvHist(Mat mat) {
        int hBins = 16;
        int vBins = 8;
        float[] hRange = { 0, 180 }; // H channel range for HSV
        float[] vRange = { 0, 256 }; // V channel range for HSV
        Mat hsvMat = new Mat();
        opencv_imgproc.cvtColor(mat, hsvMat, opencv_imgproc.COLOR_BGR2HSV);

        Mat hist = new Mat();
        var images = new MatVector(hsvMat);
        try (IntPointer channels = new IntPointer(2);
                IntPointer histSizes = new IntPointer(2);
                FloatPointer ranges = new FloatPointer(hRange[0], hRange[1], vRange[0], vRange[1])) {
            channels.put(0, 0).put(1, 2); // Use H and V channels
            histSizes.put(0, hBins).put(1, vBins);
            opencv_imgproc.calcHist(images, channels, new Mat(), hist, histSizes, ranges, false);
        }

        float[][] histogram = new float[hBins][vBins];
        var histIndexer = ((FloatRawIndexer) hist.createIndexer());
        for (int h = 0; h < hBins; h++) {
            for (int v = 0; v < vBins; v++) {
                histogram[h][v] = histIndexer.get(h, v);
            }
        }
        return histogram;
    }

    public static double[] calculateHuMoments(Mat mat) {
        Mat binMat = new Mat();
        opencv_imgproc.threshold(mat, binMat, 0, 255, opencv_imgproc.THRESH_BINARY);
        Moments moments = opencv_imgproc.moments(binMat);
        double[] huMoments = new double[7];
        opencv_imgproc.HuMoments(moments, huMoments);
        for (int j = 0; j < 7; j++) {
            double a = -1 * Math.signum(huMoments[j]);
            double b = (huMoments[j] == 0) ? 0 : Math.log10(Math.abs(huMoments[j]));
            huMoments[j] = a * b;
        }
        return huMoments;
    }

    public static double[] getAllHuMoments(Mat image) {
        Mat grayImage = new Mat();
        Mat hsvImage = new Mat();
        cvtColor(image, grayImage, COLOR_BGRA2GRAY);
        cvtColor(image, hsvImage, COLOR_BGR2HSV);
        MatVector channels = new MatVector(3);
        org.bytedeco.opencv.global.opencv_core.split(hsvImage, channels);
        Mat hChannel = channels.get()[0];
        Mat sChannel = channels.get()[1];
        Mat vChannel = channels.get()[2];
        double[] hu = new double[28]; // 7 moments * 4 channels
        System.arraycopy(calculateHuMoments(grayImage), 0, hu, 0, 7);
        System.arraycopy(calculateHuMoments(hChannel), 0, hu, 7, 7);
        System.arraycopy(calculateHuMoments(sChannel), 0, hu, 14, 7);
        System.arraycopy(calculateHuMoments(vChannel), 0, hu, 21, 7);
        return hu;
    }

    public static Map<Integer, int[][]> calculateCoOccurrenceMatrices(Mat mat, int[] distances) {
        // Convert image to grayscale
        Mat grayMat = new Mat();
        opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);

        // Quantize to 16 gray levels
        Mat quantizedMat = new Mat(grayMat.size(), grayMat.type());
        grayMat.convertTo(quantizedMat, CvType.CV_8U, 15.0 / 255.0, 0);

        Map<Integer, int[][]> coOccurrenceMatrices = new HashMap<>();

        for (int distance : distances) {
            int[][] matrix = new int[16][16];

            for (int y = 0; y < quantizedMat.rows(); y++) {
                for (int x = 0; x < quantizedMat.cols(); x++) {
                    int pixelValue = (int) quantizedMat.ptr(y, x).get() & 0xFF;

                    // Horizontal co-occurrence
                    if (x + distance < quantizedMat.cols()) {
                        int neighborValue = (int) quantizedMat.ptr(y, x + distance).get() & 0xFF;
                        matrix[pixelValue][neighborValue]++;
                    }

                    // Vertical co-occurrence
                    if (y + distance < quantizedMat.rows()) {
                        int neighborValue = (int) quantizedMat.ptr(y + distance, x).get() & 0xFF;
                        matrix[pixelValue][neighborValue]++;
                    }

                    // Diagonal co-occurrence (down-right)
                    if (x + distance < quantizedMat.cols() && y + distance < quantizedMat.rows()) {
                        int neighborValue = (int) quantizedMat.ptr(y + distance, x + distance).get() & 0xFF;
                        matrix[pixelValue][neighborValue]++;
                    }

                    // Anti-diagonal co-occurrence (up-right)
                    if (x + distance < quantizedMat.cols() && y - distance >= 0) {
                        int neighborValue = (int) quantizedMat.ptr(y - distance, x + distance).get() & 0xFF;
                        matrix[pixelValue][neighborValue]++;
                    }
                }
            }

            coOccurrenceMatrices.put(distance, matrix);
        }

        return coOccurrenceMatrices;
    }
}