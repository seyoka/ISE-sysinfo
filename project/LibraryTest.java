public class LibraryTest {
    static {
        try {
            System.loadLibrary("sysinfo");
            System.out.println("Successfully loaded sysinfo library");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load library: " + e.getMessage());
            System.err.println("java.library.path = " + System.getProperty("java.library.path"));
        }
    }

    public static void main(String[] args) {
        cpuInfo cpu = new cpuInfo();
        try {
            cpu.read();
            System.out.println("Successfully read CPU info");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to read CPU info: " + e.getMessage());
        }
    }
}
