import java.awt.*;
import javax.swing.*;

// Main class for the disk menu
public class DiskMenu {

    static JPanel innitDiskMenu() {
        diskInfo disk = new diskInfo();
        disk.read(); // Load disk information

        // Create the main panel with black background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 40, 20, 40); // Increased vertical and horizontal gaps

        // Disk Count
        int count = disk.diskCount();
        JLabel label1 = new JLabel("Disk count: " + count);
        label1.setForeground(Color.white);
        label1.setFont(new Font("Arial", Font.BOLD, 24)); // Increased font size
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.anchor = GridBagConstraints.CENTER; // Center the label
        mainPanel.add(label1, gbc);

        // Display each disk's details with a heatmap beside the text
        for (int i = 0; i < count; i++) {
            String name = disk.getName(i);
            long total = disk.getTotal(i);
            long used = disk.getUsed(i);
            long available = disk.getAvailable(i);
            double usagePercentage = (double) used / total * 100; // Calculate usage percentage
            String status = usagePercentage < 80 ? "Healthy" : "Warning"; // Determine status

            // Create a panel for each disk
            JPanel diskPanel = new JPanel();
            diskPanel.setBackground(Color.black);
            diskPanel.setLayout(new BoxLayout(diskPanel, BoxLayout.X_AXIS)); // Horizontal layout for text and heatmap

            // Left side: Text details for the disk
            JPanel textPanel = new JPanel();
            textPanel.setBackground(Color.black);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

            // Centered text labels with increased font size
            JLabel diskLabel = new JLabel("Disk " + (i + 1) + " - Name: " + name);
            diskLabel.setForeground(Color.white);
            diskLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size
            textPanel.add(diskLabel);

            JLabel totalLabel = new JLabel("Total: " + formatSize(total));
            totalLabel.setForeground(Color.white);
            totalLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
            textPanel.add(totalLabel);

            JLabel usedLabel = new JLabel("Used: " + formatSize(used));
            usedLabel.setForeground(Color.white);
            usedLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
            textPanel.add(usedLabel);

            JLabel availableLabel = new JLabel("Available: " + formatSize(available));
            availableLabel.setForeground(Color.white);
            availableLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
            textPanel.add(availableLabel);

            JLabel usageLabel = new JLabel(String.format("Usage: %.2f%%", usagePercentage));
            usageLabel.setForeground(Color.white);
            usageLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
            textPanel.add(usageLabel);

            JLabel statusLabel = new JLabel("Status: " + status);
            statusLabel.setForeground(status.equals("Warning") ? Color.RED : Color.GREEN); // Color based on status
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
            textPanel.add(statusLabel);

            diskPanel.add(textPanel); // Add text panel to disk panel

            // Right side: Heatmap for disk usage
            DiskUsagePanel heatmap = new DiskUsagePanel(total, used);
            heatmap.setPreferredSize(new Dimension(200, 200)); // Set smaller size for the heatmap
            diskPanel.add(heatmap); // Add heatmap to disk panel

            // Center the disk panel on the main panel
            gbc.gridx = 0; // Column 0
            gbc.gridy++; // Next row
            gbc.anchor = GridBagConstraints.CENTER; // Center the disk panel
            mainPanel.add(diskPanel, gbc);
        }

        // Add Memory Overview at the end
        gbc.gridy++; // Next row
        gbc.insets = new Insets(40, 40, 40, 40); // Reset insets for memory panel
        mainPanel.add(new MemInfo(), gbc); // Add memory info panel

        // Wrap main panel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar

        // Create a new JPanel to hold the JScrollPane
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel; // Return the wrapper panel containing the JScrollPane
    }

    // Helper to format sizes in KB, MB, or GB
    private static String formatSize(long size) {
        double kb = size / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        if (gb >= 1) return String.format("%.2f GB", gb);
        else if (mb >= 1) return String.format("%.2f MB", mb);
        else return String.format("%.2f KB", kb);
    }
}

// Panel to display heatmap for each disk
class DiskUsagePanel extends JPanel {
    private long totalSpace;
    private long usedSpace;

