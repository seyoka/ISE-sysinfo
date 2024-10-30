import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PCIMaps {
    private HashMap<String, String> vendorMap;
    private HashMap<String, String> productMap;

    public PCIMaps() {
        vendorMap = new HashMap<>();
        productMap = new HashMap<>();
    }

    public void loadMaps(String csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
              
            br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                ArrayList<String> fields = parseCSVLine(line);
                if (fields.size() >= 4) {
                    String vendorId = fields.get(0).trim();
                    String vendorName = fields.get(1).trim();
                    String deviceId = fields.get(2).trim();
                    String deviceName = fields.get(3).trim();

                      
                    if (!vendorId.isEmpty()) {
                        vendorMap.put(vendorId, vendorName);
                    }

                      
                    if (!vendorId.isEmpty() && !deviceId.isEmpty()) {
                        String fullId = vendorId + ":" + deviceId;
                        productMap.put(fullId, deviceName);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
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
        
          
        for (int i = 0; i < fields.size(); i++) {
            String f = fields.get(i);
            if (f.startsWith("\"") && f.endsWith("\"")) {
                fields.set(i, f.substring(1, f.length() - 1));
            }
        }
        
        return fields;
    }

      
    public HashMap<String, String> getVendorMap() {
        return vendorMap;
    }

    public HashMap<String, String> getProductMap() {
        return productMap;
    }

      
    public String getVendorName(String vendorId) {
        return vendorMap.getOrDefault(vendorId, "Unknown Vendor");
    }

    public String getProductName(String vendorId, String deviceId) {
        return productMap.getOrDefault(vendorId + ":" + deviceId, "Unknown Device");
    }

      
      
      
      
        
      
      
      
        
      
      
      
        
      
      
      
      
      
}
