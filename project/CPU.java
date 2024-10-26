import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CPU{
    public int cores;
    public int sockets;
    cpuInfo cpu;

    public CPU(){
        this.cpu = new cpuInfo();
        this.cores = cpu.coresPerSocket();
        this.sockets = cpu.socketCount();
    }

        //gets all types of cache and puts it into a key/value storage
    public double[][] getAllCache() {
        double[][] caches = new double[cores][4];
        String str;

        //looping through cores and getting the different cache for each
        for (int core=0; core<caches.length; core++){
            caches[core][0] = cpu.l1dCacheSize();
            caches[core][1] = cpu.l1iCacheSize();
            caches[core][2] = cpu.l2CacheSize();
            caches[core][3] = cpu.l3CacheSize();

        }

        return caches;
    }

    //CPU LOAD-----------------

    //returns the percentage of time a given core spends being not idle
    private float loadPercent(int core){
        int[] vals = getCoreStats(core);

        int totalLoad = vals[0] + vals[1] + vals[2];
        int busyLoad = vals[1] + vals[2];

        return ((float)busyLoad/totalLoad) * 100;
    }

    //returns percentage of the total load of a socket - might not use
    public float totalSocketLoad(){
        int[][] coreLoads = getSocketLoadArray();
        int totalLoad = 0;
        int busyLoad = 0;

        //loops through each core and
        // //adds up how much being time is spent in total and while not in idle
        for(int[] row: coreLoads){
            totalLoad += row[0] + row[1] + row[2];
            busyLoad += row[1] + row[2];
        }

        return ((float)busyLoad/totalLoad) * 100;
    }

    private int[][] getSocketLoadArray(){
        //Contains value for the idle, system and user state for each core
        //Looks like: [(core 1)[idle, user, system],
        //            (core 2))[idle, user, system]] and so on
        int[][] coreLoads = new int[cores][3];

        for (int i=0; i<cores; i++){
            coreLoads[0] = getCoreStats(i);
        }

        return coreLoads;
    }

    private int[] getCoreStats(int core){
        cpu.read(1);

        //gets Idle time, User time and system Time
        int[] coreTimes = {cpu.getIdleTime(core),
                cpu.getUserTime(core),
                cpu.getSystemTime(core)};

        return coreTimes;
    }


}
