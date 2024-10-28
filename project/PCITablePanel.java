
// import java.awt.*;
// import java.util.ArrayList;
// import javax.swing.*;
// import javax.swing.event.DocumentEvent;
// import javax.swing.event.DocumentListener;
// import javax.swing.table.*;
// import java.util.Set;
// import java.util.TreeSet;
// import java.util.Arrays;

// public class PCITablePanel extends JPanel {
//     private JTable table;
//     private DefaultTableModel model;
//     private JPanel statsPanel;
//     private PCINetworkMapPanel networkMapPanel;
//     private JPanel tablePanel;
//     private JTextField searchField;
//     private final Color BACKGROUND_COLOR = new Color(47, 47, 68);
//     private final Color DARKER_BG = new Color(41, 41, 66);
//     private final Color BORDER_COLOR = new Color(75, 75, 96);
//     private final Color HEADER_COLOR = new Color(52, 50, 74);

//     public PCITablePanel() {
//         setLayout(new BorderLayout());
//         setBackground(BACKGROUND_COLOR);

//         // Initialize statsPanel and create it
//         statsPanel = new JPanel();
//         createStatsPanel();
//         add(statsPanel, BorderLayout.NORTH);

//         // Create tabbed pane
//         JTabbedPane tabbedPane = new JTabbedPane();
//         tabbedPane.setBackground(DARKER_BG);
//         tabbedPane.setForeground(Color.WHITE);
//         tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

//         // Initialize table panel
//         tablePanel = new JPanel(new BorderLayout());
//         tablePanel.setBackground(BACKGROUND_COLOR);

//         // Define columns
//         String[] columns = {"Bus", "Vendor", "Product", "Functions"};
        
//         // Create the model
//         model = new DefaultTableModel(columns, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };

//         // Create and configure table
//         table = new JTable(model);
//         configureTable();
        
//         // Create search panel
//         createSearchPanel();
        
//         // Add table to its panel
//         JScrollPane scrollPane = new JScrollPane(table);
//         scrollPane.setBackground(BACKGROUND_COLOR);
//         scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
//         tablePanel.add(scrollPane, BorderLayout.CENTER);

//         // Get real PCI data
//         PCIDevArray pciArray = new PCIDevArray();
//         ArrayList<PCIDevice> realDevices = pciArray.getArray();
        
//         // Get total number of buses
//         pciInfo pci = new pciInfo();
//         pci.read();
//         int totalBuses = pci.busCount();

//         // Create network map panel with real data
//         networkMapPanel = new PCINetworkMapPanel(realDevices, totalBuses);
//         JScrollPane mapScrollPane = new JScrollPane(networkMapPanel);
//         mapScrollPane.setBackground(BACKGROUND_COLOR);
//         mapScrollPane.getViewport().setBackground(BACKGROUND_COLOR);

//         // Add panels to tabbed pane
//         tabbedPane.addTab("Table View", tablePanel);
//         tabbedPane.addTab("Network Map", mapScrollPane);

//         // Add tabbed pane to main panel
//         add(tabbedPane, BorderLayout.CENTER);
        
//         // Load PCI Data
//         loadPCIData();
//     }

//     private void createSearchPanel() {
//         JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         searchPanel.setBackground(DARKER_BG);
        
//         JLabel searchLabel = new JLabel("Search:");
//         searchLabel.setForeground(Color.WHITE);
//         searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
//         searchField = new JTextField(20);
//         searchField.setBackground(HEADER_COLOR);
//         searchField.setForeground(Color.WHITE);
//         searchField.setCaretColor(Color.WHITE);
//         searchField.setBorder(BorderFactory.createCompoundBorder(
//             BorderFactory.createLineBorder(BORDER_COLOR),
//             BorderFactory.createEmptyBorder(5, 5, 5, 5)
//         ));
        
//         // Add search functionality
//         searchField.getDocument().addDocumentListener(new DocumentListener() {
//             public void changedUpdate(DocumentEvent e) { filterTable(); }
//             public void removeUpdate(DocumentEvent e) { filterTable(); }
//             public void insertUpdate(DocumentEvent e) { filterTable(); }
//         });
        
