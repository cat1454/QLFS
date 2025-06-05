package service;

import OrderView.InvoiceView;
import OrderView.LuckyWheel;
import database.DataBaseConfig;
import model.CartItem;
import utils.AuthUtils;
import utils.OrderUtils;

import javax.swing.*;
import java.sql.*;

public class OrderService {
    private static final int MAX_ORDER_QUANTITY = 100;
    private static final int MIN_ORDER_QUANTITY = 1;
    
    private static OrderService instance;
    
    private OrderService() {}
    
    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    /**
     * Đặt hàng cho một sản phẩm đơn lẻ
     */
    public boolean placeSingleOrder(int productId, String productName, int quantity, double unitPrice, float discountRate) {
        // Validation đầu vào
        if (!validateOrderInput(quantity, productId)) {
            return false;
        }
        
        // Kiểm tra tồn kho trước khi đặt hàng
        if (!checkProductStock(productId, quantity)) {
            JOptionPane.showMessageDialog(null, "Không đủ hàng trong kho!");
            return false;
        }

        Connection con = null;
        try {
            con = DataBaseConfig.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction
            
            String orderId = OrderUtils.generateOrderID(AuthUtils.getCurrentUserName(), AuthUtils.getCurrentUserId());
            
            

            // 1. Tạo đơn hàng chính
            double originalAmount = unitPrice * quantity;
            double totalAmount = calculateTotalAmount(unitPrice, quantity,discountRate);
            System.out.println("Original Amount: " + originalAmount);
            System.out.println("Discount Rate: " + discountRate);
            System.out.println("Total Amount: " + totalAmount);
            String actualOrderId = createMainOrder(con, orderId, totalAmount,originalAmount,discountRate);
            if (actualOrderId == null) {
                throw new SQLException("Không thể tạo đơn hàng chính!");
            }

            // 2. Thêm chi tiết đơn hàng
            if (!createOrderDetail(con, actualOrderId, productId, quantity, unitPrice, discountRate)) {
                throw new SQLException("Không thể tạo chi tiết đơn hàng!");
            }

            // 3. Cập nhật tồn kho
            if (!updateProductStock(con, productId, quantity)) {
                throw new SQLException("Không thể cập nhật tồn kho!");
            }

            con.commit(); // Commit transaction
            
            // Hiển thị thông báo thành công và hóa đơn
            showSuccessMessage(actualOrderId, productName, quantity, totalAmount);
            showInvoice(actualOrderId);
            
            return true;
            
        } catch (SQLException ex) {
            handleTransactionError(con, ex);
            return false;
        } finally {
            closeConnection(con);
        }
    }
    
    // Private helper methods
    
    private boolean validateOrderInput(int quantity, int productId) {
        if (quantity < MIN_ORDER_QUANTITY || quantity > MAX_ORDER_QUANTITY) {
            JOptionPane.showMessageDialog(null, 
                "Số lượng phải từ " + MIN_ORDER_QUANTITY + " đến " + MAX_ORDER_QUANTITY + "!");
            return false;
        }
        
        if (productId <= 0) {
            JOptionPane.showMessageDialog(null, "ID sản phẩm không hợp lệ!");
            return false;
        }
        
        return true;
    }
    
    private boolean checkProductStock(int productId, int requestedQuantity) {
        String sql = "SELECT Quantity FROM Products WHERE Product_ID = ?";
        try (Connection con = DataBaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int availableStock = rs.getInt("Quantity");
                return availableStock >= requestedQuantity;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
    
    private double calculateTotalAmount(double unitPrice, int quantity, float discountRate) {
        double subtotal = unitPrice * quantity;
        double discountAmount = subtotal * discountRate;
        return subtotal - discountAmount;
    }
    
   private String createMainOrder(Connection con, String orderId, double originalAmount, double originalAmount1, float discountPercent) throws SQLException {
    double totalAmount = originalAmount * (1 - discountPercent );
    System.out.println(discountPercent);
    String sql = "INSERT INTO Orders (Order_ID, User_ID, Order_Date, Total_Amount, Original_Amount, Discount_Percent) " +
                 "OUTPUT INSERTED.Order_ID VALUES (?, ?, GETDATE(), ?, ?, ?)";

    try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setString(1, orderId);
        stmt.setInt(2, AuthUtils.getCurrentUserId());
        stmt.setDouble(3, totalAmount);
        stmt.setDouble(4, originalAmount);
        stmt.setFloat(5, discountPercent);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("Order_ID");
        }
    }

    return null;
}

    private boolean createOrderDetail(Connection con, String orderId, int productId, 
                                    int quantity, double unitPrice, float discountRate) throws SQLException {
        String sql = "INSERT INTO OrderDetails (Order_ID, Product_ID, Quantity, Unit_Price, Discount_Rate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, unitPrice);
            stmt.setFloat(5, discountRate);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private boolean updateProductStock(Connection con, int productId, int quantity) throws SQLException {
        String sql = "UPDATE Products SET Quantity = Quantity - ? WHERE Product_ID = ? AND Quantity >= ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void handleTransactionError(Connection con, SQLException ex) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi khi đặt hàng: " + ex.getMessage());
    }
    
    private void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void showSuccessMessage(String orderId, String productName, int quantity, double totalAmount) {
        String message = String.format(
            "Đặt hàng thành công!\n" +
            "Mã đơn hàng: %s\n" +
            "Sản phẩm: %s\n" +
            "Số lượng: %d\n" +
            "Tổng tiền: %.0f VNĐ",
            orderId, productName, quantity, totalAmount
        );
        JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showInvoice(String orderId) {
        try {
            new InvoiceView(orderId, AuthUtils.getCurrentUserName());
        } catch (Exception ex) {
            System.err.println("Lỗi khi hiển thị hóa đơn: " + ex.getMessage());
        }
    }
}