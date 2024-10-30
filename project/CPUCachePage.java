import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class CPUCachePage extends JPanel {
    private final LinkedHashMap<String, Integer> vals;
    private final Color[] colors;

    private final Color BACKGROUND_COLOR = new Color(30, 30, 47);

    public CPUCachePage() {
        CPU cpu = new CPU();
        this.vals = cpu.getAllCache();
        this.colors = new Color[]{new Color(245, 91, 73),
                new Color(245, 162, 73),
                new Color(75, 189, 227),
                new Color(192, 114, 237)
        };
        setPreferredSize(new Dimension(800, 600));
        setBackground(BACKGROUND_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        drawPieChart(g);
    }

    private void drawPieChart(Graphics g){
        //adding total for pie chart to read
        int total=0;
        for(int value: vals.values()){
            total+= value;
        }

        //Draw pie chart angles
        int startAngle = 0;
        int minAngle = 10; //for small cache
        int totalAngle = 360;
        int i=0;
        for(int value: vals.values()){
            int angle = (int)Math.round((double) value / total * totalAngle);
            System.out.println(angle);
            // Use minimum angle for small segments
            if (angle < minAngle) {
                angle = minAngle;
                totalAngle -= 10;
            }

            g.setColor(colors[i % colors.length]);
            System.out.println("angle = " + angle);
            g.fillArc(100, 100, 400, 400, startAngle, angle);
            startAngle += angle;
            i++;
        }

        if (startAngle < 360) {
            g.setColor(colors[i % colors.length]); // Use next color for the fill, if needed
            g.fillArc(100, 100, 400, 400, startAngle, 360 - startAngle);
        }

        //legend
        int j=0;
        for (String labels: vals.keySet()){
            g.setColor(colors[j % colors.length]);
            g.fillRect(520, 100 + (j*30), 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(labels + ": " + vals.get(labels) + " Bytes", 550, 115 + (j*30));
            j++;
        }
    }
}
