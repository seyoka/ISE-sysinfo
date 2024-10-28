import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class PCINetworkMapPanel extends JPanel {
    private List<PCIDevice> devices;
    private int totalBuses;
    private static final int DEVICE_WIDTH = 280;
    private static final int DEVICE_HEIGHT = 60;
    private static final int VERTICAL_GAP = 80;
    private static final int HORIZONTAL_GAP = 100;
    private static final int DEVICE_VERTICAL_GAP = 20;
    private Map<Integer, List<PCIDevice>> devicesByBus;
    
    public PCINetworkMapPanel(List<PCIDevice> devices, int totalBuses) {
        this.devices = devices;
        this.totalBuses = totalBuses;
        setBackground(new Color(47, 47, 68));
        
        // Calculate panel size based on content
        int panelWidth = (DEVICE_WIDTH + HORIZONTAL_GAP) * totalBuses + HORIZONTAL_GAP;
        int maxDevicesPerBus = getMaxDevicesPerBus();
        int panelHeight = DEVICE_HEIGHT * (maxDevicesPerBus + 2) + VERTICAL_GAP * 3;
        
        setPreferredSize(new Dimension(Math.max(1200, panelWidth), Math.max(800, panelHeight)));
        
        // Initialize devicesByBus map
        updateDeviceMap();
    }
    
    private void updateDeviceMap() {
        devicesByBus = new HashMap<>();
        for (PCIDevice device : devices) {
            devicesByBus.computeIfAbsent(device.getBus(), k -> new ArrayList<>()).add(device);
        }
    }
    
    private int getMaxDevicesPerBus() {
        Map<Integer, Integer> devicesPerBus = new HashMap<>();
        for (PCIDevice device : devices) {
            devicesPerBus.merge(device.getBus(), 1, Integer::sum);
        }
        return devicesPerBus.values().stream().mapToInt(Integer::intValue).max().orElse(1);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate positions
        int computerX = getWidth() / 2 - DEVICE_WIDTH / 2;
        int computerY = 20;
        int totalWidth = (totalBuses * (DEVICE_WIDTH + HORIZONTAL_GAP)) - HORIZONTAL_GAP;
        int startX = (getWidth() - totalWidth) / 2;
        int busY = computerY + DEVICE_HEIGHT + VERTICAL_GAP;
        
        // First draw all connection lines
        g2d.setColor(new Color(75, 75, 96));
        drawAllConnectionLines(g2d, computerX, computerY, startX, busY);
        
        // Then draw all nodes
        drawAllNodes(g2d, computerX, computerY, startX, busY);
    }
    
    private void drawAllConnectionLines(Graphics2D g2d, int computerX, int computerY, int startX, int busY) {
        for (int i = 1; i <= totalBuses; i++) {
            int busX = startX + (i-1) * (DEVICE_WIDTH + HORIZONTAL_GAP);
            
            // Draw line from computer to bus
            g2d.drawLine(computerX + DEVICE_WIDTH/2, computerY + DEVICE_HEIGHT,
                        busX + DEVICE_WIDTH/2, busY);
            
            // Draw lines to devices
            List<PCIDevice> busDevices = devicesByBus.getOrDefault(i, new ArrayList<>());
            if (!busDevices.isEmpty()) {
                int deviceY = busY + DEVICE_HEIGHT + VERTICAL_GAP;
                
                for (int j = 0; j < busDevices.size(); j++) {
                    int currentDeviceY = deviceY + (j * (DEVICE_HEIGHT + DEVICE_VERTICAL_GAP));
                    g2d.drawLine(busX + DEVICE_WIDTH/2, busY + DEVICE_HEIGHT,
                               busX + DEVICE_WIDTH/2, currentDeviceY);
                }
            }
        }
    }

    private void drawAllNodes(Graphics2D g2d, int computerX, int computerY, int startX, int busY) {
        // Draw computer node
        drawNode(g2d, computerX, computerY, "Computer", null);
        
        // Draw bus nodes and their devices
        for (int i = 1; i <= totalBuses; i++) {
            int busX = startX + (i-1) * (DEVICE_WIDTH + HORIZONTAL_GAP);
            String busLabel = devicesByBus.containsKey(i) ? "Bus " + i : "Bus " + i + " (Empty)";
            drawNode(g2d, busX, busY, busLabel, null);
            
            // Draw device nodes
            List<PCIDevice> busDevices = devicesByBus.getOrDefault(i, new ArrayList<>());
            int deviceY = busY + DEVICE_HEIGHT + VERTICAL_GAP;
            
            for (int j = 0; j < busDevices.size(); j++) {
                PCIDevice device = busDevices.get(j);
                int currentDeviceY = deviceY + (j * (DEVICE_HEIGHT + DEVICE_VERTICAL_GAP));
                drawNode(g2d, busX, currentDeviceY, "", device);
            }
        }
    }
    
    private void drawNode(Graphics2D g2d, int x, int y, String label, PCIDevice device) {
        // Draw background
        g2d.setColor(new Color(41, 41, 66));
        g2d.fillRect(x, y, DEVICE_WIDTH, DEVICE_HEIGHT);
        g2d.setColor(new Color(75, 75, 96));
        g2d.drawRect(x, y, DEVICE_WIDTH, DEVICE_HEIGHT);
        
        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        FontMetrics fm = g2d.getFontMetrics();
        if (device == null) {
            // Bus or computer node
            int textX = x + (DEVICE_WIDTH - fm.stringWidth(label)) / 2;
            int textY = y + DEVICE_HEIGHT / 2 + fm.getAscent() / 2;
            g2d.drawString(label, textX, textY);
        } else {
            // Device node
            int padding = 5;
            int lineHeight = fm.getHeight();
            
            String vendor = truncateText("Vendor: " + device.getVendor(), DEVICE_WIDTH - 2*padding, fm);
            String product = truncateText("Product: " + device.getProduct(), DEVICE_WIDTH - 2*padding, fm);
            String functions = "Functions: " + device.getfunction();
            
            g2d.drawString(vendor, x + padding, y + lineHeight);
            g2d.drawString(product, x + padding, y + 2*lineHeight);
            g2d.drawString(functions, x + padding, y + 3*lineHeight);
        }
    }
    
    private String truncateText(String text, int maxWidth, FontMetrics fm) {
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        
        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);
        
        for (int i = text.length(); i > 0; i--) {
            String truncated = text.substring(0, i) + ellipsis;
            if (fm.stringWidth(truncated) <= maxWidth) {
                return truncated;
            }
        }
        
        return text.substring(0, 1) + ellipsis;
    }
    
    public void updateDevices(List<PCIDevice> newDevices) {
        this.devices = newDevices;
        updateDeviceMap();
        repaint();
    }
}