import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.table.*;

public class USBInfoPanel extends JPanel {
    private final Color DARKER_BG = new Color(35, 35, 52);
    private final Color HEADER_COLOR = new Color(47, 47, 68);
    private final Color BORDER_COLOR = new Color(75, 75, 96);
    private final Color ACCENT_COLOR = new Color(77, 208, 255);
    
    private JPanel tablePanel;
    private JPanel chartPanel;
    private JPanel statsPanel;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> busFilter;
    private java.util.List<BusDevice> devices;
    private usbInfo usb;

    private Timer updateTimer;


    public USBInfoPanel() {
        try {
            // Basic panel setup
            setLayout(new BorderLayout());
            setBackground(DARKER_BG);
            
            // Initialize collections and USB info
            devices = new ArrayList<>();
            usb = new usbInfo();
            
            // Create UI components
            initializeComponents();
            
            // Initial data load
            loadUSBData();
            
            // Setup live updates
            updateTimer = new Timer(1000, e -> {
                try {
                    loadUSBData();
                    repaint();
                } catch (Exception ex) {
                    System.err.println("Error updating USB data: " + ex.getMessage());
                }
            });
            updateTimer.start();
            
        } catch (Exception e) {
            System.err.println("Error initializing USB panel: " + e.getMessage());
            add(new JLabel("Error initializing USB information"), BorderLayout.CENTER);
        }
    }

    private void initializeComponents() {
        // Create main panels
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(DARKER_BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPieChart((Graphics2D) g);
            }
        };
        chartPanel.setBackground(DARKER_BG);
        chartPanel.setPreferredSize(new Dimension(300, 0));

        // Create table
        String[] columns = {"Bus", "Device", "Vendor ID", "Product ID"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        configureTable();

        // Create search panel
        createSearchPanel();
        
        // Create stats panel
        createStatsPanel();

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tablePanel, chartPanel);
        splitPane.setDividerLocation(0.7);
        splitPane.setResizeWeight(0.7);
        splitPane.setBackground(DARKER_BG);
        splitPane.setBorder(null);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(statsPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void loadUSBData() {
        try {
            model.setRowCount(0);
            devices.clear();
            
            usb.read();
            
            for (int busIndex = 1; busIndex <= usb.busCount(); busIndex++) {
                int deviceCount = usb.deviceCount(busIndex);
                for (int deviceIndex = 1; deviceIndex <= deviceCount; deviceIndex++) {
                    int vendorID = usb.vendorID(busIndex, deviceIndex);
                    int productID = usb.productID(busIndex, deviceIndex);
                    
                    String vendorIdHex = String.format("%04x", vendorID);
                    String productIdHex = String.format("%04x", productID);
                    
                    String vendorName = USBDeviceMap.getVendorName(vendorIdHex);
                    String productName = USBDeviceMap.getProductName(vendorIdHex, productIdHex);
                    
                    // Only display the names, no hex codes
                    model.addRow(new Object[]{
                        busIndex,
                        deviceIndex,
                        vendorName,
                        productName
                    });
                    
                    devices.add(new BusDevice(busIndex, deviceIndex, vendorID, productID));
                }
            }
            
            updateFilters();
            repaint();
            
        } catch (Exception e) {
            System.err.println("Error loading USB data: " + e.getMessage());
            e.printStackTrace();
        }
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
        styleComboBox(busFilter);

        // Add listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        busFilter.addActionListener(e -> filterTable());

        // Add components
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(busLabel);
        searchPanel.add(busFilter);

        tablePanel.add(searchPanel, BorderLayout.NORTH);
    }

    private void createStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(DARKER_BG);
        
        try {
            // Get unique buses
            Set<Integer> uniqueBuses = new HashSet<>();
            int totalDevices = 0;
            
            usb.read();  // Read USB info
            
            // Count buses that actually have devices
            for (int busIndex = 1; busIndex <= usb.busCount(); busIndex++) {
                int deviceCount = usb.deviceCount(busIndex);
                if (deviceCount > 0) {
                    uniqueBuses.add(busIndex);
                    totalDevices += deviceCount;
                }
            }
            
            // Create labels with actual counts
            JLabel busCountLabel = new JLabel(String.format("Total USB Buses: %d", uniqueBuses.size()));
            busCountLabel.setForeground(Color.WHITE);
            busCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel deviceCountLabel = new JLabel(String.format("Total Devices: %d", totalDevices));
            deviceCountLabel.setForeground(Color.WHITE);
            deviceCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // Add to panel
            statsPanel.add(busCountLabel);
            statsPanel.add(Box.createHorizontalStrut(20));
            statsPanel.add(deviceCountLabel);
            
        } catch (Exception e) {
            System.err.println("Error calculating USB stats: " + e.getMessage());
            JLabel errorLabel = new JLabel("Error reading USB information");
            errorLabel.setForeground(Color.RED);
            statsPanel.add(errorLabel);
        }
    }

