
  
  
  
  
  
  
  
  
  

  
  
  
  
  
  
  
  
  
  
  

  
  
  

  
  
  
  

  
  
  
  
  

  
  
  

  
  
        
  
  
  
  
  
  
  

  
  
  
        
  
  
        
  
  
  
  
  

  
  
  
        
  
  
  
  

  
  
  
  
  

  
  
  

  
  
        
  
  
  

  
  
  
        
  
  
  
        
  
  
  
  
  
  
  
  
        
  
  
  
  
  
  
        
  
  
        
  
  
  

  
  
  
        
  
  
  
        
  
  
  
        
  
  
  
  
  
  
  
        
  
  
  
  
  

  
  
  
  
  
  
  

  
  
        
  
  
  
  
  

  
  
  
  
  
  
  

  
  
  
  
  
  
  

  
  
  
  
  

  
  
  
  
  
  
  
                
  
  
  
  
                
  
  
  
  
  
  
                
  
  
  
  

  
  
  
  
        
  
  
  
  
  
  

  
  
  
  

  
  

  
  
  
  
  

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
            
  
  
  
  
  
  
  
  
  

  
  
  
  
  
  
  

  
  
        
  
  
  
  
  
  
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

          
        statsPanel = new JPanel();
        createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

          
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(DARKER_BG);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

          
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);

          
        String[] columns = {"Bus", "Vendor", "Product", "Functions"};

          
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

          
        table = new JTable(model);
        configureTable();

          
        createSearchPanel();

          
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

          
        PCIDevArray pciArray = new PCIDevArray();
        ArrayList<PCIDevice> realDevices = pciArray.getArray();

          
        pciInfo pci = new pciInfo();
        pci.read();
        int totalBuses = pci.busCount();

          
        networkMapPanel = new PCINetworkMapPanel(realDevices, totalBuses);
        JScrollPane mapScrollPane = new JScrollPane(networkMapPanel);
        mapScrollPane.setBackground(BACKGROUND_COLOR);
        mapScrollPane.getViewport().setBackground(BACKGROUND_COLOR);

          
        tabbedPane.addTab("Table View", tablePanel);
        tabbedPane.addTab("Network Map", mapScrollPane);

          
        add(tabbedPane, BorderLayout.CENTER);

          
        loadPCIData();
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBackground(DARKER_BG);

          
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

          
        JLabel busLabel = new JLabel("Bus:");
        busLabel.setForeground(Color.WHITE);
        busLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        busFilter = new JComboBox<>();
        busFilter.setBackground(HEADER_COLOR);
        busFilter.setForeground(Color.WHITE);
        busFilter.addItem("All Buses");

          
        JLabel typeLabel = new JLabel("Device Type:");
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        deviceTypeFilter = new JComboBox<>();
        deviceTypeFilter.setBackground(HEADER_COLOR);
        deviceTypeFilter.setForeground(Color.WHITE);
        deviceTypeFilter.addItem("All Devices");

          
        styleComboBox(busFilter);
        styleComboBox(deviceTypeFilter);

          
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void insertUpdate(DocumentEvent e) { filterTable(); }
        });

          
        busFilter.addActionListener(e -> filterTable());
        deviceTypeFilter.addActionListener(e -> filterTable());

          
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(busLabel);
        searchPanel.add(busFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(typeLabel);
        searchPanel.add(deviceTypeFilter);

          
        tablePanel.add(searchPanel, BorderLayout.NORTH);
    }
    private void createStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(DARKER_BG);
        
          
        pciInfo pci = new pciInfo();
        pci.read();
        
          
        JLabel busCountLabel = new JLabel(String.format("Total PCI Buses: %d", pci.busCount()));
        busCountLabel.setForeground(Color.WHITE);
        
          
        int totalDevices = 0;
        for (int i = 0; i < pci.busCount(); i++) {
            totalDevices += pci.deviceCount(i);
        }
        JLabel deviceCountLabel = new JLabel(String.format("Total Devices: %d", totalDevices));
        deviceCountLabel.setForeground(Color.WHITE);
        
          
        statsPanel.add(busCountLabel);
        statsPanel.add(Box.createHorizontalStrut(20));    
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
          
        String selectedBus = busFilter.getSelectedItem() != null ? (String) busFilter.getSelectedItem() : "All Buses";
        String selectedType = deviceTypeFilter.getSelectedItem() != null ? (String) deviceTypeFilter.getSelectedItem() : "All Devices";
        
          
        busFilter.removeAllItems();
        deviceTypeFilter.removeAllItems();
        
          
        busFilter.addItem("All Buses");
        deviceTypeFilter.addItem("All Devices");
        
          
        Set<Integer> buses = new TreeSet<>();
        Set<String> deviceTypes = new TreeSet<>();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String busValue = model.getValueAt(i, 0).toString();
            try {
                buses.add(Integer.parseInt(busValue));   
            } catch (NumberFormatException e) {
                System.out.println("Error parsing bus number: " + busValue);
            }
    
            String product = model.getValueAt(i, 2).toString();
            String deviceType = extractDeviceType(product);
            if (deviceType != null) {
                deviceTypes.add(deviceType);
            }
        }
        
          
        for (Integer bus : buses) {
            busFilter.addItem(String.valueOf(bus));
        }
        deviceTypes.forEach(type -> deviceTypeFilter.addItem(type));
        
          
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
    
                  
                System.out.println("Filtering entry:");
                System.out.println("Search text: " + searchText);
                System.out.println("Selected bus: " + selectedBus);
                System.out.println("Selected type: " + selectedType);
    
                  
                boolean matchesSearch = searchText.isEmpty();
                for (int i = 0; i < entry.getValueCount(); i++) {
                    String value = entry.getStringValue(i);
                    System.out.println("Checking value for search: " + value);
                    if (value != null && value.toLowerCase().contains(searchText)) {
                        matchesSearch = true;
                        break;
                    }
                }
    
                  
                String busValue = entry.getStringValue(0);
                boolean matchesBus = "All Buses".equals(selectedBus) || 
                                     (busValue != null && busValue.equals(selectedBus));
                System.out.println("Bus value: " + busValue + ", Matches bus: " + matchesBus);
    
                  
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
            e.printStackTrace();    
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
