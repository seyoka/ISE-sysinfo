import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class USBInfoTree extends JPanel {
    private usbInfo usb;
    private JTree tree;
    private DefaultMutableTreeNode root;

    public USBInfoTree(usbInfo usb) {
        this.usb = usb;
        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 52));

        // Create refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(75, 75, 96));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(35, 35, 52));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Create tree
        root = new DefaultMutableTreeNode("USB Buses");
        tree = new JTree(root);
        tree.setBackground(new Color(35, 35, 52));
        tree.setForeground(Color.WHITE);
        
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setBackground(new Color(35, 35, 52));
        treeView.getViewport().setBackground(new Color(35, 35, 52));
        add(treeView, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> {
            refreshTree();
        });

        refreshTree(); // Initial load
    }

    private void refreshTree() {
        root.removeAllChildren();
        usb.read();

        int busCount = usb.busCount();
        for (int bus = 1; bus <= busCount; bus++) {
            DefaultMutableTreeNode busNode = new DefaultMutableTreeNode("Bus " + bus);
            int deviceCount = usb.deviceCount(bus);
            for (int device = 1; device <= deviceCount; device++) {
                int vendorId = usb.vendorID(bus, device);
                int productId = usb.productID(bus, device);
                String vendorIdHex = String.format("%04x", vendorId);
                String productIdHex = String.format("%04x", productId);
                String vendorName = USBDeviceMap.getVendorName(vendorIdHex);
                String productName = USBDeviceMap.getProductName(vendorIdHex, productIdHex);
                
                busNode.add(new DefaultMutableTreeNode(
                    String.format("Device %d - %s %s", device, vendorName, productName)
                ));
            }
            if (deviceCount > 0) {
                root.add(busNode);
            }
        }
        ((DefaultTreeModel)tree.getModel()).reload();
    }
}