//         searchPanel.add(searchLabel);
//         searchPanel.add(searchField);
        
//         // Add search panel to table panel
//         tablePanel.add(searchPanel, BorderLayout.NORTH);
//     }

//     private void createStatsPanel() {
//         statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         statsPanel.setBackground(DARKER_BG);
        
//         // Get PCI information
//         pciInfo pci = new pciInfo();
//         pci.read();
        
//         // Create labels for bus and device counts
//         JLabel busCountLabel = new JLabel(String.format("Total PCI Buses: %d", pci.busCount()));
//         busCountLabel.setForeground(Color.WHITE);
        
//         // Get total devices across all buses
//         int totalDevices = 0;
//         for (int i = 0; i < pci.busCount(); i++) {
//             totalDevices += pci.deviceCount(i);
//         }
//         JLabel deviceCountLabel = new JLabel(String.format("Total Devices: %d", totalDevices));
//         deviceCountLabel.setForeground(Color.WHITE);
        
//         // Add labels to stats panel
//         statsPanel.add(busCountLabel);
//         statsPanel.add(Box.createHorizontalStrut(20));  // spacing
//         statsPanel.add(deviceCountLabel);
//     }

//     private void configureTable() {
//         table.setBackground(DARKER_BG);
//         table.setForeground(Color.WHITE);
//         table.setGridColor(BORDER_COLOR);
//         table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//         table.setRowHeight(30);
//         table.getTableHeader().setReorderingAllowed(false);

//         // Get the column model
//         TableColumnModel columnModel = table.getColumnModel();
        
//         // Fix width for Bus and Functions columns
//         columnModel.getColumn(0).setMinWidth(50);   // Bus
//         columnModel.getColumn(0).setMaxWidth(50);   // Bus
//         columnModel.getColumn(3).setMinWidth(70);   // Functions
//         columnModel.getColumn(3).setMaxWidth(120);  // Functions

//         // Center align Bus and Functions columns
//         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//         centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//         centerRenderer.setBackground(DARKER_BG);
//         centerRenderer.setForeground(Color.WHITE);
//         columnModel.getColumn(0).setCellRenderer(centerRenderer);  // Bus
//         columnModel.getColumn(3).setCellRenderer(centerRenderer);  // Functions

//         // Left align Vendor and Product columns
//         DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
//         leftRenderer.setHorizontalAlignment(JLabel.LEFT);
//         leftRenderer.setBackground(DARKER_BG);
//         leftRenderer.setForeground(Color.WHITE);
//         columnModel.getColumn(1).setCellRenderer(leftRenderer);    // Vendor
//         columnModel.getColumn(2).setCellRenderer(leftRenderer);    // Product

//         // Style the header
//         JTableHeader header = table.getTableHeader();
//         header.setBackground(HEADER_COLOR);
//         header.setForeground(Color.WHITE);
//         header.setFont(new Font("Segoe UI", Font.BOLD, 14));

//         // Set alternating row colors
//         table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//             @Override
//             public Component getTableCellRendererComponent(JTable table, Object value,
//                     boolean isSelected, boolean hasFocus, int row, int column) {
//                 Component c = super.getTableCellRendererComponent(table, value,
//                         isSelected, hasFocus, row, column);
                
//                 if (!isSelected) {
//                     c.setBackground(row % 2 == 0 ? DARKER_BG : new Color(45, 45, 70));
//                     c.setForeground(Color.WHITE);
//                 }
                
//                 // Center alignment for Bus and Functions columns
//                 if (column == 0 || column == 3) {
//                     ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
//                 } else {
//                     ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
//                 }
                
//                 return c;
//             }
//         });
//     }

//     private void filterTable() {
//         String searchText = searchField.getText().toLowerCase();
//         TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
//         table.setRowSorter(sorter);
        
