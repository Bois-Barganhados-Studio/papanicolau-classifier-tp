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
import org.bytedeco.javacpp.indexer.DoubleRawIndexer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Moments;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Size;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public final class Utils {

    public static int HIST_SIZE = 256;
    public static float[] RANGE = new float[] { 0.0f, 255.0f };

    /**
     * Convert a Mat object to a BufferedImage
     * 
     * @param image Mat object to be converted
     * @return BufferedImage with the Mat data
     */
    public static Mat image2Mat(BufferedImage image) {
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), org.bytedeco.opencv.global.opencv_core.CV_8UC3);
        mat.data().put(data);
        return mat;
    }

    /**
     * Convert a BufferedImage to a Mat object
     * 
     * @param frame BufferedImage to be converted
     * @return Mat object with the BufferedImage data
     */
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

    /**
     * Convert a Mat object to a JavaFX Image
     * 
     * @param mat Mat object to be converted
     * @return JavaFX Image with the Mat data
     */
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

    /**
     * Convert a JavaFX Image to a BufferedImage
     * 
     * @param image JavaFX Image to be converted
     * @return BufferedImage with the JavaFX Image data
     */
    public static Image bufferedImageToJavafxImage(BufferedImage bufferedImage) throws IllegalArgumentException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    /**
     * Save an image to a file
     * 
     * @param image Image to be saved
     * @param name  Name of the file
     */
    public static void saveImage(BufferedImage image, String name) {
        try {
            File outputfile = new File(name);
            ImageIO.write(image, "jpg", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the histogram of an image
     * 
     * @param mat Image to calculate the histogram
     * @return Array with the histogram values
     */
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

    /**
     * Calculate the histogram of the Hue and Value channels of an image in the HSV
     * 
     * @param mat   Image in BGR format
     * @param hBins Number of bins for the Hue channel
     * @return 2D array with the histogram values
     */
    public static float[][] hsvHist(Mat mat, Integer hBins) {
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

    /**
     * Do a threshold operation on an image
     * 
     * @param image     Image to be thresholded
     * @param threshold Threshold value
     * @return Thresholded image
     */
    public static Mat threshold(Mat image, int threshold) {
        Mat thresholded = new Mat();
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        opencv_imgproc.threshold(image, thresholded, threshold, 255, opencv_imgproc.THRESH_BINARY);
        return thresholded;
    }

    /**
     * Invert the colors of an image
     * 
     * @param image Image to be inverted
     * @return Inverted image
     */
    public static Mat invert(Mat image) {
        Mat inverted = new Mat();
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        opencv_core.bitwise_not(image, inverted);
        return inverted;
    }

    /**
     * Apply a contrast adjustment to an image
     * 
     * @param image Image to be adjusted
     * @param alpha Contrast factor
     * @param beta  Brightness factor
     * @return Adjusted image
     */
    public static Mat contrast(Mat image, double alpha, int beta) {
        Mat contrasted = new Mat();
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        image.convertTo(contrasted, opencv_core.CV_8UC3, alpha, beta);
        return contrasted;
    }

    /**
     * Apply a gamma correction to an image
     * 
     * @param image Image to be corrected
     * @param gamma Gamma value
     * @return Corrected image
     */
    public static Mat saturate(Mat image, double alpha) {
        Mat saturated = new Mat();
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        opencv_imgproc.cvtColor(image, saturated, opencv_imgproc.COLOR_BGR2HSV);

        MatVector hsvChannels = new MatVector();
        opencv_core.split(saturated, hsvChannels);

        Mat saturation = hsvChannels.get(1);
        saturation.convertTo(saturation, -1, alpha, 0);

        opencv_core.merge(hsvChannels, saturated);

        Mat saturatedImage = new Mat();
        opencv_imgproc.cvtColor(saturated, saturatedImage, opencv_imgproc.COLOR_HSV2BGR);

        return saturatedImage;
    }

    /**
     * Apply a brightness adjustment to an image
     * 
     * @param image Image to be adjusted
     * @param beta  Brightness factor
     * @return Adjusted image
     */
    public static Mat brightness(Mat image, double beta) {
        Mat brightened = new Mat();
        if (image.channels() == 4) {
            opencv_imgproc.cvtColor(image, image, opencv_imgproc.COLOR_BGRA2BGR);
        }
        image.convertTo(brightened, -1, 1, beta);
        return brightened;
    }

    /**
     * Apply a hue adjustment to an image
     * 
     * @param image Image to be adjusted
     * @param shift Hue shift value
     * @return Adjusted image
     */
    public static Mat hue(Mat image, double shift) {
        Mat hsvImage = new Mat();
        if (image.channels() == 4) {
            opencv_imgproc.cvtColor(image, image, opencv_imgproc.COLOR_BGRA2BGR);
        }
        opencv_imgproc.cvtColor(image, hsvImage, opencv_imgproc.COLOR_BGR2HSV);

        MatVector hsvChannels = new MatVector();
        opencv_core.split(hsvImage, hsvChannels);

        Mat hue = hsvChannels.get(0);
        hue.convertTo(hue, hue.type(), 1, shift);

        opencv_core.merge(hsvChannels, hsvImage);

        Mat hueAdjusted = new Mat();
        opencv_imgproc.cvtColor(hsvImage, hueAdjusted, opencv_imgproc.COLOR_HSV2BGR);

        return hueAdjusted;
    }

    /**
     * Apply a sharpness filter to an image
     * 
     * @param image Image to be sharpened
     * @param alpha Sharpness factor
     * @return Sharpened image
     */
    public static Mat sharpness(Mat image, double alpha) {
        Mat blurred = new Mat();
        Mat sharpened = new Mat();

        if (image.channels() == 4) {
            opencv_imgproc.cvtColor(image, image, opencv_imgproc.COLOR_BGRA2BGR);
        }

        opencv_imgproc.GaussianBlur(image, blurred, new Size(0, 0), 10);

        opencv_core.addWeighted(image, 1 + alpha, blurred, -alpha, 0.0, sharpened);

        return sharpened;
    }

    /**
     * Apply a blur filter to an image
     * 
     * @param image      Image to be blurred
     * @param kernelSize Kernel size for the blur filter
     * @return Blurred image
     */
    public static Mat colorFilter(Mat image, Color color) {
        Mat filteredImage = new Mat();
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        MatVector channels = new MatVector();
        org.bytedeco.opencv.global.opencv_core.split(image, channels);
        if (channels.size() != 3) {
            throw new IllegalArgumentException("Expected BGR image (3 channels)");
        }
        MatVector mergeChannels = new MatVector(3);
        switch (color) {
            case RED:
                mergeChannels.put(0, channels.get(0));
                mergeChannels.put(1, channels.get(1));
                mergeChannels.put(2, Mat.zeros(channels.get(1).size(), channels.get(1).type()).asMat());
                break;
            case GREEN:
                mergeChannels.put(0, channels.get(0));
                mergeChannels.put(1, Mat.zeros(channels.get(1).size(), channels.get(1).type()).asMat());
                mergeChannels.put(2, channels.get(2));
                break;
            case BLUE:
                mergeChannels.put(0, Mat.zeros(channels.get(0).size(), channels.get(0).type()).asMat());
                mergeChannels.put(1, channels.get(1));
                mergeChannels.put(2, channels.get(2));
                break;
            default:
                throw new IllegalArgumentException("Invalid color");

        }
        org.bytedeco.opencv.global.opencv_core.merge(mergeChannels, filteredImage);
        return filteredImage;
    }

    /**
     * Apply a blur filter to an image
     * 
     * @param image      Image to be blurred
     * @param kernelSize Kernel size for the blur filter
     * @return Blurred image
     */
    public static double[] getAllHuMoments(Mat image) {
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
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

    /**
     * Calculate the Hu moments of an image
     * 
     * @param mat Image to calculate the moments
     * @return Array with the 7 Hu moments
     */
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

    /**
     * Calculate the co-occurrence matrices of an image
     * 
     * @param mat       Image to calculate the matrices
     * @param distances Distances to calculate the matrices
     * @return Map with the calculated matrices
     */
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

    /**
     * Apply a Fourier filter to an image
     * 
     * @param image   Image to be filtered
     * @param cutFreq Cutoff frequency
     * @param pass    True for high-pass filter, false for low-pass filter
     * @return Filtered image
     */
    public static Mat applyFourierFilter(Mat image, float cutFreq, boolean pass) {
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        Mat padded = new Mat();
        int m = opencv_core.getOptimalDFTSize(gray.rows());
        int n = opencv_core.getOptimalDFTSize(gray.cols());
        opencv_core.copyMakeBorder(gray, padded, 0, m - gray.rows(), 0, n - gray.cols(), opencv_core.BORDER_CONSTANT,
                Scalar.all(0));
        Mat complexImage = new Mat();
        MatVector planes = new MatVector(new Mat(padded.size(), opencv_core.CV_32F),
                new Mat(padded.size(), opencv_core.CV_32F));
        padded.convertTo(planes.get(0), opencv_core.CV_32F);
        planes.put(1, Mat.zeros(padded.size(), opencv_core.CV_32F).asMat());
        opencv_core.merge(planes, complexImage);
        opencv_core.dft(complexImage, complexImage);
        shiftDFT(complexImage);
        Mat mask = new Mat(complexImage.size(), opencv_core.CV_32F);
        if (pass)
            createHighPassFilter(mask, cutFreq);
        else
            createLowPassFilter(mask, cutFreq);
        MatVector newPlanes = new MatVector();
        opencv_core.split(complexImage, newPlanes);
        opencv_core.multiply(newPlanes.get(0), mask, newPlanes.get(0));
        opencv_core.multiply(newPlanes.get(1), mask, newPlanes.get(1));
        opencv_core.merge(newPlanes, complexImage);
        shiftDFT(complexImage);
        opencv_core.idft(complexImage, complexImage, opencv_core.DFT_SCALE | opencv_core.DFT_REAL_OUTPUT, -1);
        Mat result = new Mat();
        opencv_core.normalize(complexImage, complexImage, 0, 255, opencv_core.NORM_MINMAX, -1,
                new Mat(0, 0, opencv_core.CV_8UC3));
        complexImage.convertTo(result, opencv_core.CV_8UC3);
        return result;
    }

    /**
     * Shift the DFT to the center
     * 
     * @param image Image to be shifted
     */
    private static void shiftDFT(Mat image) {
        int cx = image.cols() / 2;
        int cy = image.rows() / 2;
        Mat q0 = new Mat(image, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(image, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(image, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(image, new Rect(cx, cy, cx, cy));
        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    /**
     * Create a low-pass filter mask
     * 
     * @param mask            Mask to be created
     * @param cutoffFrequency Cutoff frequency
     */
    private static void createLowPassFilter(Mat mask, float cutoffFrequency) {
        int rows = mask.rows();
        int cols = mask.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float d = (i - rows / 2) * (i - rows / 2) + (j - cols / 2) * (j - cols / 2);
                mask.ptr(i, j).putFloat((float) Math.exp(-d / (2 * cutoffFrequency * cutoffFrequency)));
            }
        }
    }

    /**
     * Create a high-pass filter mask
     * 
     * @param mask            Mask to be created
     * @param cutoffFrequency Cutoff frequency
     */
    private static void createHighPassFilter(Mat mask, float cutoffFrequency) {
        int rows = mask.rows();
        int cols = mask.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float d = (i - rows / 2) * (i - rows / 2) + (j - cols / 2) * (j - cols / 2);
                mask.ptr(i, j).putFloat((float) (1 - Math.exp(-d / (2 * cutoffFrequency * cutoffFrequency))));
            }
        }
    }

    /**
     * Get the Fourier spectrum of an image
     * 
     * @param image Image to get the spectrum
     * @return Fourier spectrum of the image
     */
    public static Mat getFourierSpectrum(Mat image) {
        if (image.channels() == 4) {
            cvtColor(image, image, COLOR_BGRA2BGR);
        }
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        Mat padded = new Mat();
        int m = opencv_core.getOptimalDFTSize(gray.rows());
        int n = opencv_core.getOptimalDFTSize(gray.cols());
        opencv_core.copyMakeBorder(gray, padded, 0, m - gray.rows(), 0, n - gray.cols(), opencv_core.BORDER_CONSTANT,
                Scalar.all(0));
        Mat complexImage = new Mat();
        MatVector planes = new MatVector(new Mat(padded.size(), opencv_core.CV_64F),
                new Mat(padded.size(), opencv_core.CV_64F));
        padded.convertTo(planes.get(0), opencv_core.CV_64F);
        planes.put(1, Mat.zeros(padded.size(), opencv_core.CV_64F).asMat());
        opencv_core.merge(planes, complexImage);
        opencv_core.dft(complexImage, complexImage);
        shiftDFT(complexImage);
        MatVector newPlanes = new MatVector();
        opencv_core.split(complexImage, newPlanes);
        Mat mag = new Mat();
        opencv_core.magnitude(planes.get(0), planes.get(1), mag);
        var s = Scalar.all(1);
        Mat exp = new Mat(mag.size(), mag.type(), s);
        opencv_core.add(exp, mag, mag);
        opencv_core.log(mag, mag);
        opencv_core.normalize(mag, mag, 0, 1, opencv_core.NORM_MINMAX, -1, new Mat());
        return mag;
    }

    /**
     * Convert a Mat object to a 2D array
     * 
     * @param mat Mat object to be converted
     * @return 2D array with the Mat data
     */
    public static double[][] matTo2DArray(Mat mat) {
        int rows = mat.rows();
        int cols = mat.cols();
        double[][] data = new double[rows][cols];
        var indexer = ((DoubleRawIndexer) mat.createIndexer());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = indexer.get(i, j);
            }
        }
        return data;
    }
}