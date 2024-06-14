package com.boisbarganhados.view;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ChartController {
    
    @FXML
    private LineChart<Number, Number> lineChart;

    public void initialize() {
        // Create sample data series
        XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();
        dataSeries.getData().add(new XYChart.Data<>(1, 23));
        dataSeries.getData().add(new XYChart.Data<>(2, 14));
        dataSeries.getData().add(new XYChart.Data<>(3, 15));
        dataSeries.getData().add(new XYChart.Data<>(4, 24));
        dataSeries.getData().add(new XYChart.Data<>(5, 34));
        
        // Add data series to the chart
        lineChart.getData().add(dataSeries);
        
        // Set chart title
        lineChart.setTitle("Sample Line Chart");
        
        // Set axis labels
        ((NumberAxis) lineChart.getXAxis()).setLabel("X Axis");
        ((NumberAxis) lineChart.getYAxis()).setLabel("Y Axis");
    }

    @FXML
    private void navigateBack() {
        try {
            com.boisbarganhados.Main.setRoot("Home.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}