/*
 *  Memory information class for JNI
 *
 *  Copyright (c) 2024 Mark Burkley (mark.burkley@ul.ie)
 */

public class memInfo 
{
      
    public native void read ();
    public native int getTotal ();
    public native int getUsed ();
}

