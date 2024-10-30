import java.awt.*;
import javax.swing.*;

  
public class DiskMenu {

    static JPanel innitDiskMenu() {
        diskInfo disk = new diskInfo();
        disk.read();   

          
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setLayout(new GridBagLayout());   

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 40, 20, 40);   

          
        int count = disk.diskCount();
        JLabel label1 = new JLabel("Disk count: " + count);
        label1.setForeground(Color.white);
        label1.setFont(new Font("Arial", Font.BOLD, 24));   
        gbc.gridx = 0;   
        gbc.gridy = 0;   
        gbc.anchor = GridBagConstraints.CENTER;   
        mainPanel.add(label1, gbc);

          
        for (int i = 0; i < count; i++) {
            String name = disk.getName(i);
            long total = disk.getTotal(i);
            long used = disk.getUsed(i);
            long available = disk.getAvailable(i);
            double usagePercentage = (double) used / total * 100;   
            String status = usagePercentage < 80 ? "Healthy" : "Warning";   

              
            JPanel diskPanel = new JPanel();
            diskPanel.setBackground(Color.black);
            diskPanel.setLayout(new BoxLayout(diskPanel, BoxLayout.X_AXIS));   

              
            JPanel textPanel = new JPanel();
            textPanel.setBackground(Color.black);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

              
            JLabel diskLabel = new JLabel("Disk " + (i + 1) + " - Name: " + name);
            diskLabel.setForeground(Color.white);
            diskLabel.setFont(new Font("Arial", Font.BOLD, 20));   
            textPanel.add(diskLabel);

            JLabel totalLabel = new JLabel("Total: " + formatSize(total));
            totalLabel.setForeground(Color.white);
            totalLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
            textPanel.add(totalLabel);

            JLabel usedLabel = new JLabel("Used: " + formatSize(used));
            usedLabel.setForeground(Color.white);
            usedLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
            textPanel.add(usedLabel);

            JLabel availableLabel = new JLabel("Available: " + formatSize(available));
            availableLabel.setForeground(Color.white);
            availableLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
            textPanel.add(availableLabel);

            JLabel usageLabel = new JLabel(String.format("Usage: %.2f%%", usagePercentage));
            usageLabel.setForeground(Color.white);
            usageLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
            textPanel.add(usageLabel);

            JLabel statusLabel = new JLabel("Status: " + status);
            statusLabel.setForeground(status.equals("Warning") ? Color.RED : Color.GREEN);   
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
            textPanel.add(statusLabel);

            diskPanel.add(textPanel);   

              
            DiskUsagePanel heatmap = new DiskUsagePanel(total, used);
            heatmap.setPreferredSize(new Dimension(200, 200));   
            diskPanel.add(heatmap);   

              
            gbc.gridx = 0;   
            gbc.gridy++;   
            gbc.anchor = GridBagConstraints.CENTER;   
            mainPanel.add(diskPanel, gbc);
        }

          
        gbc.gridy++;   
        gbc.insets = new Insets(40, 40, 40, 40);   
        mainPanel.add(new MemInfo(), gbc);   

          
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);   

          
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;   
    }

      
    private static String formatSize(long size) {
        double kb = size / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        if (gb >= 1) return String.format("%.2f GB", gb);
        else if (mb >= 1) return String.format("%.2f MB", mb);
        else return String.format("%.2f KB", kb);
    }
}

  
class DiskUsagePanel extends JPanel {
    private long totalSpace;
    private long usedSpace;

    public DiskUsagePanel(long totalSpace, long usedSpace) {
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
        setBackground(Color.black);   
    }

      
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = 10;
        int cols = 10;
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows) - 2;   
        int gap = 2;   

          
        double usedPercentage = (double) usedSpace / totalSpace;

          
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                double cellUsageRatio = index / (double) (rows * cols);

                  
                Color cellColor = cellUsageRatio <= usedPercentage ? new Color(255, 0, 0) : new Color(0, 255, 0);   
                g.setColor(cellColor);

                  
                int x = col * (cellSize + gap);   
                int y = row * (cellSize + gap);   

                  
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }
}

  
class MemInfo extends JPanel {
    public MemInfo() {
          
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double usagePercentage = (double) usedMemory / totalMemory * 100;   
        String status = usagePercentage < 80 ? "Healthy" : "Warning";   

        setBackground(Color.black);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));   

          
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.black);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

          
        JLabel titleLabel = new JLabel("Memory Overview");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));   
        textPanel.add(titleLabel);

        JLabel totalLabel = new JLabel("Total: " + formatSize(totalMemory));
        totalLabel.setForeground(Color.white);
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
        textPanel.add(totalLabel);

        JLabel usedLabel = new JLabel("Used: " + formatSize(usedMemory));
        usedLabel.setForeground(Color.white);
        usedLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
        textPanel.add(usedLabel);

        JLabel availableLabel = new JLabel("Available: " + formatSize(freeMemory));
        availableLabel.setForeground(Color.white);
        availableLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
        textPanel.add(availableLabel);

        JLabel usageLabel = new JLabel(String.format("Usage: %.2f%%", usagePercentage));
        usageLabel.setForeground(Color.white);
        usageLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
        textPanel.add(usageLabel);

        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setForeground(status.equals("Warning") ? Color.RED : Color.GREEN);   
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));   
        textPanel.add(statusLabel);

          
        MemoryUsagePanel memoryUsagePanel = new MemoryUsagePanel(totalMemory, usedMemory);
        memoryUsagePanel.setPreferredSize(new Dimension(200, 200));   

          
        add(textPanel);   
        add(memoryUsagePanel);   
    }

      
    private static String formatSize(long size) {
        double kb = size / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        if (gb >= 1) return String.format("%.2f GB", gb);
        else if (mb >= 1) return String.format("%.2f MB", mb);
        else return String.format("%.2f KB", kb);
    }
}

  
class MemoryUsagePanel extends JPanel {
    private long totalMemory;
    private long usedMemory;

    public MemoryUsagePanel(long totalMemory, long usedMemory) {
        this.totalMemory = totalMemory;
        this.usedMemory = usedMemory;
        setBackground(Color.black);   
    }

      
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = 10;
        int cols = 10;
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows) - 2;   
        int gap = 2;   

          
        double usedPercentage = (double) usedMemory / totalMemory;

          
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                double cellUsageRatio = index / (double) (rows * cols);

                  
                Color cellColor = cellUsageRatio <= usedPercentage ? new Color(255, 0, 0) : new Color(0, 255, 0);   
                g.setColor(cellColor);

                  
                int x = col * (cellSize + gap);   
                int y = row * (cellSize + gap);   

                  
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }
}