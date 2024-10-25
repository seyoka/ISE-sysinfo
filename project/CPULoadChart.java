//To make the area chart update in real time, you can use a timer to periodically
//generate new data points and update the dataset.

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CPULoadChart extends JFrame {

    private XYSeries series;

    public CPULoadChart(String title) {
        super(title);
        CPU cpu = new CPU();
        XYSeriesCollection dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
        goTimer(cpu);

    }

//use Dynamic time series instead?
    private void goTimer(CPU cpu){
        //updates every 1 second
        Timer timer = new Timer(1, new ActionListener() {
            private int time = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Update data point
                series.add(time++, cpu.totalSocketLoad()); // Simulated Load data
                // Limits the number displayed
                if (series.getItemCount() > 20) {
                    series.remove(0); // Remove the oldest point
                }
            }
        });
        timer.start();
    }

    private XYSeriesCollection createDataset() {
        series = new XYSeries("Load");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }


    private JFreeChart createChart(XYSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createXYAreaChart(
                "CPU Load Chart",
                "Time",
                "Load (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize the chart (optional)
        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(false);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(false);

        return chart;
    }

    public static void main(String[] args) {

    }
}