    private void drawPieChart(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate devices per bus
        Map<Integer, Integer> devicesPerBus = new HashMap<>();
        for (BusDevice device : devices) {
            devicesPerBus.merge(device.getBus(), 1, Integer::sum);
        }

        // Draw pie chart
        int diameter = Math.min(chartPanel.getWidth(), chartPanel.getHeight()) - 60;
        int x = (chartPanel.getWidth() - diameter) / 2;
        int y = (chartPanel.getHeight() - diameter) / 2;

        double total = devices.size();
        double currentAngle = 0;

        // Draw title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String title = "Devices per Bus";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, 
            (chartPanel.getWidth() - fm.stringWidth(title)) / 2, 
            20);

        // Draw pie slices
        int colorIndex = 0;
        Color[] colors = {
            new Color(77, 208, 255),
            new Color(255, 77, 77),
            new Color(77, 255, 77),
            new Color(255, 208, 77),
            new Color(208, 77, 255)
        };

        for (Map.Entry<Integer, Integer> entry : devicesPerBus.entrySet()) {
            double sliceAngle = 360.0 * entry.getValue() / total;
            
            g2.setColor(colors[colorIndex % colors.length]);
            g2.fill(new Arc2D.Double(x, y, diameter, diameter, 
                currentAngle, sliceAngle, Arc2D.PIE));

            // Draw legend
            int legendY = y + diameter + 20 + (colorIndex * 20);
            g2.fillRect(x, legendY, 15, 15);
            g2.setColor(Color.WHITE);
            g2.drawString(String.format("Bus %d (%d devices)", 
                entry.getKey(), entry.getValue()),
                x + 20, legendY + 12);

            currentAngle += sliceAngle;
            colorIndex++;
        }
    }

    private void configureTable() {
        table.setBackground(DARKER_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);

        // Column sizing
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(50);
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setMinWidth(70);
        columnModel.getColumn(1).setMaxWidth(70);

        // Cell renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(DARKER_BG);
        centerRenderer.setForeground(Color.WHITE);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Row styling
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus, 
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? DARKER_BG : new Color(45, 45, 70));
                    c.setForeground(Color.WHITE);
                }
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                        int index, boolean isSelected, 
                                                        boolean cellHasFocus) {
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
        String selectedBus = busFilter.getSelectedItem() != null ? 
            (String) busFilter.getSelectedItem() : "All Buses";
        
        busFilter.removeAllItems();
        busFilter.addItem("All Buses");
        
        Set<Integer> buses = new TreeSet<>();
        for (BusDevice device : devices) {
            buses.add(device.getBus());
        }
        
        for (Integer bus : buses) {
            busFilter.addItem(String.valueOf(bus));
        }
        
        if (selectedBus != null && comboBoxContainsItem(busFilter, selectedBus)) {
            busFilter.setSelectedItem(selectedBus);
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

    private void filterTable() {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String searchText = searchField.getText().toLowerCase();
                String selectedBus = busFilter.getSelectedItem() != null ? 
                    (String) busFilter.getSelectedItem() : "All Buses";

                // Search text filter
                boolean matchesSearch = searchText.isEmpty();
                if (!matchesSearch) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        String value = entry.getStringValue(i).toLowerCase();
                        if (value.contains(searchText)) {
                            matchesSearch = true;
                            break;
                        }
                    }
                }

                // Bus filter
                boolean matchesBus = "All Buses".equals(selectedBus) ||
                    entry.getStringValue(0).equals(selectedBus);

                return matchesSearch && matchesBus;
            }
        };

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(filter);
        table.setRowSorter(sorter);
    }
    public void cleanup() {
        if (updateTimer != null) {
            updateTimer.stop();
            updateTimer = null;
        }
        if (devices != null) {
            devices.clear();
        }
        if (model != null) {
            model.setRowCount(0);
        }
        usb = null;
    }
}


