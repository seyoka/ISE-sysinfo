import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.*;

public class DiskInfoPanel extends JPanel {
    private final Color MAIN_CONTENT_COLOR = new Color(35, 35, 52);
    private final Color ACCENT_COLOR = new Color(75, 75, 96);
    private final Color PROGRESS_COLOR = new Color(77, 208, 255);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    // Static storage for disk information
    private static class DiskData {
        String name;
        long total;
        long used;
        
        DiskData(String name, long total, long used) {
            this.name = name;
            this.total = total;
            this.used = used;
        }
    }
    
    private List<DiskData> diskDataList;
    private diskInfo diskInfo;

    static {
        try {
            System.loadLibrary("sysinfo");
            System.out.println("Disk Panel: Successfully loaded sysinfo library");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Disk Panel: Failed to load library: " + e.getMessage());
        }
    }

    public DiskInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_CONTENT_COLOR);
        diskDataList = new ArrayList<>();
        
        try {
            // Initialize and read disk info once
            diskInfo = new diskInfo();
            readDiskInfo();
            initializeComponents();
        } catch (Exception e) {
            System.err.println("Error initializing disk info: " + e.getMessage());
            add(new JLabel("Error reading disk information"), BorderLayout.CENTER);
        }
    }

    private void readDiskInfo() {
        try {
            diskInfo.read();
            diskDataList.clear();
            HashSet<String> processedDisks = new HashSet<>();
            
            // Store all disk information
            for (int i = 0; i < diskInfo.diskCount(); i++) {
                String name = diskInfo.getName(i);
                if (!processedDisks.contains(name) && 
                    !name.startsWith("tmpfs") && 
                    !name.startsWith("efivarfs")) {
                    
                    diskDataList.add(new DiskData(
                        name,
                        diskInfo.getTotal(i),
                        diskInfo.getUsed(i)
                    ));
                    processedDisks.add(name);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading disk info: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(MAIN_CONTENT_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.add(new DriveListPanel());
        add(rightPanel, BorderLayout.EAST);

        add(new StorageRingPanel(), BorderLayout.CENTER);
    }

    private class StorageRingPanel extends JPanel {
        private static final int RING_SIZE = 300;
        private static final int RING_THICKNESS = 25;

        public StorageRingPanel() {
            setBackground(MAIN_CONTENT_COLOR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate totals
            long totalStorage = 0;
            long usedStorage = 0;
            for (DiskData disk : diskDataList) {
                totalStorage += disk.total;
                usedStorage += disk.used;
            }

            double percentage = totalStorage > 0 ? (double) usedStorage / totalStorage : 0;
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Draw the visualization
            drawRing(g2, centerX, centerY, percentage);
            drawText(g2, centerX, centerY, usedStorage, totalStorage);
        }

        private void drawRing(Graphics2D g2, int centerX, int centerY, double percentage) {
            g2.setColor(ACCENT_COLOR);
            g2.setStroke(new BasicStroke(RING_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Double(
                centerX - RING_SIZE/2, centerY - RING_SIZE/2,
                RING_SIZE, RING_SIZE,
                0, 360, Arc2D.OPEN
            ));

            g2.setColor(PROGRESS_COLOR);
            g2.draw(new Arc2D.Double(
                centerX - RING_SIZE/2, centerY - RING_SIZE/2,
                RING_SIZE, RING_SIZE,
                90, -360 * percentage, Arc2D.OPEN
            ));
        }

        private void drawText(Graphics2D g2, int centerX, int centerY, long used, long total) {
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
            String percentText = String.format("%.2f%%", (double) used / total * 100);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(percentText, 
                centerX - fm.stringWidth(percentText)/2, 
                centerY);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            String usedText = "Used: " + formatSize(used);
            String totalText = "Total: " + formatSize(total);
            String driveText = diskDataList.size() + " Drive" + (diskDataList.size() != 1 ? "s" : "");
            
            fm = g2.getFontMetrics();
            g2.drawString(usedText, 
                centerX - fm.stringWidth(usedText)/2, 
                centerY + 30);
            g2.drawString(totalText, 
                centerX - fm.stringWidth(totalText)/2, 
                centerY + 50);
            g2.drawString(driveText,
                centerX - fm.stringWidth(driveText)/2,
                centerY + 70);
        }
    }

    private class DriveListPanel extends JPanel {
        private static final int BAR_HEIGHT = 8;
        
        public DriveListPanel() {
            setBackground(MAIN_CONTENT_COLOR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int y = 10;
            for (DiskData disk : diskDataList) {
                drawDriveBar(g2, y, disk);
                y += 70;
            }
        }

        private void drawDriveBar(Graphics2D g2, int y, DiskData disk) {
            double percentage = (double) disk.used / disk.total;
            int width = getWidth() - 20;

            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString(disk.name, 0, y);

            g2.setColor(ACCENT_COLOR);
            g2.fillRoundRect(0, y + 5, width, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);

            g2.setColor(PROGRESS_COLOR);
            g2.fillRoundRect(0, y + 5, (int)(width * percentage), BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);

            String sizeText = String.format("%s of %s (%.2f%%)",
                formatSize(disk.used), formatSize(disk.total), percentage * 100);
            g2.setColor(TEXT_COLOR);
            g2.drawString(sizeText, 0, y + 30);
        }
    }

    private String formatSize(long blocks) {
        double bytes = blocks * 4096.0; // 4KB blocks
        double KB = bytes / 1024;
        double MB = KB / 1024;
        double GB = MB / 1024;
        double TB = GB / 1024;

        if (TB >= 1) {
            return String.format("%.2f TB", TB);
        } else if (GB >= 1) {
            return String.format("%.2f GB", GB);
        } else {
            return String.format("%.2f MB", MB);
        }
    }
}