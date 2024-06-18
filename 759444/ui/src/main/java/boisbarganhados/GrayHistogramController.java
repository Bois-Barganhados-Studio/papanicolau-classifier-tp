package boisbarganhados;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import lombok.Data;

@Data
public class GrayHistogramController {
  @FXML
  private BarChart<String, Number> barChart;

  private float[] histogram;

  @FXML
  private void initialize() {
  }

  public void start() {
    // craeate chart data
    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
    for (int i = 0; i < histogram.length; i++) {
      if (histogram[i] > 0)
        dataSeries.getData().add(new XYChart.Data<>(" " + i, histogram[i]));
    }

    // Add data to chart
    barChart.getData().add(dataSeries);

    // Set chart title
    barChart.setTitle("Histograma de tons de cinza");

    // Set axis labels
    barChart.getXAxis().setLabel("Tom de cinza");
    barChart.getYAxis().setLabel("Ocorrências");
  }
}
