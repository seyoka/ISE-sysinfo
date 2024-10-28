import org.jfree.ui.RefineryUtilities;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");
        CPU test = new CPU();
//

        //invoke later makes sure that everything runs correctly and smoothly on swing UI
        SwingUtilities.invokeLater(() -> {
            CPUCachePage example = new CPUCachePage("CPU Load Area Chart");
            example.setSize(800, 600);
            RefineryUtilities.centerFrameOnScreen(example);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.setVisible(true);
        });

    }
}