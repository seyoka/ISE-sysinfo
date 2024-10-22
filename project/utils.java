public class utils {
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
}
