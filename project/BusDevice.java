

class BusDevice {
    private int bus;
    private int device;
    private int vendorID;
    private int productID;

    public BusDevice(int bus, int device, int vendorID, int productID) {
        this.bus = bus;
        this.device = device;
        this.vendorID = vendorID;
        this.productID = productID;
    }

    public int getBus() {
        return bus;
    }

    public int getDevice() {
        return device;
    }

    public int getVendorID() {
        return vendorID;
    }

    public int getProductID() {
        return productID;
    }

    // Format the array
    @Override
    public String toString() {
        return String.format("Bus: %d, Device: %d, Vendor ID: 0x%04X, Product ID: 0x%04X",
                bus, device, vendorID, productID);
    }
}
