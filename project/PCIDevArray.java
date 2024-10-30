import java.util.ArrayList;

public class PCIDevArray {
    private ArrayList<PCIDevice> deviceArray = new ArrayList<>(); 

    public ArrayList<PCIDevice> getArray() {
        pciInfo pci = new pciInfo();
        pci.read();
        PCIMaps map = new PCIMaps(); 
        map.loadMaps("pci_devices.csv"); 
        
        System.out.println("\nThis machine has " + pci.busCount() + " PCI buses ");
    
        for (int i = 0; i < pci.busCount(); i++) {
            for (int j = 0; j < 32; j++) {
                if (pci.functionCount(i, j) > 0) {
                    int functionNumber = pci.functionCount(i, j);
                    for (int k = 0; k < 8; k++) {
                        if (pci.functionPresent(i, j, k) > 0) {
                              
                            int vendorID = pci.vendorID(i, j, k);
                            int deviceID = pci.productID(i, j, k);
                            
                              
                            String vendorIDHex = String.format("%04x", vendorID);
                            String deviceIDHex = String.format("%04x", deviceID);
                            
                              
                            System.out.println("Debug - Raw IDs: " + 
                                             "Vendor: 0x" + vendorIDHex + 
                                             " Device: 0x" + deviceIDHex);
                            
                            String vendorName = map.getVendorName(vendorIDHex);
                            String deviceName = map.getProductName(vendorIDHex, deviceIDHex);
                            
                              
                            System.out.println("Debug - Lookup Results: " + 
                                             "Vendor: " + vendorName + 
                                             " Device: " + deviceName);
    
                            PCIDevice device = new PCIDevice(i, vendorName, deviceName, functionNumber);
                            deviceArray.add(device);
                        }
                    }
                }
            }
        }
    
        return deviceArray; 
    }
      
      
      
      
      
        


      
      

      
      
      
      

      
      
      
      
      
      

      
      
      
      
      
      
      

                                
      
      

      
      


                                

      
      
      
      
      
      
      

      
      

    public static void main(String[] args) {
        PCIDevArray pciArray = new PCIDevArray();
        ArrayList<PCIDevice> devices = pciArray.getArray();

        System.out.println("\nPCI Devices Found:");
        for (PCIDevice device : devices) {
            System.out.println("Bus: " + device.getBus());
            System.out.println("Vendor: " + device.getVendor());
            System.out.println("Product: " + device.getProduct());
            System.out.println("Functions: " + device.getFunctionCount());
            System.out.println("------------------------");
        }
    }

}
