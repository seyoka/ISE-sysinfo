import javax.swing.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read(0);
        CPU help = new CPU(cpu);

        //invoke later makes sure that everything runs correctly and smoothly on swing UI
        SwingUtilities.invokeLater(() -> {
            CPULoadChart cpuLoad = new CPULoadChart("CPU Load Chart Example");
            cpuLoad.setSize(800, 600);
            //Specifying that application exits when window closes (so it doesnt keep running)
            //Other methods: HIDE_ON_CLOSE, DISPOSE_ON_CLOSE
            cpuLoad.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            cpuLoad.setLocationRelativeTo(null);
            cpuLoad.setVisible(true);
        });

    }
}