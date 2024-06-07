package com.boisbarganhados.view;


import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistogramController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Button backButton;

    private float[] histogram;

    @FXML
    private void initialize() {
        backButton.setOnAction(event -> navigateBack());
    }

    private void navigateBack() {
        try {
            com.boisbarganhados.Main.setRoot("Home.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting");

        // craeate chart data
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        for (int i = 0; i < histogram.length; i++) {
            dataSeries.getData().add(new XYChart.Data<>("Cinza " + i, histogram[i]));
        }

        // Add data to chart
        barChart.getData().add(dataSeries);

        // Set chart title
        barChart.setTitle("Histogram");

        // Set axis labels
        barChart.getXAxis().setLabel("Intensity");
        barChart.getYAxis().setLabel("Frequency");
    }

}
