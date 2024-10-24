import java.util.ArrayList;
import java.util.List;

public class CPU{
    public int cores;
    public int sockets;
    cpuInfo cpu;

    public CPU(cpuInfo cpu){
        this.cpu = cpu;
        this.cores = cpu.coresPerSocket();
        this.sockets = cpu.socketCount();
    }

    //model name of the cpu
    public String getCPUModel(){
        return cpu.getModel();
    }

    //the amount of cores in 1 cpu
    public int getCPUCores(){return cores;}

    //gets socket count (cpu amount) of computer - probably going to be 1
    public int getSocketCount(){return sockets;}

}
