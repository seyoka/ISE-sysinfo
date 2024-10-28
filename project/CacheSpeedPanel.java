import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import com.sun.jna.*;
import java.io.*;


public class CacheSpeedPanel extends JPanel {
    private static final int BALL_SIZE = 20;
    private Map<String, Ball> balls = new HashMap<>();
    private Timer animationTimer;
    
    // Colors for different cache levels
    private static final Color L1_COLOR = new Color(144, 238, 144);  // Light green
    private static final Color L2_COLOR = new Color(0, 0, 255);      // Blue
    private static final Color L3_COLOR = new Color(128, 0, 128);    // Purple
    private static final Color RAM_COLOR = new Color(255, 0, 0);     // Red
    
    private class Ball {
        double x = 50;  // Starting position
        final int y;    // Vertical position
        final double speed;
        final Color color;
        final String label;
        final String latency;
        
        Ball(int y, double speed, Color color, String label, String latency) {
            this.y = y;
            this.speed = speed;
            this.color = color;
            this.label = label;
            this.latency = latency;
        }
    }
    
    // In your CacheSpeedPanel constructor
        public CacheSpeedPanel() {
            setBackground(new Color(41, 41, 66));
            setPreferredSize(new Dimension(800, 300));
            setBorder(BorderFactory.createLineBorder(new Color(75, 75, 96), 1));
            
            // Get real latencies
            Map<String, Double> latencies = CacheLatencyTest.measureCacheLatencies();
            
            // Create balls with proper spacing
            int startY = 50;
            int spacing = 50;
            
            // Use inverse of latency for speed (faster latency = faster ball)
            // Scale factor to make the animation visible but not too fast
            double scaleFactor = 3.0;
            
            // Calculate speeds based on actual latencies
            double l1Speed = scaleFactor * (1.0 / latencies.get("L1"));
            double l2Speed = scaleFactor * (1.0 / latencies.get("L2"));
            double l3Speed = scaleFactor * (1.0 / latencies.get("L3"));
            double ramSpeed = scaleFactor * (1.0 / latencies.get("RAM"));

            balls.put("L1", new Ball(startY, l1Speed, L1_COLOR, "L1 Cache", 
                    String.format("%.2f ns", latencies.get("L1"))));
            balls.put("L2", new Ball(startY + spacing, l2Speed, L2_COLOR, "L2 Cache", 
                    String.format("%.2f ns", latencies.get("L2"))));
            balls.put("L3", new Ball(startY + spacing * 2, l3Speed, L3_COLOR, "L3 Cache", 
                    String.format("%.2f ns", latencies.get("L3"))));
            balls.put("RAM", new Ball(startY + spacing * 3, ramSpeed, RAM_COLOR, "RAM", 
                    String.format("%.2f ns", latencies.get("RAM"))));
            
            // Start animation
            animationTimer = new Timer(16, e -> {
                for (Ball ball : balls.values()) {
                    ball.x += ball.speed;
                    if (ball.x > getWidth() - BALL_SIZE) {
                        ball.x = 50;
                    }
                }
                repaint();
            });
            animationTimer.start();
        }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw title
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Memory Access Speed Visualization", 10, 30);
        
        // Draw CPU box
        g2d.setColor(new Color(173, 216, 230));
        g2d.fillRect(10, 50, 30, 200);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("CPU", 10, 270);
        
        // Draw legend and balls
        int legendX = getWidth() - 200;
        int legendY = 50;
        int legendSpacing = 25;
        
        for (Ball ball : balls.values()) {
            // Draw ball
            g2d.setColor(ball.color);
            g2d.fillOval((int)ball.x, ball.y, BALL_SIZE, BALL_SIZE);
            
            // Draw legend
            g2d.fillOval(legendX, legendY, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.format("%s (%s)", ball.label, ball.latency), 
                         legendX + 20, legendY + 10);
            legendY += legendSpacing;
        }
    }
    
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    // Test method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cache Speed Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        CacheSpeedPanel panel = new CacheSpeedPanel();
        frame.add(panel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

