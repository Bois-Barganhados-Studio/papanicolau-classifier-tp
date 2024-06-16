package boisbarganhados;

import org.bytedeco.opencv.opencv_core.Mat;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.Data;

@Data
public class FourierChartController {
  @FXML
  private LineChart<Number, Number> lineChart;

  private Mat yourImageMat;

  @FXML
  private void initialize() {
  }

  public void start() {

    lineChart.setTitle("Fourier Spectrum");

    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName("Magnitude Spectrum");

    // Get the Fourier Spectrum
    Mat spectrum = Utils.getFourierSpectrum(yourImageMat); // Sua imagem Mat

    // Convert Mat to 2D array and populate the series
    int rows = spectrum.rows();
    int cols = spectrum.cols();

    var data = new double[rows][cols];

    data = Utils.matTo2DArray(spectrum);

    for (int i = 0; i < rows; i += 25) {
      for (int j = 0; j < cols; j += 8) {
        series.getData().add(new XYChart.Data<>(i, data[i][j]));
      }
    }

    lineChart.getData().add(series);

    lineChart.getXAxis().setLabel("Frequency");
    lineChart.getYAxis().setLabel("Magnitude");

    // remove dots from line chart just keep the lines
    lineChart.setCreateSymbols(false);
    lineChart.setLegendVisible(false);
  }
}
