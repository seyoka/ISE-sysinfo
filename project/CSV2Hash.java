import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CSV2Hash {
    private HashMap<String, String> vendorMap;  // Vendor ID -> Vendor Name
    private HashMap<String, String> deviceMap;  // Vendor ID -> Device Description

    public CSV2Hash() {
        vendorMap = new HashMap<>();
        deviceMap = new HashMap<>();
    }

    public void parseCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                ArrayList<String> fields = parseCSVLine(line);
                
                String vendorId = fields.get(0).trim();
                String description = fields.get(1).replace("\"", "").trim();
                
                if (!vendorId.isEmpty() && !description.isEmpty()) {
                    // Add to vendor map
                    vendorMap.put(vendorId, description);
                    
                    // If the description looks like a device description (contains controller, bridge, ethernet, etc.)
                    // add it to device map as well
                    if (isDeviceDescription(description)) {
                        deviceMap.put(vendorId, description);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    private boolean isDeviceDescription(String description) {
        String lowerDesc = description.toLowerCase();
        String[] deviceKeywords = {
            "controller", "bridge", "ethernet", "gpu", "audio", "display",
            "usb", "sata", "network", "adapter", "processor"
        };
        
        for (String keyword : deviceKeywords) {
            if (lowerDesc.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> parseCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().trim());
        
        while (fields.size() < 7) {
            fields.add("");
        }
        
        return fields;
    }

    // Test method for specific ID
    public void testId(String id) {
        System.out.println("\nTesting ID: " + id);
        if (vendorMap.containsKey(id)) {
            System.out.println("Vendor Map: " + id + " -> " + vendorMap.get(id));
        } else {
            System.out.println("ID " + id + " not found in vendor map");
        }
        
        if (deviceMap.containsKey(id)) {
            System.out.println("Device Map: " + id + " -> " + deviceMap.get(id));
        } else {
            System.out.println("ID " + id + " not found in device map");
        }
    }

    public HashMap<String, String> getVendorMap() {
        return vendorMap;
    }

    public HashMap<String, String> getDeviceMap() {
        return deviceMap;
    }

    public static void main(String[] args) {
        CSV2Hash parser = new CSV2Hash();
        parser.parseCSV("pci_ids.csv");

        // Print vendor mappings
        System.out.println("\nVendor Mappings:");
        parser.getVendorMap().forEach((id, name) -> 
            System.out.println("Vendor ID: " + id + " -> " + name));

        // Print device mappings
        System.out.println("\nDevice Mappings:");
        parser.getDeviceMap().forEach((id, name) -> 
            System.out.println("Device ID: " + id + " -> " + name));

        // Test specific ID
        parser.testId("0010");
        parser.testId("7a00");  // Testing a known device entry
        
        // Print statistics
        System.out.println("\nStatistics:");
        System.out.println("Vendor Map Size: " + parser.getVendorMap().size());
        System.out.println("Device Map Size: " + parser.getDeviceMap().size());
    }
}