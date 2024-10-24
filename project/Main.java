import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read(0);

        CPU help = new CPU(cpu, cpu.coresPerSocket());
        help.buildChartValues();
        int i = 0;
        help.getAllCache();

    }
}