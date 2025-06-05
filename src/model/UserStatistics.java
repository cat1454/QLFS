// File: src/main/java/com/flowershop/models/UserUserStatistics.java
package model;

import java.sql.Date;

public class UserStatistics {
    private int userId;
    private String fullName;
    private int orderCount;
    private double totalSpent;
    private Date lastOrderDate;
    private double avgOrderValue;
    
    // Constructors
    public UserStatistics() {}
    
    // Getters and Setters
    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int userId) { 
        this.userId = userId; 
    }
    
    public String getFullName() { 
        return fullName; 
    }
    
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }
    
    public int getOrderCount() { 
        return orderCount; 
    }
    
    public void setOrderCount(int orderCount) { 
        this.orderCount = orderCount; 
    }
    
    public double getTotalSpent() { 
        return totalSpent; 
    }
    
    public void setTotalSpent(double totalSpent) { 
        this.totalSpent = totalSpent; 
    }
    
    public Date getLastOrderDate() { 
        return lastOrderDate; 
    }
    
    public void setLastOrderDate(Date lastOrderDate) { 
        this.lastOrderDate = lastOrderDate; 
    }
    
    public double getAvgOrderValue() { 
        return avgOrderValue; 
    }
    
    public void setAvgOrderValue(double avgOrderValue) { 
        this.avgOrderValue = avgOrderValue; 
    }
}