package boisbarganhados;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Data;

import java.io.File;
import java.util.Stack;

import javax.imageio.ImageIO;

@Data
public class PrimaryController {
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    private boolean isMaximized = false;
    private double lastWidth = 0;
    private double lastHeight = 0;
    private double lastX = 0;
    private double lastY = 0;

    private Stack<Image> undoStack = new Stack<>();
    private Stack<Image> redoStack = new Stack<>();

    private final ModalPane modalPane = new ModalPane();

    private Slider contrastSlider;
    private Slider saturationSlider;
    private Slider hueSlider;
    private Slider brightnessSlider;
    private Slider sharpnessSlider;

    @FXML
    private StackPane primaryContainer;
    @FXML
    private ImageView imageView;
    @FXML
    private ToolBar topBar;
    @FXML
    private ToolBar leftBar;
    @FXML
    private BorderPane imageWrapper;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private MenuItem saveButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private HBox contrastSliderWrapper;
    @FXML
    private Spinner<Integer> binSpinner;
    @FXML
    private Button binButton;
    @FXML
    private Spinner<Integer> fourierSpinner;
    @FXML
    private Button lowPassButton;
    @FXML
    private Button highPassButton;
    @FXML
    private Button grayHistogramButton;
    @FXML
    private Button HSVHistogramButton;
    @FXML
    private Spinner<Integer> grayHistogramSpinner;
    @FXML
    private Spinner<Integer> HSVHistogramSpinner;
    @FXML
    private HBox saturationSliderWrapper;
    @FXML
    private HBox hueSliderWrapper;
    @FXML
    private HBox brightnessSliderWrapper;
    @FXML
    private HBox sharpnessSliderWrapper;

    public void zoomImage(double zoomFactor) {
        imageView.setScaleX(imageView.getScaleX() * zoomFactor);
        imageView.setScaleY(imageView.getScaleY() * zoomFactor);

        double newWidth = imageView.getImage().getWidth() * imageView.getScaleX();
        double newHeight = imageView.getImage().getHeight() * imageView.getScaleY();

        imageWrapper.setMinWidth(newWidth);
        imageWrapper.setMinHeight(newHeight);

        scrollPane.setHbarPolicy(newWidth > scrollPane.getWidth() ? ScrollPane.ScrollBarPolicy.ALWAYS
                : ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(newHeight > scrollPane.getHeight() ? ScrollPane.ScrollBarPolicy.ALWAYS
                : ScrollPane.ScrollBarPolicy.NEVER);
    }

    public void resetImageZoom() {
        imageView.setScaleX(1);
        imageView.setScaleY(1);
        zoomImage(1);
    }

    public void resetUndoAndRedoStacks() {
        undoStack.clear();
        redoStack.clear();
        saveButton.setDisable(true);
    }

    public void editImage(Image image) {
        undoStack.push(imageView.getImage());
        redoStack.clear();
        imageView.setImage(image);

        saveButton.setDisable(false);
        undoButton.setDisable(false);
        redoButton.setDisable(true);
    }

    public void contrastImage(double contrast) {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.contrast(mat, contrast, 0);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image contrastImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(contrastImage);
        }
    }

