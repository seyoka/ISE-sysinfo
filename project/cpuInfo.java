/*
 *  CPU information class for JNI
 *
 *  Copyright (c) 2024 Mark Burkley (mark.burkley@ul.ie)
 */

public class cpuInfo 
{
      
    public native void read (int seconds);
    public native void read ();

      
    public native int coresPerSocket ();

      
    public native int socketCount ();

      
    public native String getModel ();

      
    public native int l1dCacheSize ();

      
    public native int l1iCacheSize ();

      
    public native int l2CacheSize ();

      
    public native int l3CacheSize ();

      
      
    public native int getUserTime (int core);

      
      
    public native int getIdleTime (int core);

      
      
    public native int getSystemTime (int core);
}
