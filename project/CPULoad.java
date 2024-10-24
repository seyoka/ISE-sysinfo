import java.util.ArrayList;
import java.util.List;

public class CPULoad extends CPU{
    public CPULoad(cpuInfo cpu) {
        super(cpu);
    }

    //returns the percentage of time a given core spends being not idle
    private float loadPercent(int core){
        int[] vals = getCoreStats(core);

        int totalLoad = vals[0] + vals[1] + vals[2];
        int busyLoad = vals[1] + vals[2];

        return ((float)busyLoad/totalLoad) * 100;
    }

    //returns percentage of the total load of a socket
    private float totalSocketLoad(){
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

    //shows the load of a specified core
    public void buildLoadValues(int type){
        //temporary timer
        int count = 0;
        int core = 0; //change to be mutable

        List<Integer> chartValues = new ArrayList<Integer>();

        //temporary amount of time
        while (count <10){
            float val = loadPercent(core);

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
        int[] coreTimes = {cpu.getIdleTime(core),
                cpu.getUserTime(core),
                cpu.getSystemTime(core)};

        return coreTimes;
    }
}
