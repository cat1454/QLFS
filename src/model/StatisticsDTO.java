package model ;

import java.util.Date;

/**
 * Data Transfer Object cho các thống kê
 */
public class StatisticsDTO {
    
    // DTO cho thống kê doanh thu theo thời gian
    public static class RevenueByTimeDTO {
        private String timePeriod;
        private double totalRevenue;
        private int orderCount;
        
        public RevenueByTimeDTO() {}
        
        public RevenueByTimeDTO(String timePeriod, double totalRevenue, int orderCount) {
            this.timePeriod = timePeriod;
            this.totalRevenue = totalRevenue;
            this.orderCount = orderCount;
        }
        
        // Getters and Setters
        public String getTimePeriod() { return timePeriod; }
        public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }
        
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    }
    
    // DTO cho thống kê sản phẩm
    public static class ProductStatsDTO {
        private int productId;
        private String productName;
        private double totalRevenue;
        private int quantitySold;
        private int currentStock;
        private double unitPrice;
        
        public ProductStatsDTO() {}
        
        public ProductStatsDTO(int productId, String productName, double totalRevenue, 
                              int quantitySold, int currentStock, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.totalRevenue = totalRevenue;
            this.quantitySold = quantitySold;
            this.currentStock = currentStock;
            this.unitPrice = unitPrice;
        }
        
        // Getters and Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public int getQuantitySold() { return quantitySold; }
        public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
        
        public int getCurrentStock() { return currentStock; }
        public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
        
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    }
    
    // DTO cho thống kê khách hàng
    public static class CustomerStatsDTO {
        private int userId;
        private String userName;
        private String email;
        private String phoneNumber;
        private double totalSpent;
        private int orderCount;
        private Date lastOrderDate;
        private Date registrationDate;
        
        public CustomerStatsDTO() {}
        
        public CustomerStatsDTO(int userId, String userName, String email, String phoneNumber,
                               double totalSpent, int orderCount, Date lastOrderDate, Date registrationDate) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.totalSpent = totalSpent;
            this.orderCount = orderCount;
            this.lastOrderDate = lastOrderDate;
            this.registrationDate = registrationDate;
        }
        
        // Getters and Setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
        
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
        
        public Date getLastOrderDate() { return lastOrderDate; }
        public void setLastOrderDate(Date lastOrderDate) { this.lastOrderDate = lastOrderDate; }
        
        public Date getRegistrationDate() { return registrationDate; }
        public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    }
    
    // DTO cho thống kê tổng quan
    public static class OverallStatsDTO {
        private double totalRevenue;
        private int totalOrders;
        private double averageOrderValue;
        private int totalCustomers;
        private int totalProducts;
        private int lowStockProducts;
        
        public OverallStatsDTO() {}
        
        public OverallStatsDTO(double totalRevenue, int totalOrders, double averageOrderValue,
                              int totalCustomers, int totalProducts, int lowStockProducts) {
            this.totalRevenue = totalRevenue;
            this.totalOrders = totalOrders;
            this.averageOrderValue = averageOrderValue;
            this.totalCustomers = totalCustomers;
            this.totalProducts = totalProducts;
            this.lowStockProducts = lowStockProducts;
        }
        
        // Getters and Setters
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
        
        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
        
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
        
        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
        
        public int getLowStockProducts() { return lowStockProducts; }
        public void setLowStockProducts(int lowStockProducts) { this.lowStockProducts = lowStockProducts; }
    }
}