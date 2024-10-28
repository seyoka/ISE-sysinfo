
import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class GPUInfoPanel extends JPanel {
    private final Color BACKGROUND_COLOR = new Color(41, 41, 66);
    private final Color BORDER_COLOR = new Color(75, 75, 96);
    private final Color TEMPERATURE_GOOD = new Color(46, 204, 113);
    private final Color TEMPERATURE_WARNING = new Color(241, 196, 15);
    private final Color TEMPERATURE_CRITICAL = new Color(231, 76, 60);
    private Map<String, String> gpuInfo;
    private Timer updateTimer;

    public GPUInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title panel
        JLabel titleLabel = new JLabel("GPU Information");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // Update timer
        updateTimer = new Timer(2000, e -> updateGPUInfo(contentPanel));
        updateTimer.start();

        // Initial update
        updateGPUInfo(contentPanel);
    }

    private void updateGPUInfo(JPanel contentPanel) {
        contentPanel.removeAll();
        gpuInfo = SystemInfo.getGPUInfo();

        if (!gpuInfo.isEmpty()) {
            // Add NVIDIA GPU information if available
            if (gpuInfo.containsKey("NVIDIA Model")) {
                JPanel nvidiaPanel = createInfoPanel("NVIDIA GPU");
                addInfoRow(nvidiaPanel, "Model", gpuInfo.get("NVIDIA Model"));
                
                // Add temperature with color coding
                String temp = gpuInfo.get("NVIDIA Temp");
                if (temp != null) {
                    try {
                        int tempValue = Integer.parseInt(temp.replace("°C", "").trim());
                        Color tempColor = getTemperatureColor(tempValue);
                        addColoredInfoRow(nvidiaPanel, "Temperature", temp, tempColor);
                    } catch (NumberFormatException e) {
                        addInfoRow(nvidiaPanel, "Temperature", temp);
                    }
                }

                addInfoRow(nvidiaPanel, "Total Memory", gpuInfo.get("NVIDIA Memory Total"));
                addInfoRow(nvidiaPanel, "Used Memory", gpuInfo.get("NVIDIA Memory Used"));
                
                contentPanel.add(nvidiaPanel);
                contentPanel.add(Box.createVerticalStrut(10));
            }

            // Add other GPU information
            gpuInfo.entrySet().stream()
                   .filter(entry -> entry.getKey().startsWith("GPU "))
                   .forEach(entry -> {
                       JPanel gpuPanel = createInfoPanel("GPU " + entry.getKey().split(" ")[1]);
                       addInfoRow(gpuPanel, "Model", entry.getValue());
                       contentPanel.add(gpuPanel);
                       contentPanel.add(Box.createVerticalStrut(10));
                   });
        } else {
            addSection(contentPanel, "Error", "Could not retrieve GPU information");
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private Color getTemperatureColor(int temperature) {
        if (temperature < 60) {
            return TEMPERATURE_GOOD;
        } else if (temperature < 80) {
            return TEMPERATURE_WARNING;
        } else {
            return TEMPERATURE_CRITICAL;
        }
    }

    private JPanel createInfoPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));

        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        if (value != null && !value.isEmpty() && !value.equals("null")) {
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(BACKGROUND_COLOR);
            
            JLabel labelComponent = new JLabel(label + ":");
            labelComponent.setForeground(Color.WHITE);
            labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            JLabel valueComponent = new JLabel(value);
            valueComponent.setForeground(Color.WHITE);
            valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            rowPanel.add(labelComponent, BorderLayout.WEST);
            rowPanel.add(valueComponent, BorderLayout.EAST);
            
            panel.add(rowPanel);
            panel.add(Box.createVerticalStrut(2));
        }
    }

    private void addColoredInfoRow(JPanel panel, String label, String value, Color valueColor) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setForeground(Color.WHITE);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setForeground(valueColor);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.EAST);
        
        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(2));
    }

    private void addSection(JPanel panel, String title, String value) {
        JPanel sectionPanel = createInfoPanel(title);
        addInfoRow(sectionPanel, title, value);
        panel.add(sectionPanel);
        panel.add(Box.createVerticalStrut(10));
    }

    public void stopUpdates() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}

