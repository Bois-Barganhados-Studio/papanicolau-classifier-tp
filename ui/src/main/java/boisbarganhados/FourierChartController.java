package boisbarganhados;

import org.bytedeco.opencv.opencv_core.Mat;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.Data;

@Data
public class FourierChartController {
  @FXML
  private LineChart<Number, Number> lineChart;

  private Mat spectrumMat;

  @FXML
  private void initialize() {
  }

  public void start() {

    Task<XYChart.Series<Number, Number>> task = new Task<XYChart.Series<Number, Number>>() {
      @Override
      protected XYChart.Series<Number, Number> call() throws Exception {
        return updateChart();
      }
    };

    Service<XYChart.Series<Number, Number>> service = new Service<XYChart.Series<Number, Number>>() {
      @Override
      protected Task<XYChart.Series<Number, Number>> createTask() {
        return task;
      }
    };

    task.onSucceededProperty().set(event -> {
      lineChart.getData().add(task.getValue());

      lineChart.getXAxis().setLabel("Frequencia");
      lineChart.getYAxis().setLabel("Magnitude");

      // remove dots from line chart just keep the lines
      lineChart.setCreateSymbols(false);
      lineChart.setLegendVisible(false);
    });

    service.start();

  }

  private XYChart.Series<Number, Number> updateChart() {
    lineChart.setTitle("Fourier Spectrum (Magnitude)");
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName("Magnitude Spectrum");

    // Get the Fourier Spectrum
    try {
      int rows = spectrumMat.rows();
      int cols = spectrumMat.cols();

      // Convert Mat to 2D array
      double[][] data = Utils.matTo2DArray(spectrumMat);

      // Determine sampling step for rows and columns
      int passRow = rows < 100 ? 1 : rows < 2000 ? rows / 100 : rows / 200;
      int passCol = cols < 100 ? 1 : cols < 2000 ? cols / 100 : cols / 200;

      // Add data points to the series with log transformation
      for (int i = 0; i < rows; i += passRow) {
        for (int j = 0; j < cols; j += passCol) {
          double magnitude = data[i][j];
          double logMagnitude = Math.log(magnitude + 1); // Log transform, adding 1 to avoid log(0)
          series.getData().add(new XYChart.Data<>(i, logMagnitude));
        }
      }

    } catch (Exception e) {
      // Log or print the stack trace for debugging purposes
      e.printStackTrace();
      // Optionally, you could set the series name to indicate an error
      series.setName("Error: Unable to process data");
    }

    return series;
  }
}
