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

      lineChart.getXAxis().setLabel("Frequency");
      lineChart.getYAxis().setLabel("Magnitude");

      // remove dots from line chart just keep the lines
      lineChart.setCreateSymbols(false);
      lineChart.setLegendVisible(false);
    });

    service.start();

  }

  private XYChart.Series<Number, Number> updateChart() {
    lineChart.setTitle("Fourier Spectrum");

    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName("Magnitude Spectrum");

    // Get the Fourier Spectrum
    try {
      int rows = spectrumMat.rows();
      int cols = spectrumMat.cols();

      var data = new double[rows][cols];

      data = Utils.matTo2DArray(spectrumMat);

      for (int i = 0; i < rows; i += 25) {
        for (int j = 0; j < cols; j += 8) {
          series.getData().add(new XYChart.Data<>(i, data[i][j]));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return series;

  }
}
