import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SystemInfo {
    public static Map<String, String> getRAMInfo() {
        Map<String, String> ramInfo = new HashMap<>();
        
        try {
            // Get total RAM from /proc/meminfo
            BufferedReader meminfo = new BufferedReader(new FileReader("/proc/meminfo"));
            String line;
            long totalKB = 0;
            long availableKB = 0;
            
            while ((line = meminfo.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    totalKB = Long.parseLong(line.split("\\s+")[1]);
                    ramInfo.put("Total RAM", String.format("%.2f GB", totalKB / 1024.0 / 1024.0));
                }
                if (line.startsWith("MemAvailable:")) {
                    availableKB = Long.parseLong(line.split("\\s+")[1]);
                    ramInfo.put("Available RAM", String.format("%.2f GB", availableKB / 1024.0 / 1024.0));
                }
            }
            meminfo.close();
            
            // Calculate usage percentage
            double usagePercent = ((totalKB - availableKB) / (double)totalKB) * 100;
            ramInfo.put("Usage", String.format("%.1f%%", usagePercent));

            // Get detailed RAM information using dmidecode
            Process dmidecode = new ProcessBuilder("sudo", "dmidecode", "-t", "memory").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(dmidecode.getInputStream()));
            int bankCount = 0;
            String currentBank = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.startsWith("Memory Device")) {
                    if (currentBank != null) {
                        bankCount++;
                    }
                    currentBank = "Bank " + bankCount;
                    continue;
                }
                
                if (currentBank != null) {
                    if (line.startsWith("Size:")) {
                        ramInfo.put(currentBank + " Size", line.substring(6).trim());
                    } else if (line.startsWith("Type:")) {
                        ramInfo.put(currentBank + " Type", line.substring(6).trim());
                    } else if (line.startsWith("Speed:")) {
                        ramInfo.put(currentBank + " Speed", line.substring(7).trim());
                    } else if (line.startsWith("Manufacturer:")) {
                        ramInfo.put(currentBank + " Manufacturer", line.substring(13).trim());
                    } else if (line.startsWith("Form Factor:")) {
                        ramInfo.put(currentBank + " Form Factor", line.substring(13).trim());
                    } else if (line.startsWith("Data Width:")) {
                        ramInfo.put(currentBank + " Data Width", line.substring(12).trim());
                    } else if (line.startsWith("Locator:")) {
                        ramInfo.put(currentBank + " Slot", line.substring(9).trim());
                    }
                }
            }
            reader.close();
            
        } catch (Exception e) {
            ramInfo.put("Error", "Could not retrieve RAM information: " + e.getMessage());
        }
        
        return ramInfo;
    }

    public static Map<String, String> getGPUInfo() {
        Map<String, String> gpuInfo = new HashMap<>();
        
        try {
            // Use lspci with more detailed output
            Process process = new ProcessBuilder("lspci", "-v", "-nn").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder currentGpu = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                // Look for both VGA and 3D controllers
                if (line.toLowerCase().contains("vga") || 
                    line.toLowerCase().contains("display") ||
                    line.toLowerCase().contains("3d") ||
                    line.toLowerCase().contains("graphics")) {
                        
                    if (currentGpu.length() > 0) {
                        // Store previous GPU info if exists
                        gpuInfo.put("GPU " + gpuInfo.size(), currentGpu.toString().trim());
                        currentGpu = new StringBuilder();
                    }
                    currentGpu.append(line.split(":")[2].trim());
                }
                // Collect additional info for current GPU
                else if (currentGpu.length() > 0 && line.trim().startsWith("Kernel driver in use:")) {
                    gpuInfo.put("Driver", line.split(":")[1].trim());
                }
            }
            // Add last GPU if exists
            if (currentGpu.length() > 0) {
                gpuInfo.put("GPU " + gpuInfo.size(), currentGpu.toString().trim());
            }
            reader.close();
    
            // Try to get NVIDIA specific info if available
            try {
                Process nvidiaSmi = new ProcessBuilder(
                    "nvidia-smi", 
                    "--query-gpu=name,temperature.gpu,memory.total,memory.used,memory.free,utilization.gpu",
                    "--format=csv,noheader,nounits"
                ).start();
                
                reader = new BufferedReader(new InputStreamReader(nvidiaSmi.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    String[] info = line.split(",");
                    if (info.length >= 6) {
                        gpuInfo.put("Name", info[0].trim());
                        gpuInfo.put("Temperature", info[1].trim() + "°C");
                        gpuInfo.put("Total Memory", info[2].trim() + " MB");
                        gpuInfo.put("Used Memory", info[3].trim() + " MB");
                        gpuInfo.put("Free Memory", info[4].trim() + " MB");
                        gpuInfo.put("GPU Utilization", info[5].trim() + "%");
                    }
                }
                reader.close();
            } catch (Exception e) {
                // NVIDIA tools not available or failed - try AMD
                try {
                    Process rocmSmi = new ProcessBuilder("rocm-smi").start();
                    reader = new BufferedReader(new InputStreamReader(rocmSmi.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("GPU")) {
                            gpuInfo.put("AMD Info", line.trim());
                        }
                    }
                    reader.close();
                } catch (Exception ex) {
                    // AMD tools not available
                }
            }
            
            // If we still don't have detailed info, try glxinfo
            if (!gpuInfo.containsKey("Name")) {
                try {
                    Process glxinfo = new ProcessBuilder("glxinfo", "-B").start();
                    reader = new BufferedReader(new InputStreamReader(glxinfo.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("OpenGL renderer string")) {
                            gpuInfo.put("Renderer", line.split(":")[1].trim());
                        }
                        if (line.contains("OpenGL version string")) {
                            gpuInfo.put("OpenGL Version", line.split(":")[1].trim());
                        }
                    }
                    reader.close();
                } catch (Exception e) {
                    // glxinfo not available
                }
            }
    
        } catch (Exception e) {
            gpuInfo.put("Error", "Could not retrieve GPU information: " + e.getMessage());
        }
        
        return gpuInfo;
    }
}