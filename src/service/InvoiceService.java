package service;

import database.DataBaseConfig;
import java.sql.*;
import java.util.*;
import model.InvoiceItem;

public class InvoiceService {
    
    /**
     * Lấy danh sách các item trong hóa đơn
     * @param orderId Mã đơn hàng
     * @return List các InvoiceItem
     */
    public List<InvoiceItem> getInvoiceItems(String orderId) {
        List<InvoiceItem> list = new ArrayList<>();
        String sql = """
            SELECT p.Product_ID, p.Name, od.Quantity, od.Unit_Price
            FROM OrderDetails od
            JOIN Products p ON od.Product_ID = p.Product_ID
            WHERE od.Order_ID = ?
        """;
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int productId = rs.getInt("Product_ID");
                String name = rs.getString("Name");
                int quantity = rs.getInt("Quantity");
                double unitPrice = rs.getDouble("Unit_Price");
                
                // Tạo InvoiceItem với constructor phù hợp
                InvoiceItem item = new InvoiceItem(productId, name, quantity, unitPrice);
                list.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Lấy ngày đặt hàng
     * @param orderId Mã đơn hàng
     * @return Ngày đặt hàng dưới dạng String
     */
    public String getOrderDate(String orderId) {
        String sql = "SELECT FORMAT(Order_Date, 'dd/MM/yyyy HH:mm:ss') as FormattedDate FROM Orders WHERE Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("FormattedDate");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ngày đặt hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return "Không rõ";
    }
    
    /**
     * Lấy tổng tiền sau giảm giá (Final Amount)
     * @param orderId Mã đơn hàng
     * @return Tổng tiền cuối cùng
     */
    public double getTotalAmount(String orderId) {
        String sql = "SELECT Total_Amount FROM Orders WHERE Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("Total_Amount");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng tiền đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy tổng tiền gốc (trước khi giảm giá)
     * @param orderId Mã đơn hàng
     * @return Tổng tiền gốc
     */
    public double getOriginalAmount(String orderId) {
        String sql = "SELECT ISNULL(Original_Amount, Total_Amount) as Original_Amount FROM Orders WHERE Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("Original_Amount");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng tiền gốc: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy thông tin giảm giá (mã giảm giá, lý do)
     * @param orderId Mã đơn hàng
     * @return Thông tin giảm giá
     */
    public String getDiscountInfo(String orderId) {
        String sql = "SELECT Discount_Info FROM Orders WHERE Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String discountInfo = rs.getString("Discount_Info");
                return discountInfo != null ? discountInfo : "Không có";
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin giảm giá: " + e.getMessage());
            e.printStackTrace();
        }
        return "Không có";
    }
    
    /**
     * Lấy số tiền được giảm
     * @param orderId Mã đơn hàng
     * @return Số tiền giảm
     */
    public double getDiscountAmount(String orderId) {
        String sql = """
            SELECT 
                ISNULL(Original_Amount, Total_Amount) - Total_Amount as Discount_Amount 
            FROM Orders 
            WHERE Order_ID = ?
        """;
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("Discount_Amount");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số tiền giảm giá: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy phần trăm giảm giá
     * @param orderId Mã đơn hàng
     * @return Phần trăm giảm giá
     */
    public double getDiscountPercent(String orderId) {
        String sql = "SELECT ISNULL(Discount_Percent, 0) as Discount_Percent FROM Orders WHERE Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("Discount_Percent");
                
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy phần trăm giảm giá: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy thông tin đầy đủ của đơn hàng
     * @param orderId Mã đơn hàng
     * @return Map chứa thông tin đơn hàng
     */
    public Map<String, Object> getOrderFullInfo(String orderId) {
        Map<String, Object> orderInfo = new HashMap<>();
        String sql = """
            SELECT 
                Order_ID,
                User_ID,
                FORMAT(Order_Date, 'dd/MM/yyyy HH:mm:ss') as Order_Date,
                Total_Amount,
                ISNULL(Original_Amount, Total_Amount) as Original_Amount,
                ISNULL(Discount_Percent, 0) as Discount_Percent,
                ISNULL(Discount_Info, 'Không có') as Discount_Info,
                (ISNULL(Original_Amount, Total_Amount) - Total_Amount) as Discount_Amount
            FROM Orders 
            WHERE Order_ID = ?
        """;
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                orderInfo.put("orderId", rs.getString("Order_ID"));
                orderInfo.put("userId", rs.getInt("User_ID"));
                orderInfo.put("orderDate", rs.getString("Order_Date"));
                orderInfo.put("totalAmount", rs.getDouble("Total_Amount"));
                orderInfo.put("originalAmount", rs.getDouble("Original_Amount"));
                orderInfo.put("discountPercent", rs.getDouble("Discount_Percent"));
                orderInfo.put("discountInfo", rs.getString("Discount_Info"));
                orderInfo.put("discountAmount", rs.getDouble("Discount_Amount"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin đầy đủ đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return orderInfo;
    }
    
    /**
     * Kiểm tra xem đơn hàng có tồn tại không
     * @param orderId Mã đơn hàng
     * @return true nếu đơn hàng tồn tại
     */
    public boolean isOrderExists(String orderId) {
        String sql = "SELECT COUNT(*) as Count FROM Orders WHERE Order_ID = ?";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("Count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy danh sách đơn hàng của user
     * @param userId ID người dùng
     * @return List các Map chứa thông tin đơn hàng
     */
    public List<Map<String, Object>> getUserOrders(int userId) {
        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = """
            SELECT 
                Order_ID,
                FORMAT(Order_Date, 'dd/MM/yyyy HH:mm:ss') as Order_Date,
                Total_Amount,
                ISNULL(Original_Amount, Total_Amount) as Original_Amount,
                ISNULL(Discount_Percent, 0) as Discount_Percent,
                ISNULL(Discount_Info, 'Không có') as Discount_Info
            FROM Orders 
            WHERE User_ID = ?
            ORDER BY Order_Date DESC
        """;
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("orderId", rs.getString("Order_ID"));
                order.put("orderDate", rs.getString("Order_Date"));
                order.put("totalAmount", rs.getDouble("Total_Amount"));
                order.put("originalAmount", rs.getDouble("Original_Amount"));
                order.put("discountPercent", rs.getDouble("Discount_Percent"));
                order.put("discountInfo", rs.getString("Discount_Info"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
}