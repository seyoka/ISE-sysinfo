import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary("sysinfo");

        cpuInfo cpu = new cpuInfo();
        cpu.read(0);
        String name = cpu.getModel();

        CPUCache help = new CPUCache(cpu);

        System.out.println("core 1");

        help.getAllCache();
        int i = 0;

    }
}