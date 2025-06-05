package OrderView;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class OrderView extends JDialog {
    private static final int MAX_ORDER_QUANTITY = 100;
    private static final int MIN_ORDER_QUANTITY = 1;
    
    private JSpinner quantitySpinner;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel productNameLabel;
    private JLabel unitPriceLabel;
    private JLabel totalPriceLabel;
    
    private int quantity = 0;
    private boolean confirmed = false;
    private String productName;
    private double unitPrice;

    public OrderView(JFrame parent, String productName, double unitPrice) {
        super(parent, "Chọn số lượng - " + productName, true);
        this.productName = productName;
        this.unitPrice = unitPrice;
        
        initComponents();
        layoutComponents();
        setupEventHandlers();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        pack();
    }
    
    private void initComponents() {
        // Product info labels
        productNameLabel = new JLabel("Sản phẩm: " + productName);
        unitPriceLabel = new JLabel(String.format("Đơn giá: %.0f VNĐ", unitPrice));
        totalPriceLabel = new JLabel("Thành tiền: 0 VNĐ");
        
        // Quantity spinner with validation
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
            MIN_ORDER_QUANTITY, MIN_ORDER_QUANTITY, MAX_ORDER_QUANTITY, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        
        // Buttons
        confirmButton = new JButton("Mua");
        cancelButton = new JButton("Hủy");
    }
    
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Product name
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(productNameLabel, gbc);
        
        // Unit price
        gbc.gridy = 1;
        add(unitPriceLabel, gbc);
        
        // Quantity label and spinner
        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Số lượng:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(quantitySpinner, gbc);
        
        // Total price
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(totalPriceLabel, gbc);
        
        // Buttons
        gbc.gridy = 4; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(confirmButton, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(cancelButton, gbc);
    }
    
    private void setupEventHandlers() {
        // Update total price when quantity changes
        quantitySpinner.addChangeListener(e -> updateTotalPrice());
        
        confirmButton.addActionListener(e -> {
            try {
                quantity = (Integer) quantitySpinner.getValue();
                if (quantity < MIN_ORDER_QUANTITY || quantity > MAX_ORDER_QUANTITY) {
                    throw new IllegalArgumentException("Số lượng không hợp lệ");
                }
                confirmed = true;
                setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Số lượng phải từ " + MIN_ORDER_QUANTITY + " đến " + MAX_ORDER_QUANTITY,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });
        
        // Initial total price calculation
        updateTotalPrice();
    }
    
    private void updateTotalPrice() {
        int currentQuantity = (Integer) quantitySpinner.getValue();
        double totalPrice = currentQuantity * unitPrice;
        totalPriceLabel.setText(String.format("Thành tiền: %.0f VNĐ", totalPrice));
    }

    public int getQuantity() {
        return quantity;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
    
    // Static method để hiển thị dialog và trả về kết quả
    public static OrderResult showOrderDialog(JFrame parent, String productName, double unitPrice) {
        OrderView dialog = new OrderView(parent, productName, unitPrice);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            return new OrderResult(dialog.getQuantity(), dialog.getTotalPrice());
        }
        return null;
    }
    
    // Inner class để trả về kết quả
    public static class OrderResult {
        private final int quantity;
        private final double totalPrice;
        
        public OrderResult(int quantity, double totalPrice) {
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }
        
        public int getQuantity() { return quantity; }
        public double getTotalPrice() { return totalPrice; }
    }
}