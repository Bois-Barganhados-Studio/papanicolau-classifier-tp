package boisbarganhados;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.Data;

@Data
public class HSVHistogramController {
  @FXML
  private LineChart<String, Number> hsvChart;

  private float[][] histogram;

  public void start() {
    // Function to calculate 2D HSV histogram with quantization of 16 values for H
    // and 8 values for V

    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
    for (int h = 0; h < histogram.length; h++) {
      for (int v = 0; v < histogram[h].length; v++) {
        if (histogram[h][v] > 0)
          dataSeries.getData().add(new XYChart.Data<>("H " + h + " V " + v, histogram[h][v]));
      }
    }

    // Add data to chart
    hsvChart.getData().add(dataSeries);

    // Set chart title
    hsvChart.setTitle("Histograma de tons de cor HSV");

    // Set axis labels
    hsvChart.getXAxis().setLabel("H");
    hsvChart.getYAxis().setLabel("V");
  }

}
