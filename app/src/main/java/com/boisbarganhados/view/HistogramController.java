package com.boisbarganhados.view;


import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
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
    private LineChart<String, Number> hsvChart;

    @FXML
    private Button backButton;

    private float[] histogram;

    private float[][] hsvHistogram;

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

    public void startHsv(){
        // Function to calculate 2D HSV histogram with quantization of 16 values for H
    // and 8 values for V
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        for (int h = 0; h < hsvHistogram.length; h++) {
            for (int v = 0; v < hsvHistogram[h].length; v++) {
                dataSeries.getData().add(new XYChart.Data<>("H " + h + " V " + v, hsvHistogram[h][v]));
            }
        }

        // Add data to chart
        hsvChart.getData().add(dataSeries);

        // Set chart title
        hsvChart.setTitle("HSV Histogram");

        // Set axis labels
        hsvChart.getXAxis().setLabel("H");
        hsvChart.getYAxis().setLabel("V");
    }

}
