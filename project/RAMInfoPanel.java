
import java.awt.*;
import java.util.Map;
import javax.swing.*;



public class RAMInfoPanel extends JPanel {
    private final Color BACKGROUND_COLOR = new Color(41, 41, 66);
    private final Color BORDER_COLOR = new Color(75, 75, 96);
    private Map<String, String> ramInfo;
    private Timer updateTimer;

    public RAMInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title panel at the top
        JLabel titleLabel = new JLabel("RAM Information");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Content panel for RAM info
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // Update timer
        updateTimer = new Timer(5000, e -> updateRAMInfo(contentPanel));
        updateTimer.start();

        // Initial update
        updateRAMInfo(contentPanel);
    }

    private void updateRAMInfo(JPanel contentPanel) {
        contentPanel.removeAll();
        Map<String, String> ramInfo = SystemInfo.getRAMInfo();
    
        // Add Total RAM section
        JPanel totalRAMPanel = createInfoPanel("Total RAM");
        if (ramInfo.containsKey("Total RAM")) {
            addInfoRow(totalRAMPanel, "Capacity", ramInfo.get("Total RAM"));
        }
        if (ramInfo.containsKey("Usage")) {
            addInfoRow(totalRAMPanel, "Usage", ramInfo.get("Usage"));
        }
        if (ramInfo.containsKey("Available RAM")) {
            addInfoRow(totalRAMPanel, "Available", ramInfo.get("Available RAM"));
        }
        contentPanel.add(totalRAMPanel);
        contentPanel.add(Box.createVerticalStrut(10));
    
        // Add info for each memory bank
        for (String key : ramInfo.keySet()) {
            if (key.startsWith("Bank") && key.endsWith("Size")) {
                String bankPrefix = key.substring(0, key.length() - 5);  // Remove " Size"
                if (!ramInfo.get(key).contains("No Module Installed")) {
                    JPanel bankPanel = createInfoPanel(bankPrefix);
                    
                    // Add all available information for this bank
                    String[] fields = {"Size", "Type", "Speed", "Manufacturer", 
                                     "Form Factor", "Data Width", "Slot"};
                    
                    for (String field : fields) {
                        String infoKey = bankPrefix + " " + field;
                        if (ramInfo.containsKey(infoKey)) {
                            addInfoRow(bankPanel, field, ramInfo.get(infoKey));
                        }
                    }
                    
                    contentPanel.add(bankPanel);
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
    
        contentPanel.revalidate();
        contentPanel.repaint();
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

    private void addSection(JPanel panel, String title, String value) {
        JPanel sectionPanel = createInfoPanel(title);
        addInfoRow(sectionPanel, title, value);
        panel.add(sectionPanel);
        panel.add(Box.createVerticalStrut(10));
    }

    private void addBankInfo(JPanel panel, String bankPrefix, Map<String, String> info) {
        if (info.containsKey(bankPrefix + " Size") && !info.get(bankPrefix + " Size").equals("No Module Installed")) {
            JPanel bankPanel = createInfoPanel("Memory " + bankPrefix);
            
            // Add all available information for this bank
            String[] fields = {
                "Size", "Type", "Speed", "Manufacturer", 
                "Form Factor", "Data Width", "Slot"
            };
            
            for (String field : fields) {
                String key = bankPrefix + " " + field;
                if (info.containsKey(key)) {
                    addInfoRow(bankPanel, field, info.get(key));
                }
            }
            
            panel.add(bankPanel);
            panel.add(Box.createVerticalStrut(10));
        }
    }

    public void stopUpdates() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}

