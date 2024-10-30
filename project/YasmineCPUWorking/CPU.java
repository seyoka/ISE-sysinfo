import java.util.*;

public class CPU{
    public int cores;
    public int sockets;
    cpuInfo cpu;

    public CPU(){
        this.cpu = new cpuInfo();
        cpu.read(0);
        this.cores = cpu.coresPerSocket();
        this.sockets = cpu.socketCount();
    }

      
    public LinkedHashMap<String, Integer> getAllCache() {
        cpu.read(1);

        LinkedHashMap<String, Integer> caches = new LinkedHashMap<>();
        caches.put("L1i", cpu.l1iCacheSize());
        caches.put("L1d", cpu.l1dCacheSize());
        caches.put("L2", cpu.l2CacheSize());
        caches.put("L3", cpu.l3CacheSize());

        return caches;
    }

      

      
    private float loadPercent(int core){
        int[] vals = getCoreStats(core);

        int totalLoad = vals[0] + vals[1] + vals[2];
        int busyLoad = vals[1] + vals[2];

        return ((float)busyLoad/totalLoad) * 100;
    }

      
    public float totalSocketLoad(){
        int[][] coreLoads = getSocketLoadArray();
        int totalLoad = 0;
        int busyLoad = 0;

          
          
        for(int[] row: coreLoads){
            totalLoad += row[0] + row[1] + row[2];
            busyLoad += row[1] + row[2];
        }

        return ((float)busyLoad/totalLoad) * 100;
    }

    private int[][] getSocketLoadArray(){
          
          
          
        int[][] coreLoads = new int[cores][3];

        for (int i=0; i<cores; i++){
            coreLoads[0] = getCoreStats(i);
        }

        return coreLoads;
    }

    private int[] getCoreStats(int core){
        cpu.read(1);

          
        int[] coreTimes = {cpu.getIdleTime(core),
                cpu.getUserTime(core),
                cpu.getSystemTime(core)};

        return coreTimes;
    }


}
