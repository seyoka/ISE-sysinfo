import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read();

        CPU test = new CPU();
        test.getAllCache();


        //invoke later makes sure that everything runs correctly and smoothly on swing UI
        SwingUtilities.invokeLater(() -> {
            CPUCachePage example = new CPUCachePage("CPU Load Area Chart");
            example.pack();
            RefineryUtilities.centerFrameOnScreen(example);
            example.setSize(800, 600);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.setVisible(true);
        });

    }
}