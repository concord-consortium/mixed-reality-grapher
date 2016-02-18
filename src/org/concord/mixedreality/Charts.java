package org.concord.mixedreality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Saeid Nourian <snourian@concord.org>
 */
public class Charts {

    public static void createCharts(final File file, final VBox chartsPanel) {
        final String[] yAxisNames = {"", "Temperature", "Pressure", "Force", "Volume", "# of Molecules"};
        final String[] chartColors = {"", "red", "blue", "green", "purple", "orange"};

        final List<Number[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll(" +", " ");
                final String[] numbers = line.split(" ");
                final int n = Math.min(numbers.length, yAxisNames.length);
                final Number[] row = new Number[n];
                for (int i = 0; i < n; i++)
                    try {
                        row[i] = Double.parseDouble(numbers[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Number: " + numbers[i]);
                    }
                data.add(row);
            }
        } catch (final Exception e) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, e);
        }

        chartsPanel.getChildren().clear();
        for (int chartIndex = 1; chartIndex < data.get(0).length; chartIndex++) {
            final ObservableList<XYChart.Data<Number, Number>> serieData = FXCollections.<XYChart.Data<Number, Number>>observableArrayList();
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++)
                serieData.add(new XYChart.Data<>(data.get(rowIndex)[0], data.get(rowIndex)[chartIndex]));
            final XYChart.Series series = new XYChart.Series(serieData);

            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(yAxisNames[chartIndex]);
            final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setLegendVisible(false);
            chart.setCreateSymbols(false);
            chart.setAnimated(false);
            chart.getData().add(series);

            series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: " + chartColors[chartIndex] + ";");

            VBox.setVgrow(chart, Priority.ALWAYS);
            chartsPanel.getChildren().add(chart);

            addScrollAndMouseDragListeners(chart, xAxis, chartsPanel);
        }

        // align the vertical line of y axis of all charts
        Platform.runLater(() -> {
            double maxWidthYAxis = 0;
            for (Node chart : chartsPanel.getChildren())
                maxWidthYAxis = Math.max(maxWidthYAxis, ((LineChart) chart).getYAxis().getWidth());

            for (Node chart : chartsPanel.getChildren())
                ((LineChart) chart).getYAxis().setPrefWidth(maxWidthYAxis);
        });
    }

    private static void addScrollAndMouseDragListeners(final LineChart<Number, Number> chart, final NumberAxis xAxis, final VBox chartsPanel) {
        chart.setOnScroll((ScrollEvent event) -> {
            event.consume();
            if (event.getDeltaY() == 0)
                return;
            final double SCALE_DELTA = 50;
            double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : -SCALE_DELTA;
            double delta = (xAxis.getUpperBound() - xAxis.getLowerBound()) / scaleFactor;
            updateXAxisBounds(xAxis.getLowerBound() + delta, xAxis.getUpperBound() - delta, chartsPanel);
        });

        final double[] startValues = new double[3];

        chart.setOnMousePressed((MouseEvent event) -> {
            event.consume();
            startValues[0] = event.getX();
            startValues[1] = xAxis.getLowerBound();
            startValues[2] = xAxis.getUpperBound();
        });

        chart.setOnMouseDragged((MouseEvent event) -> {
            event.consume();
            final double unitPerPixel = (xAxis.getUpperBound() - xAxis.getLowerBound()) / xAxis.getWidth();
            final double dx = (event.getX() - startValues[0]) * unitPerPixel;
            updateXAxisBounds(startValues[1] - dx, startValues[2] - dx, chartsPanel);
        });
    }

    private static void updateXAxisBounds(double lowerBound, double upperBound, final VBox chartsPanel) {
        for (final Node child : chartsPanel.getChildren()) {
            final NumberAxis axis = (NumberAxis) ((LineChart) child).getXAxis();
            axis.setAutoRanging(false);
            axis.setLowerBound(lowerBound);
            axis.setUpperBound(upperBound);
        }
    }
}
