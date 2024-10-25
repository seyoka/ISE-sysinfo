import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class CPULoadChart extends JFrame {

    public CPULoadChart(String title) {
        super(title);

        // Create dataset
        XYSeriesCollection dataset = createDataset();

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "CPU Load Over Time",
                "Time (seconds)",
                "CPU Load (%)",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Orientation
                true,                  // Include legend
                true,                  // Tooltips
                false                  // URLs
        );

        // Create Panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private XYSeriesCollection createDataset() {
        XYSeries series1 = new XYSeries("CPU Load 1");
        XYSeries series2 = new XYSeries("CPU Load 2");

        // Adding some sample data
        for (int i = 0; i <= 10; i++) {
            series1.add(i, Math.random() * 100); // Random data for series 1
            series2.add(i, Math.random() * 100); // Random data for series 2
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }

    public static void main(String[] args) {

    }
}
