import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class GeneralInfoPanel extends JPanel {
    private JPanel systemInfoPanel;
    private CacheSpeedPanel cachePanel;
    private final Color MAIN_CONTENT_COLOR = new Color(47, 47, 68);
    private DecimalFormat df = new DecimalFormat("#.##");
    private JLabel osLabel;
    private JLabel cpuLabel;
    private JLabel memoryLabel;
    private JLabel powerLabel;
    
    public GeneralInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_CONTENT_COLOR);
        
        // Create top panel for system info
        systemInfoPanel = createSystemInfoPanel();
        
        // Create cache visualization panel
        cachePanel = new CacheSpeedPanel();
        
        // Create a container panel with vertical BoxLayout
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(MAIN_CONTENT_COLOR);
        
        // Add panels to container
        containerPanel.add(systemInfoPanel);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        containerPanel.add(cachePanel);
        
        // Add container to main panel
        add(containerPanel, BorderLayout.NORTH);
        
        // Start timer to update system info
        Timer timer = new Timer(2000, e -> updateSystemInfo());
        timer.start();
    }
    
    private JPanel createSystemInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBackground(new Color(41, 41, 66));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(75, 75, 96), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(800, 130));
        
        osLabel = createStyledLabel("Operating System: Loading...");
        cpuLabel = createStyledLabel("CPU Usage: Loading...");
        memoryLabel = createStyledLabel("Memory Usage: Loading...");
        powerLabel = createStyledLabel("Power Status: Loading...");
        
        panel.add(osLabel);
        panel.add(cpuLabel);
        panel.add(memoryLabel);
        panel.add(powerLabel);
        
        return panel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
    
    private void updateSystemInfo() {
        try {
            // Get OS information
            String osInfo = System.getProperty("os.name") + " " + 
                          System.getProperty("os.version");
            
            // Get CPU Usage (Linux-specific)
            double cpuUsage = getLinuxCpuUsage();
            
            // Get Memory Info (Linux-specific)
            double[] memInfo = getLinuxMemoryInfo();
            
            // Get Power/Battery Info (Linux-specific)
            String powerInfo = getLinuxPowerInfo();
            
            // Update labels
            osLabel.setText("Operating System: " + osInfo);
            cpuLabel.setText("CPU Usage: " + df.format(cpuUsage) + "%");
            memoryLabel.setText(String.format("Memory Usage: %.1f%% (Total: %.1f GB)",
                                            memInfo[0], memInfo[1] / 1024.0));
            powerLabel.setText("Power Status: " + powerInfo);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private double getLinuxCpuUsage() {
        try {
            ProcessBuilder pb = new ProcessBuilder("top", "-bn1");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("%Cpu(s)")) {
                    String[] parts = line.split(",");
                    String userCpu = parts[0].split("\\s+")[1];
                    return Double.parseDouble(userCpu);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private double[] getLinuxMemoryInfo() {
        double[] result = new double[2]; // [usage percentage, total GB]
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
            String line;
            double total = 0;
            double available = 0;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    total = Double.parseDouble(line.split("\\s+")[1]);
                } else if (line.startsWith("MemAvailable:")) {
                    available = Double.parseDouble(line.split("\\s+")[1]);
                }
            }
            reader.close();
            
            result[0] = ((total - available) / total) * 100;
            result[1] = total / 1024.0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private String getLinuxPowerInfo() {
        try {
            File batteryDir = new File("/sys/class/power_supply/BAT0");
            if (batteryDir.exists()) {
                BufferedReader statusReader = new BufferedReader(
                    new FileReader("/sys/class/power_supply/BAT0/status")
                );
                String status = statusReader.readLine();
                statusReader.close();
                
                BufferedReader capacityReader = new BufferedReader(
                    new FileReader("/sys/class/power_supply/BAT0/capacity")
                );
                String capacity = capacityReader.readLine();
                capacityReader.close();
                
                return status + " (" + capacity + "%)";
            } else {
                return "AC Power";
            }
        } catch (Exception e) {
            return "Power info unavailable";
        }
    }

    public class CacheSpeedPanel extends JPanel {
        private static final int BALL_SIZE = 20;
        private Map<String, Ball> balls = new HashMap<>();
        private Timer animationTimer;
        private static final Color L1_COLOR = new Color(144, 238, 144);  // Light green
        private static final Color L2_COLOR = new Color(0, 0, 255);      // Blue
        private static final Color L3_COLOR = new Color(128, 0, 128);    // Purple
        private static final Color RAM_COLOR = new Color(255, 0, 0);     // Red
        
        private class Ball {
            double x = 50;  // Start position
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
        
        public CacheSpeedPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 400));
            
            // Get actual cache latencies
            Map<String, Double> latencies = getCacheLatencies();
            
            // Create balls with proper spacing
            int startY = 50;
            int spacing = 70;
            
            // Normalize speeds (make L1 the fastest and others relative to it)
            double l1Speed = 5.0;  // Base speed
            double l1Latency = latencies.get("L1");
            
            balls.put("L1", new Ball(startY, l1Speed, L1_COLOR, "L1 Cache", l1Latency + "ns"));
            balls.put("L2", new Ball(startY + spacing, 
                                    l1Speed * (l1Latency/latencies.get("L2")), 
                                    L2_COLOR, "L2 Cache", 
                                    latencies.get("L2") + "ns"));
            balls.put("L3", new Ball(startY + spacing * 2, 
                                    l1Speed * (l1Latency/latencies.get("L3")), 
                                    L3_COLOR, "L3 Cache", 
                                    latencies.get("L3") + "ns"));
            balls.put("RAM", new Ball(startY + spacing * 3, 
                                     l1Speed * (l1Latency/latencies.get("RAM")), 
                                     RAM_COLOR, "RAM", 
                                     latencies.get("RAM") + "ns"));
            
            // Animation timer
            animationTimer = new Timer(16, e -> {  // ~60 FPS
                for (Ball ball : balls.values()) {
                    ball.x += ball.speed;
                    if (ball.x > getWidth() - BALL_SIZE) {
                        ball.x = 50;  // Reset to start
                    }
                }
                repaint();
            });
            animationTimer.start();
        }
        
        private Map<String, Double> getCacheLatencies() {
            Map<String, Double> latencies = new HashMap<>();
            
            try {
                // Read L1 cache latency
                String l1Latency = readCacheLatency("/sys/devices/system/cpu/cpu0/cache/index0/");
                latencies.put("L1", Double.parseDouble(l1Latency));
                
                // Read L2 cache latency
                String l2Latency = readCacheLatency("/sys/devices/system/cpu/cpu0/cache/index2/");
                latencies.put("L2", Double.parseDouble(l2Latency));
                
                // Read L3 cache latency
                String l3Latency = readCacheLatency("/sys/devices/system/cpu/cpu0/cache/index3/");
                latencies.put("L3", Double.parseDouble(l3Latency));
                
                // Estimate RAM latency (this is a rough estimate, could be improved with actual benchmarking)
                latencies.put("RAM", 80.0);  // Typical DDR4 latency
                
            } catch (Exception e) {
                // Fallback to typical values if we can't read actual values
                latencies.put("L1", 1.0);
                latencies.put("L2", 4.0);
                latencies.put("L3", 40.0);
                latencies.put("RAM", 80.0);
            }
            
            return latencies;
        }
        
        private String readCacheLatency(String path) throws IOException {
            // This is a simplified version - in reality, you'd need to parse more cache information
            // and possibly run some benchmarks for accurate measurements
            File cacheDir = new File(path);
            if (cacheDir.exists() && cacheDir.isDirectory()) {
                // Real implementation would read and parse cache details
                // This is just a placeholder
                return "1.0";
            }
            throw new IOException("Cache information not available");
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.setColor(Color.BLACK);
            g2d.drawString("CPU Cache Vs RAM Speed", getWidth()/2 - 100, 30);
            
            // Draw CPU box
            g2d.setColor(new Color(173, 216, 230));  // Light blue
            g2d.fillRect(10, 50, 30, 200);
            g2d.setColor(Color.BLACK);
            g2d.drawString("CPU", 10, 270);
            
            // Draw legend
            int legendX = getWidth() - 150;
            int legendY = 50;
            int legendSpacing = 25;
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            
            for (Ball ball : balls.values()) {
                // Draw ball
                g2d.setColor(ball.color);
                g2d.fillOval((int)ball.x, ball.y, BALL_SIZE, BALL_SIZE);
                
                // Draw legend
                g2d.fillOval(legendX, legendY, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString(ball.label + " (" + ball.latency + ")", legendX + 20, legendY + 10);
                legendY += legendSpacing;
            }
        }
        
        public void stopAnimation() {
            if (animationTimer != null) {
                animationTimer.stop();
            }
        }
    }

    // Add method to clean up resources
    public void cleanup() {
        if (cachePanel != null) {
            cachePanel.stopAnimation();
        }
    }
}