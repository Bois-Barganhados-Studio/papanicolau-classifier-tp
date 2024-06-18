package boisbarganhados;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import lombok.Data;

@Data
public class DependenciesLoadingController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private void initialize() {
        progressBar.setProgress(0.0);
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                progressBar.getScene().getWindow().hide();
            }
        });
    }
}
