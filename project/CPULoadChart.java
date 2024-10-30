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

public class CPULoadChart extends JPanel {

    private XYSeries series;

    public CPULoadChart() {
        setLayout(new BorderLayout());

          
        CPU cpu = new CPU();
        XYSeriesCollection dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        add(chartPanel, BorderLayout.CENTER);

          
        goTimer(cpu);

    }

    private void goTimer(CPU cpu){
          
        Timer timer = new Timer(16, new ActionListener() {
            private int time = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                  
                series.add(time++, cpu.totalSocketLoad());
                  
                if (series.getItemCount() > 20) {
                    series.remove(0);   
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

          
        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(false);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(false);

        return chart;
    }
}