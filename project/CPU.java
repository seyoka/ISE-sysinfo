import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CPU{
    public int cores;
    cpuInfo cpu;
    String name;

    public CPU(cpuInfo cpu, int cores, String name){
        this.cores = cores;
        this.cpu = cpu;
        this.name = name;
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

    //gets all types of cache and puts it into a key/value storage
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

    //returns the percentage of time the CPU spends being not idle
    private float loadPercent(int core){
        int[] vals = getCoreStats(core);

        int totalLoad = vals[0] + vals[1] + vals[2];
        int busyLoad = vals[1] + vals[2];

        return ((float)busyLoad/totalLoad) * 100;
    }

    //shows the load of a specified core
    public void buildLoadValues(int type){
        //temporary timer
        int count = 0;
        int core = 0; //change to be mutable

        List<Integer> chartValues = new ArrayList<Integer>();

        while (count <10){
            float val = loadPercent(core);

            System.out.println(chartValues);
            if(chartValues.size() == 5){
                chartValues.removeFirst();
            }

            //originally a float, rounded to be an int
            chartValues.add(Math.round(val));
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
