package boisbarganhados;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;

import java.io.File;

@Data
public class PrimaryController {
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    private final ModalPane modalPane = new ModalPane();

    @FXML
    private StackPane primaryContainer;
    @FXML
    private ImageView imageView;
    @FXML
    private ToolBar topBar;
    @FXML
    private BorderPane imageWrapper;
    @FXML
    private ScrollPane scrollPane;

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

    public void resetImage() {
        imageView.setScaleX(1);
        imageView.setScaleY(1);
        zoomImage(1);
    }

    public void setImage(Image image) {
        imageView.setImage(image);
        resetImage();
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
        primaryContainer.getChildren().add(modalPane);

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
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
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
            setImage(image);
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
            imageView.setImage(bwImage);
        }
    }

    @FXML
    private void openGrayHistogram() {
        var image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        var histogram = Utils.hist(mat, 16, new float[] { 0, 255 });
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
        var histogram = Utils.hsvHist(mat);
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