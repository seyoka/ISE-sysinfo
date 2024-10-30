import java.io.*;
import java.util.*;

public class USBDeviceMap {
    private static final Map<String, String> vendorMap = new HashMap<>();
    private static final Map<String, Map<String, String>> productMap = new HashMap<>();
    private static final Map<String, String> deviceClassMap = new HashMap<>();
    
    static {
          
        deviceClassMap.put("00", "Per Interface");
        deviceClassMap.put("01", "Audio");
        deviceClassMap.put("02", "Communications and CDC Control");
        deviceClassMap.put("03", "Human Interface Device (HID)");
        deviceClassMap.put("05", "Physical Interface Device");
        deviceClassMap.put("06", "Image");
        deviceClassMap.put("07", "Printer");
        deviceClassMap.put("08", "Mass Storage");
        deviceClassMap.put("09", "Hub");
        deviceClassMap.put("0A", "CDC-Data");
        deviceClassMap.put("0B", "Smart Card");
        deviceClassMap.put("0D", "Content Security");
        deviceClassMap.put("0E", "Video");
        deviceClassMap.put("0F", "Personal Healthcare");
        deviceClassMap.put("10", "Audio/Video Devices");
        deviceClassMap.put("11", "Billboard Device");
        deviceClassMap.put("12", "USB Type-C Bridge");
        deviceClassMap.put("DC", "Diagnostic Device");
        deviceClassMap.put("E0", "Wireless Controller");
        deviceClassMap.put("EF", "Miscellaneous");
        deviceClassMap.put("FE", "Application Specific");
        deviceClassMap.put("FF", "Vendor Specific");

          
        loadUSBIds();
    }

    private static void loadUSBIds() {
        try (BufferedReader reader = new BufferedReader(new FileReader("usb.ids"))) {
            String line;
            String currentVendor = null;
            Map<String, String> currentProducts = null;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }

                  
                if (!line.startsWith("\t")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length == 2) {
                        currentVendor = parts[0].toLowerCase();
                        vendorMap.put(currentVendor, parts[1].trim());
                        currentProducts = new HashMap<>();
                        productMap.put(currentVendor, currentProducts);
                    }
                }

                else if (line.startsWith("\t") && !line.startsWith("\t\t") && currentVendor != null) {
                    String[] parts = line.trim().split("\\s+", 2);
                    if (parts.length == 2) {
                        currentProducts.put(parts[0].toLowerCase(), parts[1].trim());
                    }
                }
               
            }
        } catch (IOException e) {
            System.err.println("Error loading USB IDs: " + e.getMessage());
        }
    }

    public static String getVendorName(String vendorId) {
        return vendorMap.getOrDefault(vendorId.toLowerCase(), "Unknown Vendor");
    }

    public static String getProductName(String vendorId, String productId) {
        Map<String, String> products = productMap.get(vendorId.toLowerCase());
        if (products != null) {
            return products.getOrDefault(productId.toLowerCase(), "Unknown Product");
        }
        return "Unknown Product";
    }

    public static String getDeviceClass(String classCode) {
        return deviceClassMap.getOrDefault(classCode.toUpperCase(), "Unknown Class");
    }

    
    public static String formatId(int id) {
        return String.format("0x%04x", id).toLowerCase();
    }
}