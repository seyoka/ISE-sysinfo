public class PCIDevice{
    private int functionCount; 
    private String product; 
    private String vendor; 
    private int functions; 
    private int bus; 


    public PCIDevice(int bus, String vendor, String product, int  functions){
        this.bus = bus; 
        this.vendor = vendor;
        this.product = product; 
        this.functions = functionCount; 
    }


    public int getBus(){
        return bus; 
    }

    public int getFunctionCount(){
        return functionCount;
    }

    public String getProduct(){
        return product;
    }

    public String getVendor(){
        return vendor;
    } 

    public int  getfunction(){
        return functions; 
    }
}