    public DiskUsagePanel(long totalSpace, long usedSpace) {
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
        setBackground(Color.black); // Set background to black
    }

    // Paints the heatmap grid on the JPanel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = 10;
        int cols = 10;
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows) - 2; // Calculate size for each square cell, slightly smaller
        int gap = 2; // Gap between cells

        // Calculate the usage percentage
        double usedPercentage = (double) usedSpace / totalSpace;

        // Draw each cell in the grid
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                double cellUsageRatio = index / (double) (rows * cols);

                // Determine color for each cell based on whether it's used or available
                Color cellColor = cellUsageRatio <= usedPercentage ? new Color(255, 0, 0) : new Color(0, 255, 0); // Red for used, green for available
                g.setColor(cellColor);

                // Calculate position for each cell
                int x = col * (cellSize + gap); // Include gap
                int y = row * (cellSize + gap); // Include gap

                // Draw the filled cell and its border
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }
}

// Memory Information Panel
class MemInfo extends JPanel {
    public MemInfo() {
        // Get memory info
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double usagePercentage = (double) usedMemory / totalMemory * 100; // Calculate usage percentage
        String status = usagePercentage < 80 ? "Healthy" : "Warning"; // Determine status

        setBackground(Color.black);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // Changed to horizontal layout

        // Left side: Text details for memory
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.black);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Centered text labels with increased font size
        JLabel titleLabel = new JLabel("Memory Overview");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increased font size
        textPanel.add(titleLabel);

        JLabel totalLabel = new JLabel("Total: " + formatSize(totalMemory));
        totalLabel.setForeground(Color.white);
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
        textPanel.add(totalLabel);

        JLabel usedLabel = new JLabel("Used: " + formatSize(usedMemory));
        usedLabel.setForeground(Color.white);
        usedLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
        textPanel.add(usedLabel);

        JLabel availableLabel = new JLabel("Available: " + formatSize(freeMemory));
        availableLabel.setForeground(Color.white);
        availableLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
        textPanel.add(availableLabel);

        JLabel usageLabel = new JLabel(String.format("Usage: %.2f%%", usagePercentage));
        usageLabel.setForeground(Color.white);
        usageLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
        textPanel.add(usageLabel);

        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setForeground(status.equals("Warning") ? Color.RED : Color.GREEN); // Color based on status
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
        textPanel.add(statusLabel);

        // Right side: Memory usage grid
        MemoryUsagePanel memoryUsagePanel = new MemoryUsagePanel(totalMemory, usedMemory);
        memoryUsagePanel.setPreferredSize(new Dimension(200, 200)); // Set size for the grid

        // Add both panels to the main panel
        add(textPanel); // Add text panel
        add(memoryUsagePanel); // Add memory usage grid panel
    }

    // Helper to format sizes in KB, MB, or GB
    private static String formatSize(long size) {
        double kb = size / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        if (gb >= 1) return String.format("%.2f GB", gb);
        else if (mb >= 1) return String.format("%.2f MB", mb);
        else return String.format("%.2f KB", kb);
    }
}

// Panel to display heatmap for memory usage
class MemoryUsagePanel extends JPanel {
    private long totalMemory;
    private long usedMemory;

    public MemoryUsagePanel(long totalMemory, long usedMemory) {
        this.totalMemory = totalMemory;
        this.usedMemory = usedMemory;
        setBackground(Color.black); // Set background to black
    }

    // Paints the heatmap grid on the JPanel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rows = 10;
        int cols = 10;
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows) - 2; // Calculate size for each square cell, slightly smaller
        int gap = 2; // Gap between cells

        // Calculate the usage percentage
        double usedPercentage = (double) usedMemory / totalMemory;

        // Draw each cell in the grid
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                double cellUsageRatio = index / (double) (rows * cols);

                // Determine color for each cell based on whether it's used or available
                Color cellColor = cellUsageRatio <= usedPercentage ? new Color(255, 0, 0) : new Color(0, 255, 0); // Red for used, green for available
                g.setColor(cellColor);

                // Calculate position for each cell
                int x = col * (cellSize + gap); // Include gap
                int y = row * (cellSize + gap); // Include gap

                // Draw the filled cell and its border
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }
}