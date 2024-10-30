import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        JFrame window = new JFrame("Disk Menu");
        window.setSize(640, 640);
        window.add(DiskMenu.innitDiskMenu());
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}