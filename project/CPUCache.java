import java.util.HashMap;

public class CPUCache extends CPU{
    public CPUCache(cpuInfo cpu) {
        super(cpu);
    }

    //gets all types of cache and puts it into a key/value storage
    public HashMap<String, Integer> getAllCache(){
        HashMap<String, Integer> caches = new HashMap<String, Integer>();
        String str;

        for (int core=0; core<cores; core++){
            str = "core "+ Integer.toString(core) + " l1d cache";
            caches.put(str, cpu.l1dCacheSize());
            str = "core "+ core + " l1i cache";
            caches.put(str, cpu.l1iCacheSize());
        }
        caches.put("l2 cache: ", cpu.l2CacheSize());
        caches.put("l3 cache: ", cpu.l3CacheSize());

        return caches;
    }
}
