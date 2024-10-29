#!/bin/bash

# Clean existing class files
rm -f *.class

# Set classpath
CLASSPATH=".:lib/*:libsysinfo/libsysinfo.so"

# Copy the libsysinfo.so to current directory
echo "Copying library file..."
cp libsysinfo/libsysinfo.so .

# Make sure library is executable
chmod +x libsysinfo.so

# Compile base classes
echo "Compiling base classes..."
javac -cp $CLASSPATH SystemInfo.java BusDevice.java USBDeviceMap.java cpuInfo.java memInfo.java diskInfo.java usbInfo.java pciInfo.java

# Compile panels
echo "Compiling panels..."
javac -cp $CLASSPATH CPUInfoPanel.java CPUCachePage.java RAMInfoPanel.java GPUInfoPanel.java DiskInfoPanel.java USBInfoPanel.java GeneralInfoPanel.java

# Compile main app
echo "Compiling main application..."
javac -cp $CLASSPATH DashboardApp.java

# Create debug class
echo "Creating debug class..."
cat > LibraryTest.java << EOF
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
EOF

javac LibraryTest.java

# Run test first
echo "Testing library loading..."
java -Djava.library.path=.:./libsysinfo LibraryTest

# If test successful, run main app
if [ $? -eq 0 ]; then
    echo "Library test successful. Running main application..."
    java -Djava.library.path=.:./libsysinfo -cp .:lib/*:. DashboardApp
else
    echo "Library test failed!"
fi
