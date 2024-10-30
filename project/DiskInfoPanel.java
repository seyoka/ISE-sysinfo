  
  
  
  
  
  
  

  
  
  
  
  
  
    
  
  
  
  
  
        
  
  
  
  
  
  
    
  
  

  
  
  
  
  
  
  
  

  
  
  
  
        
  
  
  
  
  
  
  
  
  
  

  
  
  
  
  
            
  
  
  
  
  
  
                    
  
  
  
  
  
  
  
  
  
  
  
  

  
  
  
  
  
  
  
  

  
  

  
  
  

  
  
  

  
  
  
  
  

  
  
  
  
  
  
  

  
            
  
  

  
  
  
  

  
  
  
  
  
  
  
  

  
  
  
  
  
  
  

  
  
  
  
  
  
  
  

  
  
  
  
            
  
  
  
  
  
  
  
  
  
  
  
  

  
  
        
  
  
  

  
  
  
  
  

  
  
  
  
  
  

  
  
  

  
  
  

  
  

  
  

  
  
  
  
  
  

  
  
  
  
  
  
  

  
  
  
  
  
  
  
  
  
  
  

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DiskInfoPanel extends JPanel {
    private final Color MAIN_CONTENT_COLOR = new Color(35, 35, 52);
    private final Color ACCENT_COLOR = new Color(75, 75, 96);
    private final Color PROGRESS_COLOR = new Color(77, 208, 255);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color HOVER_COLOR = new Color(85, 85, 106);
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    private List<DiskData> diskDataList;
    private diskInfo diskInfo;
    private JButton refreshButton;
    private JPanel headerPanel;

    static {
        try {
            System.loadLibrary("sysinfo");
            System.out.println("Disk Panel: Successfully loaded sysinfo library");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Disk Panel: Failed to load library: " + e.getMessage());
        }
    }

    private static class DiskData {
        String name;
        long total;
        long used;
        String type;
        boolean isSystem;
        
        DiskData(String name, long total, long used) {
            this.name = name;
            this.total = total;
            this.used = used;
            this.type = DriveInfo.getDriveType(name);
            this.isSystem = name.contains("root") || name.contains("sda");
        }

        public double getUsagePercentage() {
            return (double) used / total * 100;
        }
    }

    private static class DriveInfo {
        private static final Map<String, String> FRIENDLY_NAMES = new HashMap<>();
        private static final Map<String, String> DRIVE_TYPES = new HashMap<>();
        
        static {
            FRIENDLY_NAMES.put("/dev/mapper/data-root", "System Drive");
            FRIENDLY_NAMES.put("/dev/sda1", "Boot Drive (EFI)");
            FRIENDLY_NAMES.put("/dev/sda2", "Recovery Drive");
            FRIENDLY_NAMES.put("/dev/sdb", "Data Drive");
            
            DRIVE_TYPES.put("/dev/nvme", "NVMe SSD");
            DRIVE_TYPES.put("/dev/sda", "SSD");
            DRIVE_TYPES.put("/dev/sdb", "HDD");
        }
        
        public static String getFriendlyName(String path) {
            return FRIENDLY_NAMES.getOrDefault(path, getGenericName(path));
        }
        
        public static String getDriveType(String path) {
            for (Map.Entry<String, String> entry : DRIVE_TYPES.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return "Storage Drive";
        }
        
        private static String getGenericName(String path) {
            if (path.contains("mapper")) {
                return "Logical Volume";
            } else if (path.contains("nvme")) {
                return "NVMe Drive " + path.substring(path.lastIndexOf("/") + 1);
            } else if (path.contains("sd")) {
                String base = path.substring(path.lastIndexOf("/") + 1);
                char driveLetter = base.charAt(2);
                String partitionNumber = base.length() > 3 ? " (Partition " + base.substring(3) + ")" : "";
                return "Drive " + Character.toUpperCase(driveLetter) + partitionNumber;
            }
            return path.substring(path.lastIndexOf("/") + 1);
        }
    }

    public DiskInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_CONTENT_COLOR);
        diskDataList = new ArrayList<>();
        
        try {
            diskInfo = new diskInfo();
            createHeader();
            readDiskInfo();
            initializeComponents();
        } catch (Exception e) {
            System.err.println("Error initializing disk info: " + e.getMessage());
            add(new JLabel("Error reading disk information"), BorderLayout.CENTER);
        }
    }

    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(MAIN_CONTENT_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Storage Overview");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        refreshButton = new JButton("Refresh");
        refreshButton.setForeground(TEXT_COLOR);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(ACCENT_COLOR);
            }
        });

        refreshButton.addActionListener(e -> {
            readDiskInfo();
            repaint();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    private void readDiskInfo() {
        try {
            diskInfo.read();
            diskDataList.clear();
            HashSet<String> processedDisks = new HashSet<>();
            
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

              
            Collections.sort(diskDataList, (a, b) -> {
                if (a.isSystem != b.isSystem) {
                    return b.isSystem ? 1 : -1;
                }
                return a.name.compareTo(b.name);
            });

        } catch (Exception e) {
            System.err.println("Error reading disk info: " + e.getMessage());
        }
    }

    private void initializeComponents() {
          
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(MAIN_CONTENT_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

          
        JPanel detailView = new JPanel(new BorderLayout(20, 0));
        detailView.setBackground(MAIN_CONTENT_COLOR);
        detailView.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

          
        JPanel driveListContainer = new JPanel(new BorderLayout());
        driveListContainer.setBackground(MAIN_CONTENT_COLOR);
        DriveListPanel driveListPanel = new DriveListPanel();
        JScrollPane scrollPane = new JScrollPane(driveListPanel);
        scrollPane.setBackground(MAIN_CONTENT_COLOR);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(MAIN_CONTENT_COLOR);
        driveListContainer.add(scrollPane);

          
        detailView.add(new StorageRingPanel(), BorderLayout.CENTER);
        detailView.add(driveListContainer, BorderLayout.EAST);

          
        JPanel gridView = DiskMenu.innitDiskMenu();
        gridView.setBackground(MAIN_CONTENT_COLOR);

          
        tabbedPane.addTab("Detail View", detailView);
        tabbedPane.addTab("Grid View", gridView);

          
        add(tabbedPane, BorderLayout.CENTER);
    }

    private class StorageRingPanel extends JPanel {
        private static final int RING_SIZE = 300;
        private static final int RING_THICKNESS = 25;
        private boolean isHovered = false;
        private Rectangle2D ringBounds;

        public StorageRingPanel() {
            setBackground(MAIN_CONTENT_COLOR);
            setPreferredSize(new Dimension(RING_SIZE + 100, RING_SIZE + 100));
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

              
            long totalStorage = 0;
            long usedStorage = 0;
            for (DiskData disk : diskDataList) {
                totalStorage += disk.total;
                usedStorage += disk.used;
            }

            double percentage = totalStorage > 0 ? (double) usedStorage / totalStorage : 0;
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

              
            ringBounds = new Rectangle2D.Double(
                centerX - RING_SIZE/2, centerY - RING_SIZE/2,
                RING_SIZE, RING_SIZE
            );

              
            Color ringColor = isHovered ? HOVER_COLOR : ACCENT_COLOR;
            g2.setColor(ringColor);
            g2.setStroke(new BasicStroke(RING_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Double(
                ringBounds.getX(), ringBounds.getY(),
                ringBounds.getWidth(), ringBounds.getHeight(),
                0, 360, Arc2D.OPEN
            ));

              
            g2.setColor(PROGRESS_COLOR);
            g2.draw(new Arc2D.Double(
                ringBounds.getX(), ringBounds.getY(),
                ringBounds.getWidth(), ringBounds.getHeight(),
                90, -360 * percentage, Arc2D.OPEN
            ));

              
            drawCenterText(g2, centerX, centerY, usedStorage, totalStorage);
        }

        private void drawCenterText(Graphics2D g2, int centerX, int centerY, long used, long total) {
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

              
            if (isHovered) {
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                String hintText = "Click for details";
                g2.drawString(hintText,
                    centerX - fm.stringWidth(hintText)/2,
                    centerY + 90);
            }
        }
    }

    private class DriveListPanel extends JPanel {
        private static final int BAR_HEIGHT = 8;
        private static final int DRIVE_SPACING = 85;
        private Map<Rectangle, DiskData> driveRects = new HashMap<>();
        private DiskData hoveredDrive = null;

        public DriveListPanel() {
            setBackground(MAIN_CONTENT_COLOR);
            setPreferredSize(new Dimension(400, diskDataList.size() * DRIVE_SPACING + 20));

              
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent evt) {
                    DiskData previousHovered = hoveredDrive;
                    hoveredDrive = null;
                    
                    for (Map.Entry<Rectangle, DiskData> entry : driveRects.entrySet()) {
                        if (entry.getKey().contains(evt.getPoint())) {
                            hoveredDrive = entry.getValue();
                            break;
                        }
                    }
                    
                    if (hoveredDrive != previousHovered) {
                        repaint();
                    }
                }
            });

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    hoveredDrive = null;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            driveRects.clear();
            int y = 20;

            for (DiskData disk : diskDataList) {
                Rectangle driveRect = new Rectangle(0, y - 15, getWidth(), DRIVE_SPACING);
                driveRects.put(driveRect, disk);
                
                  
                if (disk == hoveredDrive) {
                    g2.setColor(HOVER_COLOR);
                    g2.fillRoundRect(0, y - 15, getWidth() - 20, DRIVE_SPACING - 5, 10, 10);
                }

                drawDriveBar(g2, y, disk);
                y += DRIVE_SPACING;
            }
        }

        private void drawDriveBar(Graphics2D g2, int y, DiskData disk) {
            double percentage = (double) disk.used / disk.total;
            int width = getWidth() - 20;

              
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String friendlyName = DriveInfo.getFriendlyName(disk.name);
            g2.drawString(friendlyName, 0, y);

              
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            g2.setColor(new Color(200, 200, 200));
            String driveType = disk.type;
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(driveType, width - fm.stringWidth(driveType), y);

              
            g2.setColor(ACCENT_COLOR);
            g2.fillRoundRect(0, y + 5, width, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);

            g2.setColor(PROGRESS_COLOR);
            g2.fillRoundRect(0, y + 5, (int)(width * percentage), BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);

              
            String sizeText = String.format("%s of %s (%.1f%%)",
                formatSize(disk.used), formatSize(disk.total), percentage * 100);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString(sizeText, 0, y + 30);

              
            if (percentage > 0.9) {    
                g2.setColor(new Color(255, 100, 100));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("!", width - 10, y + 30);
            }

              
            if (disk != diskDataList.get(diskDataList.size() - 1)) {
                g2.setColor(ACCENT_COLOR);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(0, y + 45, width, y + 45);
            }
        }
    }
    
    private String formatSize(long blocks) {
        double bytes = blocks * 1024.0;
        double KB = bytes / 1024;
        double MB = KB / 1024;
        double GB = MB / 1024;
        double TB = GB / 1024;

        if (TB >= 1) {
            return String.format("%.2f TB", TB);
        } else if (GB >= 1) {
            return String.format("%.2f GB", GB);
        } else if (MB >= 1) {
            return String.format("%.2f MB", MB);
        } else {
            return String.format("%.2f KB", KB);
        }
    }

    private void showTooltip(String text, Component component, Point location) {
        JToolTip tooltip = new JToolTip();
        tooltip.setTipText(text);
        tooltip.setBackground(ACCENT_COLOR);
        tooltip.setForeground(TEXT_COLOR);
        PopupFactory.getSharedInstance().getPopup(component, tooltip, 
            location.x, location.y).show();
    }
}