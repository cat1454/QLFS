package OrderView;

import database.DataBaseConfig;
import model.CartItem;
import service.CartService;
import utils.AuthUtils;
import utils.OrderUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class CartView extends JPanel {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JButton deleteButton, orderButton;
    private List<CartItem> cartItems;

    public CartView() {
        this.cartItems = CartService.getInstance().getCartItems();
        initUI();
    }

   // Thay thế phần initUI() trong CartView bằng code này:

private void initUI() {
    setLayout(new BorderLayout());

    // Thêm cột Product ID để tránh lỗi mapping
    String[] columnNames = {"Chọn", "Product ID", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
    tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) return Boolean.class;
            if (column == 1) return Integer.class;
            if (column == 3 || column == 4 || column == 5) return Double.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0 || column == 3; // Cho phép chọn checkbox và sửa số lượng
        }
        
        // FIX: Override setValueAt để xử lý khi user thay đổi giá trị
        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 3) { // Cột số lượng
                try {
                    int newQuantity = 0;
                    if (value instanceof Integer) {
                        newQuantity = (Integer) value;
                    } else {
                        newQuantity = Integer.parseInt(value.toString());
                    }
                    
                    // Validate số lượng
                    if (newQuantity <= 0) {
                        JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return; // Không cập nhật nếu số lượng không hợp lệ
                    }
                    
                    // Cập nhật giá trị
                    super.setValueAt(newQuantity, row, column);
                    
                    // Tính lại thành tiền cho dòng này
                    Object priceObj = getValueAt(row, 4); // Đơn giá
                    if (priceObj != null) {
                        double price = priceObj instanceof Double ? (Double) priceObj : Double.parseDouble(priceObj.toString());
                        double itemTotal = newQuantity * price;
                        super.setValueAt(itemTotal, row, 5); // Cập nhật thành tiền
                    }
                    
                    // Cập nhật trong CartService
                    Integer productId = (Integer) getValueAt(row, 1);
                    if (productId != null) {
                        CartService.getInstance().updateQuantity(productId, newQuantity);
                    }
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                super.setValueAt(value, row, column);
            }
            
            // Cập nhật tổng tiền
            SwingUtilities.invokeLater(() -> updateTotal());
        }
    };

    cartTable = new JTable(tableModel);
    
    // Ẩn cột Product ID
    cartTable.getColumnModel().getColumn(1).setMinWidth(0);
    cartTable.getColumnModel().getColumn(1).setMaxWidth(0);
    cartTable.getColumnModel().getColumn(1).setWidth(0);
    
    // FIX: Đặt editor cho cột số lượng để dễ edit hơn
    cartTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()) {
        @Override
        public boolean stopCellEditing() {
            try {
                String value = (String) getCellEditorValue();
                int quantity = Integer.parseInt(value);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(cartTable, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(cartTable, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return super.stopCellEditing();
        }
    });
    
    // FIX: Thêm listener để bắt sự kiện thay đổi
    tableModel.addTableModelListener(e -> {
        if (e.getColumn() == 0) { // Chỉ update tổng khi thay đổi checkbox
            SwingUtilities.invokeLater(() -> updateTotal());
        }
        // Không cần listener cho cột 3 vì đã xử lý trong setValueAt
    });

    JScrollPane scrollPane = new JScrollPane(cartTable);
    add(scrollPane, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    totalLabel = new JLabel("Tổng: 0 VNĐ");
    deleteButton = new JButton("Xóa mục đã chọn");
    orderButton = new JButton("Đặt hàng");

    deleteButton.addActionListener(e -> deleteSelectedItem());
    orderButton.addActionListener(e -> {
    // Kiểm tra có sản phẩm được chọn không
    boolean hasSelection = false;
    double totalAmount = 0;
    
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
        if (selected != null && selected) {
            hasSelection = true;
            Object totalObj = tableModel.getValueAt(i, 5);
            if (totalObj != null) {
                totalAmount += totalObj instanceof Double ? (Double) totalObj : Double.parseDouble(totalObj.toString());
            }
        }
    }

    if (!hasSelection) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để đặt hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (totalAmount <= 0) {
        JOptionPane.showMessageDialog(this, "Tổng tiền phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Lưu tổng tiền để sử dụng trong callback
    final double finalTotalAmount = totalAmount;
    
    // Hỏi có muốn tham gia vòng quay may mắn không
    int choice = JOptionPane.showOptionDialog(
        this,
        "<html><center>" +
        "<h2>🎯 VÒNG QUAY MAY MẮN</h2>" +
        "<p>Bạn có muốn tham gia vòng quay may mắn để nhận mã giảm giá không?</p>" +
        "<p><b>Tổng tiền hiện tại: " + String.format("%.0f", totalAmount) + " VNĐ</b></p>" +
        "<br><b>Quy tắc:</b>" +
        "<li>• Có tối đa 3 lượt quay miễn phí</li>" +
        "<li>• Cơ hội nhận giảm giá từ 5% đến 50%</li>" +
        "<li>• Có thể dừng bất kỳ lúc nào để sử dụng mã đã có</li>" +
        "<li>• Nếu không tham gia sẽ đặt hàng với giá gốc</li>" +
        "</center></html>",
        "Vòng Quay May Mắn",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        new String[]{"🎲 THAM GIA", "❌ BỎ QUA"},
        "🎲 THAM GIA"
    );
    
    if (choice == 0) { // Chọn tham gia
        // Tạo LuckyWheel với callback
        LuckyWheel wheel = new LuckyWheel(new SpinResultCallback() {
            @Override
            public void onSpinComplete(String prizeLabel) {
                System.out.println("Lần quay vừa rồi: " + prizeLabel);
            }
            
            @Override
            public void onAllSpinsCompleted(String finalPrizeLabel) {
                float discount = 0f;
                if (finalPrizeLabel != null && finalPrizeLabel.endsWith("%")) {
                    System.out.println("Nhận được giảm giá cuối cùng: " + finalPrizeLabel);
                    String discountStr = finalPrizeLabel.replace("%", "");
                    discount = Float.parseFloat(discountStr) / 100f;
                }
                
                // Xác nhận đặt hàng với giảm giá
                double discountAmount = finalTotalAmount * discount;
                double finalAmount = finalTotalAmount - discountAmount;
                
                String confirmMessage = discount > 0 ? 
                    String.format("Xác nhận đặt hàng:\n" +
                                "Tổng tiền gốc: %.0f VNĐ\n" +
                                "Giảm giá (%s): -%.0f VNĐ\n" +
                                "Thành tiền: %.0f VNĐ", 
                                finalTotalAmount, finalPrizeLabel, discountAmount, finalAmount) :
                    String.format("Xác nhận đặt hàng với tổng tiền: %.0f VNĐ", finalTotalAmount);
                
                int confirm = JOptionPane.showConfirmDialog(
                    CartView.this, 
                    confirmMessage, 
                    "Xác nhận đặt hàng", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Gọi phương thức đặt hàng với giảm giá
                    placeSelectedOrdersWithDiscount(discount, finalPrizeLabel != null ? finalPrizeLabel : "Không có giảm giá");
                }
            }
        });
        
        wheel.setVisible(true);
        
    } else { // Chọn bỏ qua - đặt hàng ngay
        // Xác nhận đặt hàng bình thường
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận đặt hàng với tổng tiền: " + String.format("%.0f", totalAmount) + " VNĐ?", 
            "Xác nhận đặt hàng", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            placeSelectedOrdersWithDiscount(0f, "Không có giảm giá");
        }
    }
});

    bottomPanel.add(totalLabel);
    bottomPanel.add(deleteButton);
    bottomPanel.add(orderButton);

    add(bottomPanel, BorderLayout.SOUTH);

    refreshCart();
}

    public void refreshCart() {
        try {
            cartItems = CartService.getInstance().getCartItems();
            tableModel.setRowCount(0);
            for (CartItem item : cartItems) {
                tableModel.addRow(new Object[]{
                    false, // mặc định chưa chọn
                    item.getProductId(), // Product ID (ẩn)
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
                });
            }
            updateTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải giỏ hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedItem() {
        try {
            boolean hasSelection = false;
            
            // Kiểm tra xem có item nào được chọn không
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    hasSelection = true;
                    break;
                }
            }
            
            if (!hasSelection) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa các sản phẩm đã chọn?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Xóa các item đã chọn (duyệt ngược để tránh lỗi index)
            for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
                Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    try {
                        Integer productId = (Integer) tableModel.getValueAt(i, 1); // Cột 1: Product ID
                        if (productId != null) {
                            CartService.getInstance().removeFromCart(productId);
                        }
                    } catch (ClassCastException e) {
                        System.err.println("Lỗi cast Product ID tại dòng " + i + ": " + e.getMessage());
                    }
                }
            }
            
            refreshCart();
            JOptionPane.showMessageDialog(this, "Đã xóa các sản phẩm đã chọn!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

   // Thay thế method updateTotal() trong CartView bằng version này:

private void updateTotal() {
    try {
        double total = 0;
        
        // Chỉ tính tổng các item được chọn
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    Object totalObj = tableModel.getValueAt(i, 5); // Cột 5: Thành tiền
                    if (totalObj != null) {
                        double itemTotal = totalObj instanceof Double ? (Double) totalObj : Double.parseDouble(totalObj.toString());
                        total += itemTotal;
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi tính tổng tại dòng " + i + ": " + e.getMessage());
            }
        }

        totalLabel.setText("Tổng: " + String.format("%.0f", total) + " VNĐ");
        
    } catch (Exception e) {
        System.err.println("Lỗi trong updateTotal(): " + e.getMessage());
        totalLabel.setText("Tổng: 0 VNĐ");
    }
}

 // Phương thức đặt hàng với giảm giá (đã cập nhật theo database hiện tại)
private void placeSelectedOrdersWithDiscount(float discountPercent, String discountInfo) {
    Connection con = null;
    try {
        // Tính toán tổng tiền gốc
        double originalTotal = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                Object totalObj = tableModel.getValueAt(i, 5);
                if (totalObj != null) {
                    originalTotal += totalObj instanceof Double ? (Double) totalObj : Double.parseDouble(totalObj.toString());
                }
            }
        }
        
        // Áp dụng giảm giá
        double discountAmount = originalTotal * discountPercent;
        double finalTotal = originalTotal - discountAmount;

        con = DataBaseConfig.getConnection();
        con.setAutoCommit(false); // Bắt đầu transaction
        
        String orderId = OrderUtils.generateOrderID(AuthUtils.getCurrentUserName(), AuthUtils.getCurrentUserId());

        // Tạo đơn hàng với thông tin giảm giá - KHÔNG sử dụng OUTPUT INSERTED
        String insertOrder = "INSERT INTO Orders (Order_Id, User_ID, Order_Date, Total_Amount, Original_Amount, Discount_Percent, Discount_Info) VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(insertOrder)) {
            stmt.setString(1, orderId);
            stmt.setInt(2, AuthUtils.getCurrentUserId());
            stmt.setDouble(3, finalTotal);
            stmt.setDouble(4, originalTotal);
            stmt.setFloat(5, discountPercent * 100); // Lưu phần trăm (ví dụ: 15.0 cho 15%)
            stmt.setString(6, discountInfo);
            stmt.executeUpdate();
        }

        // Thêm chi tiết đơn hàng và cập nhật tồn kho
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                try {
                    Integer productId = (Integer) tableModel.getValueAt(i, 1);  // Cột 1: Product ID
                    Object quantityObj = tableModel.getValueAt(i, 3);          // Cột 3: Số lượng
                    Object priceObj = tableModel.getValueAt(i, 4);             // Cột 4: Đơn giá
                    
                    if (productId == null || quantityObj == null || priceObj == null) {
                        continue;
                    }
                    
                    int quantity = quantityObj instanceof Integer ? (Integer) quantityObj : Integer.parseInt(quantityObj.toString());
                    double unitPrice = priceObj instanceof Double ? (Double) priceObj : Double.parseDouble(priceObj.toString());
                    
                    if (quantity <= 0) {
                        continue;
                    }

                    // Kiểm tra tồn kho trước khi đặt hàng
                    String checkStock = "SELECT Quantity FROM Products WHERE Product_ID = ?";
                    try (PreparedStatement checkStmt = con.prepareStatement(checkStock)) {
                        checkStmt.setInt(1, productId);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next()) {
                            int availableStock = rs.getInt("Quantity");
                            if (availableStock < quantity) {
                                con.rollback();
                                JOptionPane.showMessageDialog(this, 
                                    "Sản phẩm \"" + tableModel.getValueAt(i, 2) + "\" không đủ hàng!\n" +
                                    "Tồn kho: " + availableStock + ", Yêu cầu: " + quantity, 
                                    "Lỗi tồn kho", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } else {
                            con.rollback();
                            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm ID: " + productId, "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    // Thêm chi tiết đơn hàng - giữ nguyên như code gốc
                    String insertDetail = "INSERT INTO OrderDetails (Order_ID, Product_ID, Quantity, Unit_Price) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = con.prepareStatement(insertDetail)) {
                        stmt.setString(1, orderId);
                        stmt.setInt(2, productId);
                        stmt.setInt(3, quantity);
                        stmt.setDouble(4, unitPrice);
                        stmt.executeUpdate();
                    }

                    // Cập nhật tồn kho
                    String updateQuantity = "UPDATE Products SET Quantity = Quantity - ? WHERE Product_ID = ?";
                    try (PreparedStatement stmt = con.prepareStatement(updateQuantity)) {
                        stmt.setInt(1, quantity);
                        stmt.setInt(2, productId);
                        stmt.executeUpdate();
                    }

                    // Xóa khỏi giỏ hàng
                    CartService.getInstance().removeFromCart(productId);
                    
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi parse dữ liệu tại dòng " + i + ": " + e.getMessage());
                    continue;
                }
            }
        }

        con.commit(); // Commit transaction
        
        // Thông báo thành công với thông tin giảm giá
        String successMessage = discountPercent > 0 ? 
            String.format("🎉 Đặt hàng thành công!\n\n" +
                         "💰 Tổng tiền gốc: %.0f VNĐ\n" +
                         "🎁 Giảm giá: %.0f VNĐ (%.0f%%)\n" +
                         "💳 Thành tiền: %.0f VNĐ\n" +
                         "🎯 Mã giảm giá: %s", 
                         originalTotal, discountAmount, discountPercent * 100, finalTotal, discountInfo) :
            "✅ Đặt hàng thành công!";
            
        JOptionPane.showMessageDialog(this, successMessage, "Thành công", JOptionPane.INFORMATION_MESSAGE);
        
        // Hiển thị hóa đơn
        try {
            new InvoiceView(orderId, AuthUtils.getCurrentUserName());
        } catch (Exception e) {
            System.err.println("Lỗi hiển thị hóa đơn: " + e.getMessage());
        }
        
        refreshCart();

    } catch (SQLException ex) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi database khi đặt hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

// Query để xem lịch sử đơn hàng có giảm giá (để test)
public void viewOrderHistory() {
    String query = """
        SELECT 
            o.Order_ID,
            o.Order_Date,
            o.Original_Amount,
            o.Total_Amount,
            o.Discount_Percent,
            o.Discount_Info,
            (o.Original_Amount - o.Total_Amount) AS Discount_Amount
        FROM Orders o
        WHERE o.User_ID = ?
        ORDER BY o.Order_Date DESC
        """;
    
    // Implementation để hiển thị lịch sử...
}
}