import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class CPUCachePage extends JPanel {

    public CPUCachePage() {
          
        setLayout(new BorderLayout());

          
        setOpaque(false);

        CPU cpu = new CPU();
        JFreeChart chart = createChart(createDataset(cpu.getAllCache()));
        setColours(chart);

          
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);   
        chartPanel.setBackground(new Color(0, 0, 0, 0));   
        chartPanel.setPreferredSize(new Dimension(400, 300));   

        add(chartPanel, BorderLayout.CENTER);
    }

    private static PieDataset createDataset(HashMap<String, Integer> cacheVals) {
        DefaultPieDataset ds = new DefaultPieDataset();
        for (String key : cacheVals.keySet()) {
            ds.setValue(key, cacheVals.get(key));
        }
        return ds;
    }

    private static void setColours(JFreeChart chart) {
        RingPlot plot = (RingPlot) chart.getPlot();
        PieDataset ds = plot.getDataset();

          
        plot.setSectionPaint(ds.getKey(0), new Color(245, 91, 73));
        plot.setSectionPaint(ds.getKey(1), new Color(245, 162, 73));
        plot.setSectionPaint(ds.getKey(2), new Color(75, 189, 227));
        plot.setSectionPaint(ds.getKey(3), new Color(192, 114, 237));

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} Bytes"));
        plot.setSeparatorsVisible(false);

          
        plot.setExplodePercent(ds.getKey(0), 0.15);
        plot.setExplodePercent(ds.getKey(1), 0.0);
        plot.setExplodePercent(ds.getKey(2), 0.15);
        plot.setExplodePercent(ds.getKey(3), 0.0);
    }

    private static JFreeChart createChart(PieDataset ds) {
        return ChartFactory.createRingChart(
                "CPU Caches",
                ds,
                true,    
                false,   
                false    
        );
    }
}
