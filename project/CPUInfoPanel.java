<<<<<<< HEAD
import java.awt.*;
import javax.swing.*;

public class CPUInfoPanel extends JPanel {
    private final Color MAIN_CONTENT_COLOR = new Color(47, 47, 68);
    private cpuInfo cpu; // Make it a class member

    static {
        try {
            System.loadLibrary("sysinfo");
            System.out.println("CPU Panel: Successfully loaded sysinfo library");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("CPU Panel: Failed to load library: " + e.getMessage());
        }
    }
=======
import javax.swing.*;
import java.awt.*;

public class CPUInfoPanel extends JPanel {
    private final Color MAIN_CONTENT_COLOR = new Color(47, 47, 68);

>>>>>>> 4594384636ee83ecd8755db47f58b30a6f9a0f63

    public CPUInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_CONTENT_COLOR);

<<<<<<< HEAD
        // Initialize CPU info once
        try {
            cpu = new cpuInfo();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to create cpuInfo: " + e.getMessage());
        }

=======
>>>>>>> 4594384636ee83ecd8755db47f58b30a6f9a0f63
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(MAIN_CONTENT_COLOR);

        containerPanel.add(createTextPanel());
        containerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        containerPanel.add(createCpuInfoPanel());
<<<<<<< HEAD

        add(containerPanel, BorderLayout.NORTH);
=======
        containerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        containerPanel.add(createCpuLoadPanel());

        add(containerPanel, BorderLayout.NORTH);

>>>>>>> 4594384636ee83ecd8755db47f58b30a6f9a0f63
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
<<<<<<< HEAD
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return label;
    }

    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(41, 41, 66));

        try {
            cpu.read();
            panel.add(createStyledLabel(cpu.getModel()));
        } catch (Exception e) {
            System.err.println("Error reading CPU info: " + e.getMessage());
            panel.add(createStyledLabel("Error reading CPU information"));
        }
=======
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        return label;
    }

    private JPanel createTextPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(41, 41, 66));

        cpuInfo cpuName = new cpuInfo();
        cpuName.read();
        panel.add(createStyledLabel(cpuName.getModel()));
>>>>>>> 4594384636ee83ecd8755db47f58b30a6f9a0f63

        return panel;
    }

<<<<<<< HEAD
    private JPanel createCpuInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(41, 41, 66));

        try {
            CPUCachePage cpuCache = new CPUCachePage();
            panel.add(cpuCache);
        } catch (Exception e) {
            System.err.println("Error creating CPU cache page: " + e.getMessage());
            panel.add(createStyledLabel("Error displaying CPU cache information"));
        }

        return panel;
    }
}
=======
    private JPanel createCpuInfoPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(41, 41, 66));

        CPUCachePage cpuCache = new CPUCachePage();
        panel.add(cpuCache);

        return panel;
    }

    private JPanel createCpuLoadPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(41, 41, 66));

        CPULoadChart cpuLoad = new CPULoadChart();
        panel.add(cpuLoad);

        return panel;
    }
}
>>>>>>> 4594384636ee83ecd8755db47f58b30a6f9a0f63
