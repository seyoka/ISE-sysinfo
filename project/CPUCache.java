import java.util.HashMap;

public class CPUCache extends CPU{
    public CPUCache(cpuInfo cpu) {
        super(cpu);
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
}
