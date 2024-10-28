
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class CacheSpeedPanel extends JPanel {
    private static final int BALL_SIZE = 20;
    private static final int BOX_WIDTH = 80;
    private static final int BOX_HEIGHT = 60;
    private Map<String, Ball> balls = new HashMap<>();
    private Timer animationTimer;
    private Map<String, Double> latencies;
    
    private static final Color L1_COLOR = new Color(144, 238, 144);  // Light green
    private static final Color L2_COLOR = new Color(0, 0, 255);      // Blue
    private static final Color L3_COLOR = new Color(128, 0, 128);    // Purple
    private static final Color RAM_COLOR = new Color(255, 0, 0);     // Red
    
    private class Ball {
        double x;
        final int y;
        double speed;
        final Color color;
        final String label;
        final String latency;
        boolean movingRight = true;
        
        Ball(int y, double speed, Color color, String label, String latency) {
            this.x = 50;  // Start position
            this.y = y;
            this.speed = speed;
            this.color = color;
            this.label = label;
            this.latency = latency;
        }
    }
    
    public CacheSpeedPanel() {
        setBackground(new Color(41, 41, 66));
        setPreferredSize(new Dimension(800, 300));
        setBorder(BorderFactory.createLineBorder(new Color(75, 75, 96), 1));
        
        // Get real latencies
        latencies = CacheLatencyTest.measureCacheLatencies();
        
        // Create balls with proper spacing
        int startY = 50;
        int spacing = 50;
        
        // Calculate speeds based on latencies
        double scaleFactor = 3.0;
        double l1Speed = scaleFactor * (1.0 / latencies.get("L1"));
        double l2Speed = scaleFactor * (1.0 / latencies.get("L2"));
        double l3Speed = scaleFactor * (1.0 / latencies.get("L3"));
        double ramSpeed = scaleFactor * (1.0 / latencies.get("RAM"));
        
        // Create balls
        balls.put("L1", new Ball(startY, l1Speed, L1_COLOR, "L1 Cache", 
                  String.format("%.2f ns", latencies.get("L1"))));
        balls.put("L2", new Ball(startY + spacing, l2Speed, L2_COLOR, "L2 Cache", 
                  String.format("%.2f ns", latencies.get("L2"))));
        balls.put("L3", new Ball(startY + spacing * 2, l3Speed, L3_COLOR, "L3 Cache", 
                  String.format("%.2f ns", latencies.get("L3"))));
        balls.put("RAM", new Ball(startY + spacing * 3, ramSpeed, RAM_COLOR, "RAM", 
                  String.format("%.2f ns", latencies.get("RAM"))));
        
        // Animation timer
        animationTimer = new Timer(16, e -> {
            int boxX = getWidth() - 150;  // Calculate target X position dynamically
            
            for (Ball ball : balls.values()) {
                if (ball.movingRight) {
                    ball.x += ball.speed;
                    if (ball.x >= boxX - BALL_SIZE) {
                        ball.movingRight = false;
                    }
                } else {
                    ball.x -= ball.speed;
                    if (ball.x <= 50) {
                        ball.movingRight = true;
                    }
                }
            }
            repaint();
        });
        
        // Start animation when panel is shown
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    animationTimer.start();
                } else {
                    animationTimer.stop();
                }
            }
        });
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
        
        // Draw memory boxes and balls
        int rightMargin = 150;
        int boxX = getWidth() - rightMargin;
        
        for (Ball ball : balls.values()) {
            // Draw memory box
            g2d.setColor(ball.color);
            g2d.fillRect(boxX, ball.y, BOX_WIDTH, BOX_HEIGHT);
            g2d.setColor(Color.WHITE);
            g2d.drawString(ball.label, boxX + 5, ball.y + BOX_HEIGHT/2);
            
            // Draw ball
            g2d.setColor(ball.color);
            g2d.fillOval((int)ball.x, ball.y + BOX_HEIGHT/2 - BALL_SIZE/2, 
                        BALL_SIZE, BALL_SIZE);
            
            // Draw latency
            g2d.setColor(Color.WHITE);
            g2d.drawString(ball.latency, boxX + BOX_WIDTH + 10, 
                         ball.y + BOX_HEIGHT/2);
        }
    }
    
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}
