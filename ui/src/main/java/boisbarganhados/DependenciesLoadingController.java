package boisbarganhados;

import java.io.IOException;

import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import boisbarganhados.python_layer.Requirements;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class DependenciesLoadingController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private void initialize() {
    }

    public void loadDependencies(Stage stage, Scene scene) {
        Task<Void> taskMain = new Task<>() {
            @Override
            protected Void call() {
                if (!Requirements.loadRequirements()) {
                    System.err.println("Failed to load requirements");
                    throw new RuntimeException(
                            "Internal Failure (Python could not install dependencies with PIP install). See logs (cli) for more information.");
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(taskMain.progressProperty());

        taskMain.setOnSucceeded(e -> {
            try {
                startMainScreen(stage, scene);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        taskMain.setOnFailed(e -> {
            var node = new BorderPane();
            var textFlow = new TextFlow();
            node.setPrefWidth(300);
            node.setPrefHeight(200);
            textFlow.getChildren()
                    .add(new Text("Failed to load dependencies. Please check the logs for more information. \nError: "
                            + e.getSource().getException().getMessage()));
            node.setCenter(textFlow);
            FontIcon errorIcon = new FontIcon("mdoal-error");
            errorIcon.setIconSize(50);
            errorIcon.setIconColor(Color.RED);
            node.setLeft(errorIcon);
            Card card = new Card();
            card.setPrefWidth(300);
            card.setPrefHeight(200);
            card.setMaxWidth(450);
            card.setMaxHeight(200);
            card.getStyleClass().add(Styles.ELEVATED_2);
            card.setBody(node);
            Dialog<Void> dialog = new Dialog<Void>();
            dialog.getDialogPane().setContent(card);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.setTitle("Dependencies install error");
            dialog.initOwner(stage);
            dialog.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            dialog.setOnCloseRequest(ev -> {
                System.exit(0);
            });
            dialog.showAndWait();
        });

        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return taskMain;
            }
        };

        service.start();

    }

    public void startMainScreen(Stage stage, Scene scene) throws IOException {
        stage.close();
        stage = new Stage();
        FXMLLoader loader = App.loadFXML("primary");
        scene = new Scene(loader.load(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        PrimaryController controller = loader.getController();
        controller.setStage(stage);
        stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Papanicolau Image Viewer and Classifier");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        stage.setResizable(true);
        stage.show();
        controller.startKeyBindings();
    }
}
