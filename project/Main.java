import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read(0);
        String name = cpu.getModel();

        CPU help = new CPU(cpu);

        System.out.println("core 1");

        //help.getAllCache();

        SwingUtilities.invokeLater(() -> {
            CPUPage example = new CPUPage("Line Chart Example");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });

    }
}