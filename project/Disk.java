public class Disk {
    private String name;
    private long total;
    private long used;
    private long available;

    public Disk(String name, long total, long used, long available) {
        this.name = name;
        this.total = total;
        this.used = used;
        this.available = available;
    }

      
    public String getName() { return name; }
    public long getTotal() { return total; }
    public long getUsed() { return used; }
    public long getAvailable() { return available; }
    
      
    public double getUsagePercentage() {
        return (double) used / total * 100;
    }

    @Override
    public String toString() {
        return "Disk [name=" + name + 
               ", total=" + total + 
               ", used=" + used + 
               ", available=" + available + "]";
    }
}
