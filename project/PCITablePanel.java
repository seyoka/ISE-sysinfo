import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;


public class PCITablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JPanel statsPanel;  // Declare statsPanel as JPanel
    private PCINetworkMapPanel networkMapPanel;

    public PCITablePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(47, 47, 68));
    
        // Initialize statsPanel and create it
        statsPanel = new JPanel();
        createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);
    
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(41, 41, 66));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(47, 47, 68));
    
        // Define columns
        String[] columns = {"Bus", "Vendor", "Product", "Functions"};
        
        // Create the model
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    
        // Create and configure table
        table = new JTable(model);
        configureTable();
        
        // Add table to its panel
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
    
        // Get real PCI data
        PCIDevArray pciArray = new PCIDevArray();
        ArrayList<PCIDevice> realDevices = pciArray.getArray();
        
        // Get total number of buses
        pciInfo pci = new pciInfo();
        pci.read();
        int totalBuses = pci.busCount();
    
        // Create network map panel with real data
        networkMapPanel = new PCINetworkMapPanel(realDevices, totalBuses); // Add this as class field: private PCINetworkMapPanel networkMapPanel;
        JScrollPane mapScrollPane = new JScrollPane(networkMapPanel);
        mapScrollPane.setBackground(new Color(47, 47, 68));
        mapScrollPane.getViewport().setBackground(new Color(47, 47, 68));
    
        // Add panels to tabbed pane
        tabbedPane.addTab("Table View", tablePanel);
        tabbedPane.addTab("Network Map", mapScrollPane);
    
        // Add tabbed pane to main panel
        add(tabbedPane, BorderLayout.CENTER);
        
        // Load PCI Data
        loadPCIData();
    }



    private void createStatsPanel(){

        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(new Color(41, 41, 66));
        
        // Get PCI information
        pciInfo pci = new pciInfo();
        pci.read();
        
        // Create labels for bus and device counts
        JLabel busCountLabel = new JLabel(String.format("Total PCI Buses: %d", pci.busCount()));
        busCountLabel.setForeground(Color.WHITE);
        
        // Get total devices across all buses
        int totalDevices = 0;
        for (int i = 0; i < pci.busCount(); i++) {
            totalDevices += pci.deviceCount(i);
        }
        JLabel deviceCountLabel = new JLabel(String.format("Total Devices: %d", totalDevices));
        deviceCountLabel.setForeground(Color.WHITE);
        
        // Add labels to stats panel
        statsPanel.add(busCountLabel);
        statsPanel.add(Box.createHorizontalStrut(20));  // spacing
        statsPanel.add(deviceCountLabel);
    }

    private void configureTable() {
        table.setBackground(new Color(41, 41, 66));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(75, 75, 96));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);

        // Get the column model
        TableColumnModel columnModel = table.getColumnModel();
        
        // Fix width for Bus and Functions columns
        columnModel.getColumn(0).setMinWidth(50);   // Bus
        columnModel.getColumn(0).setMaxWidth(50);   // Bus
        columnModel.getColumn(3).setMinWidth(70);   // Functions
        columnModel.getColumn(3).setMaxWidth(120);   // Functions

        // Center align Bus and Functions columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(new Color(41, 41, 66));
        centerRenderer.setForeground(Color.WHITE);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);  // Bus
        columnModel.getColumn(3).setCellRenderer(centerRenderer);  // Functions

        // Left align Vendor and Product columns
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBackground(new Color(41, 41, 66));
        leftRenderer.setForeground(Color.WHITE);
        columnModel.getColumn(1).setCellRenderer(leftRenderer);    // Vendor
        columnModel.getColumn(2).setCellRenderer(leftRenderer);    // Product

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(52, 50, 74));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Set alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(41, 41, 66) : new Color(45, 45, 70));
                    c.setForeground(Color.WHITE);
                }
                
                // Center alignment for Bus and Functions columns
                if (column == 0 || column == 3) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else {
                    ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
                }
                
                return c;
            }
        });

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(47, 47, 68));
        scrollPane.getViewport().setBackground(new Color(47, 47, 68));
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPCIData() {
        try {
            PCIDevArray pciArray = new PCIDevArray();
            ArrayList<PCIDevice> devices = pciArray.getArray();

            // Clear existing rows
            model.setRowCount(0);

            // Keep track of devices to combine
            String currentVendor = "";
            String currentProduct = "";
            int currentBus = -1;
            int functionCount = 0;

            for (PCIDevice device : devices) {
                // If this is a continuation of the same device
                if (currentBus == device.getBus() && 
                    currentVendor.equals(device.getVendor()) &&
                    currentProduct.equals(device.getProduct().split("Function")[0])) {
                    // Update function count
                    functionCount++;
                } else {
                    // If we had a previous device, add it to the table
                    if (currentBus != -1 && functionCount > 0) {
                        model.addRow(new Object[]{
                            currentBus,
                            currentVendor,
                            currentProduct,
                            functionCount
                        });
                    }
                    // Start tracking new device
                    currentBus = device.getBus();
                    currentVendor = device.getVendor();
                    currentProduct = device.getProduct().split("Function")[0];
                    functionCount = 1;
                }
            }
            
            // Add the last device
            if (currentBus != -1 && functionCount > 0) {
                model.addRow(new Object[]{
                    currentBus,
                    currentVendor,
                    currentProduct,
                    functionCount
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading PCI data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void refreshData() {
        loadPCIData();  // Your existing table refresh
        
        // Refresh network map with new data
        PCIDevArray pciArray = new PCIDevArray();
        ArrayList<PCIDevice> realDevices = pciArray.getArray();
        networkMapPanel.updateDevices(realDevices);
    }
}


