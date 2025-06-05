package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Model class đại diện cho một đơn hàng
 */
public class Order {
    private String orderId;
    private int userId;
    private Timestamp orderDate;
    private double totalAmount;
    private OrderStatus status;
    
    // Enum cho trạng thái đơn hàng
    public enum OrderStatus {
        PENDING("Đang xử lý"),
        CONFIRMED("Đã xác nhận"),
        SHIPPING("Đang giao hàng"),
        DELIVERED("Đã giao hàng"),
        CANCELLED("Đã hủy");
        
        private final String displayName;
        
        OrderStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor đầy đủ
    public Order(String orderId, int userId, Timestamp orderDate, double totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING; // Mặc định là đang xử lý
    }
    
    // Constructor với status
    public Order(String orderId, int userId, Timestamp orderDate, double totalAmount, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }
    
    // Constructor mặc định
    public Order() {
        this.status = OrderStatus.PENDING;
    }

    // Getters và Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Tổng tiền không thể âm");
        }
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    // Utility methods
    
    /**
     * Lấy ngày đặt hàng dưới dạng chuỗi định dạng
     */
    public String getFormattedOrderDate() {
        if (orderDate == null) return "";
        
        LocalDateTime dateTime = orderDate.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    /**
     * Lấy tổng tiền định dạng VNĐ
     */
    public String getFormattedTotalAmount() {
        return String.format("%.0f VNĐ", totalAmount);
    }
    
    /**
     * Kiểm tra đơn hàng có thể hủy được không
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    /**
     * Kiểm tra đơn hàng đã hoàn thành chưa
     */
    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED;
    }
    
    /**
     * Kiểm tra đơn hàng có hợp lệ không
     */
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty() && 
               userId > 0 && 
               totalAmount >= 0 && 
               orderDate != null;
    }
    
    /**
     * Tạo bản sao của đơn hàng
     */
    public Order copy() {
        return new Order(this.orderId, this.userId, this.orderDate, this.totalAmount, this.status);
    }
    
    // Override methods
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Order order = (Order) obj;
        return Objects.equals(orderId, order.orderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
    
    @Override
    public String toString() {
        return String.format(
            "Order{orderId='%s', userId=%d, orderDate=%s, totalAmount=%.2f, status=%s}",
            orderId, userId, 
            orderDate != null ? getFormattedOrderDate() : "null",
            totalAmount, 
            status != null ? status.getDisplayName() : "null"
        );
    }
    
    /**
     * Chuyển đổi sang định dạng hiển thị cho người dùng
     */
    public String toDisplayString() {
        return String.format(
            "Mã đơn: %s | Ngày: %s | Tổng tiền: %s | Trạng thái: %s",
            orderId,
            getFormattedOrderDate(),
            getFormattedTotalAmount(),
            status != null ? status.getDisplayName() : "Không rõ"
        );
    }
    
    // Builder pattern cho việc tạo Order phức tạp
    public static class Builder {
        private String orderId;
        private int userId;
        private Timestamp orderDate;
        private double totalAmount;
        private OrderStatus status = OrderStatus.PENDING;
        
        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }
        
        public Builder setUserId(int userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder setOrderDate(Timestamp orderDate) {
            this.orderDate = orderDate;
            return this;
        }
        
        public Builder setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder setStatus(OrderStatus status) {
            this.status = status;
            return this;
        }
        
        public Order build() {
            Order order = new Order(orderId, userId, orderDate, totalAmount, status);
            if (!order.isValid()) {
                throw new IllegalStateException("Dữ liệu đơn hàng không hợp lệ");
            }
            return order;
        }
    }
}