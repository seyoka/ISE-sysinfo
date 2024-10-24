import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CPU{
    public int cores;
    cpuInfo cpu;

    public CPU(cpuInfo cpu, int cores){
        this.cores = cores;
        this.cpu = cpu;
    }

    private int getCpuIdle(int core){
        return cpu.getIdleTime(core);
    }

    private int getCpuUser(int core){
        return cpu.getUserTime(core);
    }

    private int getCpuSystem(int core){
        return cpu.getSystemTime(core);
    }

    private int getL1dCacheSize(){
        return cpu.l1dCacheSize();
    }

    private int getL1iCacheSize(){
        return cpu.l1iCacheSize();
    }

    private int getL2CacheSize(){
        return cpu.l2CacheSize();
    }

    private int getL3CacheSize(){
        return cpu.l3CacheSize();
    }

    public HashMap<String, Integer> getAllCache(){
        HashMap<String, Integer> caches = new HashMap<String, Integer>();
        String str;

        for (int core=0; core<cores; core++){
            str = "core "+ Integer.toString(core) + " l1d cache";
            caches.put(str, getL1dCacheSize());
            str = "core "+ core + " l1i cache";
            caches.put(str, getL1iCacheSize());
        }
        caches.put("l2 cache: ", getL2CacheSize());
        caches.put("l3 cache: ", getL3CacheSize());

        return caches;
    }

    public void buildChartValues(){
        //temporary timer
        int count = 0;
        int idle;

        List<Integer> chartValues = new ArrayList<Integer>();

        while (count <10){
            int[] vals = getCoreStats(0);
            idle = vals[0];


            System.out.println(chartValues);
            if(chartValues.size() == 5){
                chartValues.removeFirst();
            }
            chartValues.add(idle);
            count++;
        }
    }

    private int[] getCoreStats(int core){
        cpu.read(1);

        //gets Idle time, User time and system Time
        int[] coreTimes = {getCpuIdle(core),
                            getCpuUser(core),
                            getCpuSystem(core)};

        return coreTimes;
    }

}
