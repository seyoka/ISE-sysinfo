import javax.swing.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read();


        //invoke later makes sure that everything runs correctly and smoothly on swing UI
        SwingUtilities.invokeLater(() -> {
            CPULoadChart example = new CPULoadChart("CPU Load Area Chart");
            example.setSize(800, 600);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.setVisible(true);
        });

    }
}