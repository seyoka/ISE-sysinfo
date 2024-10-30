/*
 *  PCI information class for JNI
 *
 *  Copyright (c) 2024 Mark Burkley (mark.burkley@ul.ie)
 */

public class pciInfo 
{
    static {
        System.loadLibrary("sysinfo");   
    }
      
    public native void read ();

      
    public native int busCount ();

      
    public native int deviceCount (int bus);

      
    public native int functionCount (int bus, int device);

      
    public native int functionPresent (int bus, int device, int function);

      
    public native int vendorID (int bus, int device, int function);

      
    public native int productID (int bus, int device, int function);
}

