package boisbarganhados;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.kordamp.ikonli.javafx.FontIcon;
import atlantafx.base.controls.Card;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Data;

import java.io.File;
import java.util.Stack;
import javax.imageio.ImageIO;

@Data
public class PrimaryController {
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private ProgressIndicator indicator;

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
    public void openRepo() {
        try {
            java.awt.Desktop.getDesktop()
                    .browse(new java.net.URI("https://github.com/Bois-Barganhados-Studio/pai-tp1-papanicolau"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openAboutDialog() {

        var card1 = new Card();
        card1.getStyleClass().add(Styles.ELEVATED_2);
        card1.setMinWidth(300);
        card1.setMaxWidth(300);
        card1.setMaxHeight(300);

        var header1 = new Tile(
                "Trabalho Prático 1 - Papanicolau",
                "Analisa e classificação de imagens de Papanicolau com técnicas de processamento de imagens.");
        card1.setHeader(header1);

        var text1 = new TextFlow(new Text("\n\n" +
                "Desenvolvido por:\n" +
                "  - Edmar Melandes\n" +
                "  - Leon Junio Martins\n" +
                "  - Felipe Aguilar Moura\n\n" +
                "Disciplina: Processamento de Imagens\n" +
                "Professor: Alexei Machado \n" +
                "PUC Minas - Praça da Liberdade\n" +
                "2024/1"));
        text1.setMaxWidth(260);
        card1.setBody(text1);

        showModal(card1, 300, 300);
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
        BorderPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        borderPane.setCenter(node);
        modalPane.show(borderPane);
    }

    private void showModal(Node node, double width, double height) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(width);
        borderPane.setPrefHeight(height);
        borderPane.setMaxWidth(width);
        borderPane.setMaxHeight(height);
        borderPane.setStyle("-fx-background-color: -color-bg-default; -fx-border-radius: 4px;");
        Button closeButton = new Button();
        closeButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
        closeButton.setOnAction(event -> modalPane.hide());
        FontIcon closeIcon = new FontIcon("mdoal-close");
        closeIcon.setIconSize(24);
        closeButton.setGraphic(closeIcon);
        Button detachButton = new Button();
        detachButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
        detachButton.setOnAction(event -> {
            modalPane.hide();
            showDialog(node);
        });
        FontIcon detachIcon = new FontIcon("mdi2d-dock-window");
        detachIcon.setIconSize(24);
        detachButton.setGraphic(detachIcon);
        Tooltip tooltip = new Tooltip("Abrir em nova janela");
        tooltip.setHideDelay(Duration.seconds(1));
        detachButton.setTooltip(tooltip);
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(detachButton, closeButton);
        buttonBox.setSpacing(8);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        borderPane.setTop(buttonBox);
        borderPane.setCenter(node);
        modalPane.show(borderPane);
    }

    private void showDialog(Node node) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(node);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UNIFIED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.show();
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

        var contextMenu = new javafx.scene.control.ContextMenu();
        var zoomInMenuItem = new MenuItem("Zoom In");
        zoomInMenuItem.setOnAction(event -> zoomImage(1.1));
        zoomInMenuItem.setGraphic(new FontIcon("mdi2m-magnify-plus"));
        var zoomOutMenuItem = new MenuItem("Zoom Out");
        zoomOutMenuItem.setOnAction(event -> zoomImage(0.9));
        zoomOutMenuItem.setGraphic(new FontIcon("mdi2m-magnify-minus"));
        var undoMenuItem = new MenuItem("Desfazer");
        undoMenuItem.setOnAction(event -> undo());
        undoMenuItem.setGraphic(new FontIcon("mdi2u-undo"));
        var redoMenuItem = new MenuItem("Refazer");
        redoMenuItem.setOnAction(event -> redo());
        redoMenuItem.setGraphic(new FontIcon("mdi2r-redo"));
        var save = new MenuItem("Salvar");
        save.setOnAction(event -> saveImage());
        save.setGraphic(new FontIcon("mdi2c-content-save"));
        contextMenu.getItems().addAll(zoomInMenuItem, zoomOutMenuItem, new SeparatorMenuItem(), undoMenuItem,
                redoMenuItem, new SeparatorMenuItem(), save);
        imageView.setOnContextMenuRequested(
                event -> contextMenu.show(imageView, event.getScreenX(), event.getScreenY()));
    }

    @FXML
    private void minimizeWindow() {
        stage.setIconified(true);
    }


    public void startKeyBindings() {
        var scene = stage.getScene();
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                undo();
                event.consume();
            } else if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                redo();
                event.consume();
            }
        });
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
        var pane = new BorderPane();
        pane.setPrefWidth(220);
        pane.setPrefHeight(160);
        pane.setMaxWidth(220);
        pane.setMaxHeight(160);
        pane.setStyle("-fx-background-color: -color-bg-default");
        var text = new Text(
                "Você tem certeza que deseja sair do programa?\n Todas as alterações não salvas serão perdidas.");
        var button = new Button("Sair do programa");
        button.getStyleClass().addAll(Styles.DANGER, Styles.INTERACTIVE);
        button.setOnAction(event -> stage.close());
        pane.setCenter(text);
        pane.setBottom(button);
        BorderPane.setAlignment(button, javafx.geometry.Pos.CENTER);
        BorderPane.setAlignment(text, javafx.geometry.Pos.CENTER);
        showModal(pane);
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
    private void highPassFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        mat = Utils.applyFourierFilter(mat, fourierSpinner.getValue(), true);
        try (Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                OpenCVFrameConverter.ToMat openCVFrameConverter = new OpenCVFrameConverter.ToMat()) {
            var bwBufferedImage = java2DFrameConverter.convert(openCVFrameConverter.convert(mat));
            Image bwImage = Utils.bufferedImageToJavafxImage(bwBufferedImage);
            editImage(bwImage);
        }
    }

    @FXML
    private void lowPassFilter() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        var mat = Utils.toMat(image);
        mat = Utils.applyFourierFilter(mat, fourierSpinner.getValue(), false);
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
            showModal(node, 550, 500);
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
            showModal(node, 630, 500);
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
            Card card1 = new Card();
            var textx = "Hu Moments\nMomentos invariantes de Hu para a imagem em 256 tons de cinza e para os 3 canais\r\n"
                    + "originais do modelo HSV (4*7 características)";
            var textFlow = new TextFlow(new Text(textx));
            textFlow.setMinHeight(100);
            VBox.setVgrow(textFlow, Priority.ALWAYS);
            var vbox = new VBox(textFlow);
            var text = "";
            for (int i = 0; i < 7; i++) {
                text = (text + "F" + (i + 1) + ": " + huMoments[i] + "\n");
            }
            var tp1 = new TitledPane("Cinza", new Text(text));
            text = "";
            for (int i = 7; i < 14; i++) {
                text = (text + "F" + ((i - 6) + 1) + ": " + huMoments[i] + "\n");
            }
            var tp2 = new TitledPane("Canal H", new Text(text));
            text = "";
            for (int i = 14; i < 21; i++) {
                text = (text + "F" + ((i - 13) + 1) + ": " + huMoments[i] + "\n");
            }
            var tp3 = new TitledPane("Canal S", new Text(text));
            text = "";
            for (int i = 21; i < 28; i++) {
                text = (text + "F" + ((i - 21) + 1) + ": " + huMoments[i] + "\n");
            }
            var tp4 = new TitledPane("Canal V", new Text(text));
            text = "";
            var accordion = new Accordion(tp1, tp2, tp3, tp4);
            vbox.getChildren().add(accordion);
            ScrollPane scrollPane = new ScrollPane(vbox);
            scrollPane.setFitToWidth(true);
            card1.setBody(scrollPane);
            card1.getStyleClass().add(Styles.ELEVATED_2);
            card1.setMinWidth(300);
            showModal(card1, 450, 450);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}