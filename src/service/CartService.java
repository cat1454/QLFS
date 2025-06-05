package service;

import database.DataBaseConfig;
import utils.AuthUtils;
import model.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class CartService {
    private Map<Integer, CartItem> cart;
    private static CartService instance;
    
    private CartService() {
        cart = new HashMap<>();
    }
    
    public static CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }
    
    // Lấy toàn bộ giỏ hàng
    public Map<Integer, CartItem> getCart() {
        return cart;
    }
    
    // Lấy danh sách các sản phẩm trong giỏ
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cart.values());
    }
    
    // Thêm vào giỏ hàng
    public void addToCart(int productId, String productName, int quantity, double unitPrice) {
        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            cart.put(productId, new CartItem(productId, productName, quantity, unitPrice));
        }
    }
    
    // Xoá một sản phẩm khỏi giỏ
    public void removeFromCart(int productId) {
        cart.remove(productId);
    }
    
    // Xoá toàn bộ giỏ hàng
    public void clearCart() {
        cart.clear();
    }
    
    // Cập nhật số lượng - FIX: Thêm validation và đảm bảo thành tiền được tính lại
    public void updateQuantity(int productId, int quantity) {
        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            if (quantity > 0) {
                item.setQuantity(quantity);
                // Đảm bảo thành tiền được tính lại trong CartItem
                // (nếu CartItem có method calculateTotalPrice())
            } else {
                // Nếu số lượng <= 0 thì xóa khỏi giỏ hàng
                removeFromCart(productId);
            }
        }
    }
    
    // FIX: Thêm method để cập nhật số lượng từ UI
    public void updateCartItemFromUI(int productId, int newQuantity) {
        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            if (newQuantity > 0) {
                item.setQuantity(newQuantity);
                System.out.println("Updated quantity for product " + productId + " to " + newQuantity);
                System.out.println("New total price: " + item.getTotalPrice());
            } else {
                removeFromCart(productId);
            }
        }
    }
    
    // FIX: Thêm method để lấy CartItem theo productId
    public CartItem getCartItem(int productId) {
        return cart.get(productId);
    }
    
    // FIX: Method để tính tổng tiền của giỏ hàng
    public double getCartTotal() {
        return cart.values().stream()
                   .mapToDouble(CartItem::getTotalPrice)
                   .sum();
    }
    
    // FIX: Method để kiểm tra giỏ hàng có trống không
    public boolean isEmpty() {
        return cart.isEmpty();
    }
    
    // FIX: Method để lấy số lượng item trong giỏ
    public int getCartItemCount() {
        return cart.size();
    }
    
    // Đặt hàng nhiều sản phẩm
    public boolean placeOrder(List<CartItem> items) {
        try (Connection con = DataBaseConfig.getConnection()) {
            con.setAutoCommit(false);
            int userId = AuthUtils.getCurrentUserId();
            String orderId = utils.OrderUtils.generateOrderID(AuthUtils.getCurrentUserName(), AuthUtils.getCurrentUserId());
            double total = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
            
            // Thêm vào Orders
            String sqlOrder = "INSERT INTO Orders (Order_Id, User_ID, Order_Date, Total_Amount) VALUES (?, ?, GETDATE(), ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlOrder)) {
                ps.setString(1, orderId);
                ps.setInt(2, userId);
                ps.setDouble(3, total);
                ps.executeUpdate();
            }
            
            // Thêm vào OrderDetails
            String sqlDetail = "INSERT INTO OrderDetails (Order_ID, Product_ID, Quantity, Unit_Price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlDetail)) {
                for (CartItem item : items) {
                    ps.setString(1, orderId);
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getUnitPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            // Trừ hàng trong bảng Products
            String sqlUpdateStock = "UPDATE Products SET Quantity = Quantity - ? WHERE Product_ID = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateStock)) {
                for (CartItem item : items) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            con.commit();
            clearCart(); // Xoá giỏ hàng sau khi đặt hàng
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}