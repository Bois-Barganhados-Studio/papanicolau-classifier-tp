package boisbarganhados;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import atlantafx.base.theme.PrimerDark;
import boisbarganhados.python_layer.Requirements;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        FXMLLoader loader = loadFXML("primary");
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
        if (!Requirements.loadRequirements()) {
            System.err.println("Failed to load requirements");
            System.exit(1);
        }
        stage.show();
        controller.startKeyBindings();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml).load());
    }

    public static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }

}