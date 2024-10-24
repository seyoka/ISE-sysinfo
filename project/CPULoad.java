import java.util.ArrayList;
import java.util.List;

public class CPULoad extends CPU{
    public CPULoad(cpuInfo cpu) {
        super(cpu);
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
