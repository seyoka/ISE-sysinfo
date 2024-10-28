
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import com.sun.jna.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;



public class GeneralInfoPanel extends JPanel {
    private JPanel systemInfoPanel;
    private CacheSpeedPanel cachePanel;
    private final Color MAIN_CONTENT_COLOR = new Color(47, 47, 68);
    private DecimalFormat df = new DecimalFormat("#.##");
    private JLabel osLabel;
    private JLabel cpuLabel;
    private JLabel memoryLabel;
    private JLabel powerLabel;
    private RAMInfoPanel ramInfoPanel;
    private GPUInfoPanel gpuInfoPanel;

    public GeneralInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_CONTENT_COLOR);
    
        // Create main scroll pane container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(MAIN_CONTENT_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
    
        // Configure base constraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
    
        // Add system info panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        systemInfoPanel = createSystemInfoPanel();
        mainContainer.add(systemInfoPanel, gbc);
    
        // Add RAM info panel (left side)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        ramInfoPanel = new RAMInfoPanel();
        mainContainer.add(ramInfoPanel, gbc);
    
        // Add GPU info panel (right side)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gpuInfoPanel = new GPUInfoPanel();
        mainContainer.add(gpuInfoPanel, gbc);
    
        // Add cache visualization panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        cachePanel = new CacheSpeedPanel();
        mainContainer.add(cachePanel, gbc);
    
        // Add everything to a scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBackground(MAIN_CONTENT_COLOR);
        scrollPane.getViewport().setBackground(MAIN_CONTENT_COLOR);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    
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
            File batteryPath = new File("/sys/class/power_supply/BAT0");
            if (batteryPath.exists()) {
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

    public void cleanup() {
        if (cachePanel != null) {
            cachePanel.stopAnimation();
        }
        if (ramInfoPanel != null) {
            ramInfoPanel.stopUpdates();
        }
        if (gpuInfoPanel != null) {
            gpuInfoPanel.stopUpdates();
        }
    }
}
