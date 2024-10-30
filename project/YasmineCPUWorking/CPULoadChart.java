import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

public class CPULoadChart extends JPanel {
    private LinkedList<Integer> dataPoints = new LinkedList<>();
    private static final int MAX_POINTS = 30;
    private static final int CHART_WIDTH = 800;
    private static final int CHART_HEIGHT = 600;
    private static final int Y_MAX = 100;   
    private static final int Y_MIN = -20;   

    private final Color BACKGROUND_COLOR = new Color(30, 30, 47);
    private final Color BORDER_COLOR = new Color(75, 75, 96);

    public CPULoadChart() {
        setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
        setBackground(BACKGROUND_COLOR);

        goTimer(new CPU());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAxes(g);
        drawLineGraph(g);
    }

    private void drawAxes(Graphics g) {
        g.setColor(BORDER_COLOR);
          
        g.drawLine(60, CHART_HEIGHT / 2, CHART_WIDTH - 40, CHART_HEIGHT / 2);
          
        g.drawLine(60, 20, 60, CHART_HEIGHT - 20);

          
        for (int i = Y_MIN; i <= Y_MAX; i += 20) {
            int y = CHART_HEIGHT / 2 - (i * (CHART_HEIGHT / 2 - 40) / (Y_MAX - Y_MIN));
            g.drawLine(60, y, CHART_WIDTH - 40, y);
            g.drawString(String.valueOf(i), 10, y + 5);   
        }

          
        for (int i = 0; i <= MAX_POINTS; i += 20) {
            int x = 40 + (i * (CHART_WIDTH - 60) / MAX_POINTS);
            g.drawLine(x, CHART_HEIGHT / 2 - 5, x, CHART_HEIGHT / 2 + 5);
        }
    }

    private void drawLineGraph(Graphics g) {
        if (dataPoints.size() < 2) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(56, 107, 201));
        g2d.setStroke(new BasicStroke(2f));   
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);   

        int prevX = 60;
        int prevY = CHART_HEIGHT / 2 - (dataPoints.get(0) * (CHART_HEIGHT / 2 - 40) / (Y_MAX - Y_MIN));

        for (int i = 1; i < dataPoints.size(); i++) {
            int x = 60 + (i * (CHART_WIDTH - 100) / MAX_POINTS);
            int y = CHART_HEIGHT / 2 - (dataPoints.get(i) * (CHART_HEIGHT / 2 - 40) / (Y_MAX - Y_MIN));
            g2d.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

      
    private void goTimer(CPU cpu) {
        Timer timer = new Timer(100, e -> {
            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() {
                    int load = (int) cpu.totalSocketLoad();
                    dataPoints.add(load);
                    if (dataPoints.size() > MAX_POINTS) {
                        dataPoints.removeFirst();   
                    }
                    repaint();
                    return null;
                }
            };

            worker.execute();
        });
        timer.start();
    }
}
