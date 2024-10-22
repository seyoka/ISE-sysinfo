public class PCIDevice{
    private String functionCount; 
    private String product; 
    private String vendor; 
    private String[] functions; 
    private String bus; 

    public String getFunctionCount(){
        return functionCount;
    }

    public String getProduct(){
        return product;
    }

    public String getVendor(){
        return vendor;
    } 

    public String[] getfunction(){
        return functions; 
    }

    public static  void printVendorProduct(int bus, int device, int function) {
        pciInfo pci = new pciInfo();
        pci.read();
        
        PCIMaps pciMaps = new PCIMaps();
        pciMaps.loadMaps("pci_devices.csv");

        String vendorIdHex = String.format("%04x", pci.vendorID(bus, device, function));
        String vendor = pciMaps.getVendorName(vendorIdHex);

        String deviceIdHex = String.format("%04x", pci.productID(bus, device, function));
        String product = pciMaps.getProductName(vendorIdHex, deviceIdHex);
        
        System.out.println("Vendor: " + vendor);
        System.out.println("Product: " + product);
    }

    public static void main(String[] args) {
        pciInfo pci = new pciInfo();
        pci.read();
        System.out.println(pci.deviceCount(0));
        System.out.println(pci.functionCount(0, 0));
        System.out.println(" "+ pci.vendorID(0, 0, 0) +" " +pci.productID(0, 0, 0));


        PCIDevice device = new PCIDevice(); 

        PCIDevice.printVendorProduct(0,0,0);
        
        
        // System.out.println("\nThis machine has "+
        //     pci.busCount()+" PCI buses ");

        // Iterate through each bus
    //     for (int i = 0; i < pci.busCount(); i++) {
    //         System.out.println("Bus "+i+" has "+
    //             pci.deviceCount(i)+" devices");

    //         // Iterate for up to 32 devices.  Not every device slot may be populated
    //         // so ensure at least one function before printing device information
    //         for (int j = 0; j < 32; j++) {
    //             if (pci.functionCount (i, j) > 0) {
    //                 System.out.println("Bus "+i+" device "+j+" has "+
    //                     pci.functionCount(i, j)+" functions");

    //                 // Iterate through up to 8 functions per device.
    //                 for (int k = 0; k < 8; k++) {
    //                     if (pci.functionPresent (i, j, k) > 0) {
    //                         System.out.println("Bus "+i+" device "+j+" function "+k+
    //                             " has vendor "+String.format("0x%04X", pci.vendorID(i,j,k))+
    //                             " and product "+String.format("0x%04X", pci.productID(i,j,k)));
    //                     }
    //                 }
    //             }
    //         }
    //     }
    // }

    }
}