    public void saturateImage(double saturation) {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.saturate(mat, saturation);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image saturateImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(saturateImage);
        }
    }

    public void hueImage(double hue) {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.hue(mat, hue);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image hueImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(hueImage);
        }
    }

    public void brightnessImage(double brightness) {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.brightness(mat, brightness);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image brightnessImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(brightnessImage);
        }
    }

    public void sharpnessImage(double sharpness) {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.sharpness(mat, sharpness);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image sharpnessImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(sharpnessImage);
        }
    }

    @FXML
    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        redoStack.push(imageView.getImage());
        imageView.setImage(undoStack.pop());

        redoButton.setDisable(false);
        if (undoStack.isEmpty()) {
            saveButton.setDisable(true);
            undoButton.setDisable(true);
        }
    }

    @FXML
    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        undoStack.push(imageView.getImage());
        imageView.setImage(redoStack.pop());

        undoButton.setDisable(false);
        saveButton.setDisable(false);
        if (redoStack.isEmpty()) {
            redoButton.setDisable(true);
        }
    }

    private void showModal(Node node) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(480);
        borderPane.setPrefHeight(320);
        borderPane.setMaxWidth(480);
        borderPane.setMaxHeight(320);
        borderPane.setStyle("-fx-background-color: -color-bg-default; -fx-border-radius: 4px;");

        Button closeButton = new Button();
        closeButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
        closeButton.setOnAction(event -> modalPane.hide());

        FontIcon closeIcon = new FontIcon("mdoal-close");
        closeIcon.setIconSize(24);
        closeButton.setGraphic(closeIcon);

        borderPane.setTop(closeButton);
        borderPane.setCenter(node);
        modalPane.show(borderPane);
    }

    @FXML
    private void initialize() {
        lowPassButton.getStyleClass().addAll(Styles.FLAT);
        highPassButton.getStyleClass().addAll(Styles.FLAT);

        grayHistogramButton.getStyleClass().addAll(Styles.FLAT);
        grayHistogramSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 16));

        HSVHistogramButton.getStyleClass().addAll(Styles.FLAT);
        HSVHistogramSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 16));

        fourierSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
        fourierSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 128));

        binButton.getStyleClass().addAll(Styles.FLAT);
        binSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 128));

        leftBar.getItems().forEach(item -> item.setDisable(true));

        primaryContainer.getChildren().add(modalPane);

        var contrastSlider = new Slider(0, 100, 50);
        contrastSlider.setDisable(true);
        contrastSlider.setMajorTickUnit(1);
        contrastSlider.setBlockIncrement(1);
        contrastSlider.setMinorTickCount(5);
        contrastSlider.setSnapToTicks(true);
        contrastSlider.setSkin(new ProgressSliderSkin(contrastSlider));
        contrastSlider.getStyleClass().add(Styles.SMALL);
        Text contrastText = new Text("+0.0");
        contrastSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            contrastText.setText(String.format("%+.1f", (newValue.doubleValue() - 50) / 50.0));
        });
        contrastSlider.setOnMouseReleased(event -> {
            double contrast = contrastSlider.getValue() / 50.0;
            contrastImage(contrast);
            contrastSlider.setValue(50);
        });
        contrastSliderWrapper.getChildren().addAll(contrastSlider, contrastText);
        this.contrastSlider = contrastSlider;

        var saturationSlider = new Slider(0, 100, 50);
        saturationSlider.setDisable(true);
        saturationSlider.setMajorTickUnit(1);
        saturationSlider.setBlockIncrement(1);
        saturationSlider.setMinorTickCount(5);
        saturationSlider.setSnapToTicks(true);
        saturationSlider.setSkin(new ProgressSliderSkin(saturationSlider));
        saturationSlider.getStyleClass().add(Styles.SMALL);
        Text saturationText = new Text("+0.0");
        saturationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            saturationText.setText(String.format("%+.1f", (newValue.doubleValue() - 50) / 50.0));
        });
        saturationSlider.setOnMouseReleased(event -> {
            double saturation = saturationSlider.getValue() / 50.0;
            saturateImage(saturation);
            saturationSlider.setValue(50);
        });
        saturationSliderWrapper.getChildren().addAll(saturationSlider, saturationText);
        this.saturationSlider = saturationSlider;

        var hueSlider = new Slider(-180, 180, 0);
        hueSlider.setDisable(true);
        hueSlider.setMajorTickUnit(1);
        hueSlider.setBlockIncrement(1);
        hueSlider.setMinorTickCount(5);
        hueSlider.setSnapToTicks(true);
        hueSlider.setSkin(new ProgressSliderSkin(hueSlider));
        hueSlider.getStyleClass().add(Styles.SMALL);
        Text hueText = new Text("+0.0");
        hueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            hueText.setText(String.format("%+.1f", newValue.doubleValue()));
        });
        hueSlider.setOnMouseReleased(event -> {
            hueImage(hueSlider.getValue());
            hueSlider.setValue(0);
        });
        hueSliderWrapper.getChildren().addAll(hueSlider, hueText);
        this.hueSlider = hueSlider;

        var brightnessSlider = new Slider(-50, 50, 0);
        brightnessSlider.setDisable(true);
        brightnessSlider.setMajorTickUnit(1);
        brightnessSlider.setBlockIncrement(1);
        brightnessSlider.setMinorTickCount(5);
        brightnessSlider.setSnapToTicks(true);
        brightnessSlider.setSkin(new ProgressSliderSkin(brightnessSlider));
        brightnessSlider.getStyleClass().add(Styles.SMALL);
        Text brightnessText = new Text("+0.0");
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            brightnessText.setText(String.format("%+.1f", newValue.doubleValue()));
        });
        brightnessSlider.setOnMouseReleased(event -> {
            brightnessImage(brightnessSlider.getValue());
            brightnessSlider.setValue(0);
        });
        brightnessSliderWrapper.getChildren().addAll(brightnessSlider, brightnessText);
        this.brightnessSlider = brightnessSlider;

        var sharpnessSlider = new Slider(-5, 10, 0);
        sharpnessSlider.setDisable(true);
        sharpnessSlider.setMajorTickUnit(1);
        sharpnessSlider.setBlockIncrement(1);
        sharpnessSlider.setMinorTickCount(5);
        sharpnessSlider.setSnapToTicks(true);
        sharpnessSlider.setSkin(new ProgressSliderSkin(sharpnessSlider));
        sharpnessSlider.getStyleClass().add(Styles.SMALL);
        Text sharpnessText = new Text("+0.0");
        sharpnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sharpnessText.setText(String.format("%+.1f", newValue.doubleValue()));
        });
        sharpnessSlider.setOnMouseReleased(event -> {
            sharpnessImage(sharpnessSlider.getValue());
            sharpnessSlider.setValue(0);
        });
        sharpnessSliderWrapper.getChildren().addAll(sharpnessSlider, sharpnessText);
        this.sharpnessSlider = sharpnessSlider;

        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        imageWrapper.setOnScroll(event -> {
            if (imageView.getImage() == null) {
                return;
            }
            if (event.isControlDown()) {
                double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
                zoomImage(zoomFactor);
            }
        });

    }

    @FXML
    private void minimizeWindow() {
        stage.setIconified(true);
    }

    @FXML
    private void maximizeWindow() {
        if (!isMaximized) {
            lastWidth = stage.getWidth();
            lastHeight = stage.getHeight();
            lastX = stage.getX();
            lastY = stage.getY();

            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(primaryScreenBounds.getMinX());
            stage.setY(primaryScreenBounds.getMinY());

            stage.setMaxWidth(primaryScreenBounds.getWidth());
            stage.setMinWidth(primaryScreenBounds.getWidth());

            stage.setMaxHeight(primaryScreenBounds.getHeight());
            stage.setMinHeight(primaryScreenBounds.getHeight());

            isMaximized = true;
        } else {
            stage.setMaxWidth(lastWidth);
            stage.setMinWidth(lastWidth);
            stage.setMaxHeight(lastHeight);
            stage.setMinHeight(lastHeight);
            stage.setX(lastX);
            stage.setY(lastY);

            isMaximized = false;
        }
    }

    @FXML
    private void closeWindow() {
        stage.close();
    }

    @FXML
    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            resetImageZoom();
            resetUndoAndRedoStacks();
            leftBar.getItems().forEach(item -> item.setDisable(false));
            contrastSlider.setDisable(false);
            saturationSlider.setDisable(false);
            hueSlider.setDisable(false);
            brightnessSlider.setDisable(false);
            sharpnessSlider.setDisable(false);
        }
    }

    @FXML
    private void saveImage() {
        if (imageView.getImage() == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void bwFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bwBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(mat));
            Image bwImage = Utils.bufferedImageToJavafxImage(bwBufferedImage);
            editImage(bwImage);
        }
    }

    @FXML
    private void redFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var bufferRed = Utils.colorFilter(mat, Color.RED);
        System.out.println(bufferRed.channels());
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var redBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(bufferRed));
            Image redImage = Utils.bufferedImageToJavafxImage(redBufferedImage);
            editImage(redImage);
        }
    }

    @FXML
    private void greenFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var bufferRed = Utils.colorFilter(mat, Color.GREEN);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var redBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(bufferRed));
            Image redImage = Utils.bufferedImageToJavafxImage(redBufferedImage);
            editImage(redImage);
        }
    }

    @FXML
    private void blueFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var bufferRed = Utils.colorFilter(mat, Color.BLUE);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var redBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(bufferRed));
            Image redImage = Utils.bufferedImageToJavafxImage(redBufferedImage);
            editImage(redImage);
        }
    }

    @FXML
    private void binFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.threshold(mat, binSpinner.getValue());
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image redImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(redImage);
        }
    }

    @FXML
    private void invertFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var buffer = Utils.invert(mat);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(buffer));
            Image redImage = Utils.bufferedImageToJavafxImage(bufferedImage);
            editImage(redImage);
        }
    }

    @FXML
    private void openGrayHistogram() {
        var image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var histogram = Utils.hist(mat, grayHistogramSpinner.getValue(), new float[] { 0, 255 });
        try {
            var loader = App.loadFXML("grayHistogram");
            Node node = loader.load();
            GrayHistogramController controller = loader.getController();
            controller.setHistogram(histogram);
            controller.start();
            showModal(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHSVHistogram() {
        var image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var histogram = Utils.hsvHist(mat, HSVHistogramSpinner.getValue());
        try {
            var loader = App.loadFXML("hsvHistogram");
            Node node = loader.load();
            HSVHistogramController controller = loader.getController();
            controller.setHistogram(histogram);
            controller.start();
            showModal(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHuMoments() {
        var image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var huMoments = Utils.getAllHuMoments(mat);
        try {
            Text text = new Text();
            text.setText("Hu Moments\n\n");

            text.setText(text.getText() + "Gray: \n");
            for (int i = 0; i < 7; i++) {
                text.setText(text.getText() + "F" + (i + 1) + ": " + huMoments[i] + "\n");
            }
            text.setText(text.getText() + "Canal H: \n");
            for (int i = 7; i < 14; i++) {
                text.setText(text.getText() + "F" + ((i - 6) + 1) + ": " + huMoments[i] + "\n");
            }
            text.setText(text.getText() + "Canal S: \n");
            for (int i = 14; i < 21; i++) {
                text.setText(text.getText() + "F" + ((i - 13) + 1) + ": " + huMoments[i] + "\n");
            }
            text.setText(text.getText() + "Canal V: \n");
            for (int i = 21; i < 28; i++) {
                text.setText(text.getText() + "F" + ((i - 21) + 1) + ": " + huMoments[i] + "\n");
            }
            showModal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}