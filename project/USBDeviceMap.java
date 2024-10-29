import java.util.*;

public class USBDeviceMap {
    private static final Map<String, String> vendorMap;
    private static final Map<String, Map<String, String>> productMap;

    static {
        vendorMap = new HashMap<>();
        productMap = new HashMap<>();

        // Common USB Vendor IDs
        vendorMap.put("0x1D6B", "Linux Foundation");
        vendorMap.put("0x8087", "Intel Corp.");
        vendorMap.put("0x0B05", "ASUSTek Computer, Inc.");
        vendorMap.put("0x05E3", "Genesys Logic, Inc.");
        vendorMap.put("0x1B1C", "Corsair");
        
        // Product IDs for Linux Foundation
        Map<String, String> linuxProducts = new HashMap<>();
        linuxProducts.put("0x0002", "2.0 root hub");
        linuxProducts.put("0x0003", "3.0 root hub");
        productMap.put("0x1D6B", linuxProducts);
        
        // Product IDs for Intel
        Map<String, String> intelProducts = new HashMap<>();
        intelProducts.put("0x0029", "Intel Wireless Device");
        productMap.put("0x8087", intelProducts);
        
        // Product IDs for ASUS
        Map<String, String> asusProducts = new HashMap<>();
        asusProducts.put("0x1939", "ASUS USB Device");
        productMap.put("0x0B05", asusProducts);
        
        // Product IDs for Genesys
        Map<String, String> genesysProducts = new HashMap<>();
        genesysProducts.put("0x0610", "USB 2.0 Hub");
        productMap.put("0x05E3", genesysProducts);
        
        // Product IDs for Corsair
        Map<String, String> corsairProducts = new HashMap<>();
        corsairProducts.put("0x1B79", "Corsair USB Device");
        corsairProducts.put("0x1BFE", "Corsair Gaming Device");
        corsairProducts.put("0x0C1A", "Corsair USB Peripheral");
        productMap.put("0x1B1C", corsairProducts);
    }

    public static String getVendorName(String vendorId) {
        return vendorMap.getOrDefault(vendorId.toUpperCase(), "Unknown Vendor");
    }

    public static String getProductName(String vendorId, String productId) {
        Map<String, String> products = productMap.get(vendorId.toUpperCase());
        if (products != null) {
            return products.getOrDefault(productId.toUpperCase(), "Unknown Product");
        }
        return "Unknown Product";
    }
}