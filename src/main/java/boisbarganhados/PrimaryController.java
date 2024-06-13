package boisbarganhados;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Data;

import java.io.File;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

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
        var huMoments = Utils.calculateHuMomentsHSV(mat);
        try {
            Text text = new Text();
            for (int i = 0; i < huMoments.length; i++) {
                for (int j = 0; j < huMoments[i].length; j++) {
                    text.setText(text.getText() + huMoments[i][j] + ", ");
                }
                text.setText(text.getText() + "\n");
            }
            showModal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}