//         if (searchText.length() == 0) {
//             sorter.setRowFilter(null);
//         } else {
//             sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
//         }
//     }

//     private void loadPCIData() {
//         try {
//             PCIDevArray pciArray = new PCIDevArray();
//             ArrayList<PCIDevice> devices = pciArray.getArray();

//             // Clear existing rows
//             model.setRowCount(0);

//             // Keep track of devices to combine
//             String currentVendor = "";
//             String currentProduct = "";
//             int currentBus = -1;
//             int functionCount = 0;

//             for (PCIDevice device : devices) {
//                 // If this is a continuation of the same device
//                 if (currentBus == device.getBus() && 
//                     currentVendor.equals(device.getVendor()) &&
//                     currentProduct.equals(device.getProduct().split("Function")[0])) {
//                     // Update function count
//                     functionCount++;
//                 } else {
//                     // If we had a previous device, add it to the table
//                     if (currentBus != -1 && functionCount > 0) {
//                         model.addRow(new Object[]{
//                             currentBus,
//                             currentVendor,
//                             currentProduct,
//                             functionCount
//                         });
//                     }
//                     // Start tracking new device
//                     currentBus = device.getBus();
//                     currentVendor = device.getVendor();
//                     currentProduct = device.getProduct().split("Function")[0];
//                     functionCount = 1;
//                 }
//             }
            
//             // Add the last device
//             if (currentBus != -1 && functionCount > 0) {
//                 model.addRow(new Object[]{
//                     currentBus,
//                     currentVendor,
//                     currentProduct,
//                     functionCount
//                 });
//             }

//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(this,
//                 "Error loading PCI data: " + e.getMessage(),
//                 "Error",
//                 JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     public void refreshData() {
//         loadPCIData();
        
