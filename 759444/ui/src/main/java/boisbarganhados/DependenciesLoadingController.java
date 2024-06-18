package boisbarganhados;

import java.io.IOException;

import boisbarganhados.python_layer.Requirements;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
                    System.exit(1);
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
            System.err.println("Failed to load requirements");
            System.exit(1);
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
