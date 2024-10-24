import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read(0);
        String name = cpu.getModel();

        CPU help = new CPU(cpu, cpu.coresPerSocket(), name);

        System.out.println("core 1");
        help.buildLoadValues(0);

        help.getAllCache();
        int i = 0;

    }
}