//         // Refresh network map with new data
//         PCIDevArray pciArray = new PCIDevArray();
//         ArrayList<PCIDevice> realDevices = pciArray.getArray();
//         networkMapPanel.updateDevices(realDevices);
//     }
// }
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class PCITablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JPanel statsPanel;
    private PCINetworkMapPanel networkMapPanel;
    private JPanel tablePanel;
    private JTextField searchField;
    private JComboBox<String> busFilter;
    private JComboBox<String> deviceTypeFilter;
    private final Color BACKGROUND_COLOR = new Color(47, 47, 68);
    private final Color DARKER_BG = new Color(41, 41, 66);
    private final Color BORDER_COLOR = new Color(75, 75, 96);
    private final Color HEADER_COLOR = new Color(52, 50, 74);

    public PCITablePanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Initialize statsPanel and create it
        statsPanel = new JPanel();
        createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(DARKER_BG);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Initialize table panel
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);

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

        // Create search panel
        createSearchPanel();

        // Add table to its panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Get real PCI data
        PCIDevArray pciArray = new PCIDevArray();
        ArrayList<PCIDevice> realDevices = pciArray.getArray();

        // Get total number of buses
        pciInfo pci = new pciInfo();
        pci.read();
        int totalBuses = pci.busCount();

        // Create network map panel with real data
        networkMapPanel = new PCINetworkMapPanel(realDevices, totalBuses);
        JScrollPane mapScrollPane = new JScrollPane(networkMapPanel);
        mapScrollPane.setBackground(BACKGROUND_COLOR);
        mapScrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Add panels to tabbed pane
        tabbedPane.addTab("Table View", tablePanel);
        tabbedPane.addTab("Network Map", mapScrollPane);

        // Add tabbed pane to main panel
        add(tabbedPane, BorderLayout.CENTER);

        // Load PCI Data
        loadPCIData();
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBackground(DARKER_BG);

        // Search field
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField(15);
        searchField.setBackground(HEADER_COLOR);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Bus filter
        JLabel busLabel = new JLabel("Bus:");
        busLabel.setForeground(Color.WHITE);
        busLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        busFilter = new JComboBox<>();
        busFilter.setBackground(HEADER_COLOR);
        busFilter.setForeground(Color.WHITE);
        busFilter.addItem("All Buses");

        // Device type filter
        JLabel typeLabel = new JLabel("Device Type:");
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        deviceTypeFilter = new JComboBox<>();
        deviceTypeFilter.setBackground(HEADER_COLOR);
        deviceTypeFilter.setForeground(Color.WHITE);
        deviceTypeFilter.addItem("All Devices");

        // Style the comboboxes
        styleComboBox(busFilter);
        styleComboBox(deviceTypeFilter);

        // Add search functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void insertUpdate(DocumentEvent e) { filterTable(); }
        });

        // Add filter functionality
        busFilter.addActionListener(e -> filterTable());
        deviceTypeFilter.addActionListener(e -> filterTable());

        // Add components to panel
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(busLabel);
        searchPanel.add(busFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(typeLabel);
        searchPanel.add(deviceTypeFilter);

        // Add search panel to table panel
        tablePanel.add(searchPanel, BorderLayout.NORTH);
    }
    private void createStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(DARKER_BG);
        
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




    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? BORDER_COLOR : HEADER_COLOR);
                setForeground(Color.WHITE);
                return this;
            }
        });

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JComponent) comboBox.getEditor().getEditorComponent()).setBorder(
                BorderFactory.createEmptyBorder(2, 5, 2, 5));
    }

    private void updateFilters() {
        // Save current selections
        String selectedBus = busFilter.getSelectedItem() != null ? (String) busFilter.getSelectedItem() : "All Buses";
        String selectedType = deviceTypeFilter.getSelectedItem() != null ? (String) deviceTypeFilter.getSelectedItem() : "All Devices";
        
        // Clear existing items
        busFilter.removeAllItems();
        deviceTypeFilter.removeAllItems();
        
        // Add default "All" options
        busFilter.addItem("All Buses");
        deviceTypeFilter.addItem("All Devices");
        
        // Get unique bus numbers and device types
        Set<Integer> buses = new TreeSet<>();
        Set<String> deviceTypes = new TreeSet<>();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String busValue = model.getValueAt(i, 0).toString();
            try {
                buses.add(Integer.parseInt(busValue)); // Store as integers for proper sorting
            } catch (NumberFormatException e) {
                System.out.println("Error parsing bus number: " + busValue);
            }
    
            String product = model.getValueAt(i, 2).toString();
            String deviceType = extractDeviceType(product);
            if (deviceType != null) {
                deviceTypes.add(deviceType);
            }
        }
        
        // Add sorted bus numbers back as strings
        for (Integer bus : buses) {
            busFilter.addItem(String.valueOf(bus));
        }
        deviceTypes.forEach(type -> deviceTypeFilter.addItem(type));
        
        // Restore selections if they still exist
        if (selectedBus != null && comboBoxContainsItem(busFilter, selectedBus)) {
            busFilter.setSelectedItem(selectedBus);
        }
        if (selectedType != null && comboBoxContainsItem(deviceTypeFilter, selectedType)) {
            deviceTypeFilter.setSelectedItem(selectedType);
        }
    }
    
    private boolean comboBoxContainsItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }
    
    

    private String extractDeviceType(String product) {
        if (product.toLowerCase().contains("controller")) return "Controller";
        if (product.toLowerCase().contains("bridge")) return "Bridge";
        if (product.toLowerCase().contains("adapter")) return "Adapter";
        return "Other";
    }

    private void configureTable() {
        table.setBackground(DARKER_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(50);
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(3).setMinWidth(70);
        columnModel.getColumn(3).setMaxWidth(120);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(DARKER_BG);
        centerRenderer.setForeground(Color.WHITE);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBackground(DARKER_BG);
        leftRenderer.setForeground(Color.WHITE);
        columnModel.getColumn(1).setCellRenderer(leftRenderer);
        columnModel.getColumn(2).setCellRenderer(leftRenderer);

        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? DARKER_BG : new Color(45, 45, 70));
                    c.setForeground(Color.WHITE);
                }
                ((JLabel) c).setHorizontalAlignment(column == 0 || column == 3 ? JLabel.CENTER : JLabel.LEFT);
                return c;
            }
        });
    }

    private void filterTable() {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String searchText = searchField.getText().toLowerCase();
                String selectedBus = busFilter.getSelectedItem() != null ? (String) busFilter.getSelectedItem() : "All Buses";
                String selectedType = deviceTypeFilter.getSelectedItem() != null ? (String) deviceTypeFilter.getSelectedItem() : "All Devices";
    
                // Debugging logs to track values
                System.out.println("Filtering entry:");
                System.out.println("Search text: " + searchText);
                System.out.println("Selected bus: " + selectedBus);
                System.out.println("Selected type: " + selectedType);
    
                // Search text filter
                boolean matchesSearch = searchText.isEmpty();
                for (int i = 0; i < entry.getValueCount(); i++) {
                    String value = entry.getStringValue(i);
                    System.out.println("Checking value for search: " + value);
                    if (value != null && value.toLowerCase().contains(searchText)) {
                        matchesSearch = true;
                        break;
                    }
                }
    
                // Bus filter
                String busValue = entry.getStringValue(0);
                boolean matchesBus = "All Buses".equals(selectedBus) || 
                                     (busValue != null && busValue.equals(selectedBus));
                System.out.println("Bus value: " + busValue + ", Matches bus: " + matchesBus);
    
                // Device type filter
                boolean matchesType = "All Devices".equals(selectedType);
                if (!matchesType) {
                    String productName = entry.getStringValue(2);
                    System.out.println("Product name: " + productName);
                    if (productName != null) {
                        String deviceType = extractDeviceType(productName);
                        matchesType = selectedType.equals(deviceType);
                        System.out.println("Extracted device type: " + deviceType + ", Matches type: " + matchesType);
                    }
                }
    
                System.out.println("Matches search: " + matchesSearch + ", Matches bus: " + matchesBus + ", Matches type: " + matchesType);
                return matchesSearch && matchesBus && matchesType;
            }
        };
    
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(filter);
        table.setRowSorter(sorter);
    }
    
    
    

    


    private void loadPCIData() {
        try {
            PCIDevArray pciArray = new PCIDevArray();
            ArrayList<PCIDevice> devices = pciArray.getArray();
            model.setRowCount(0);
    
            String currentVendor = "";
            String currentProduct = "";
            int currentBus = -1;
            int functionCount = 0;
    
            for (PCIDevice device : devices) {
                String vendor = device.getVendor() != null ? device.getVendor() : "";
                String product = device.getProduct() != null ? device.getProduct() : "";
                String productBase = product.split("Function")[0];
    
                // Debugging output
                System.out.println("Current Vendor: " + currentVendor);
                System.out.println("Current Product: " + currentProduct);
                System.out.println("New Vendor: " + vendor);
                System.out.println("New Product Base: " + productBase);
                System.out.println("Current Bus: " + currentBus);
                System.out.println("Device Bus: " + device.getBus());
    
                if (currentBus == device.getBus() &&
                    vendor.equals(currentVendor) &&
                    productBase.equals(currentProduct)) {
                    functionCount++;
                } else {
                    if (currentBus != -1 && functionCount > 0) {
                        model.addRow(new Object[]{currentBus, currentVendor, currentProduct, functionCount});
                    }
                    currentBus = device.getBus();
                    currentVendor = vendor;
                    currentProduct = productBase;
                    functionCount = 1;
                }
            }
            if (currentBus != -1 && functionCount > 0) {
                model.addRow(new Object[]{currentBus, currentVendor, currentProduct, functionCount});
            }
            updateFilters();
        } catch (Exception e) {
            e.printStackTrace();  // Print the full stack trace for debugging
            JOptionPane.showMessageDialog(this, "Error loading PCI data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshData() {
        loadPCIData();
        PCIDevArray pciArray = new PCIDevArray();
        ArrayList<PCIDevice> realDevices = pciArray.getArray();
        networkMapPanel.updateDevices(realDevices);
    }
}
