// File: src/main/java/com/flowershop/models/DailyRevenue.java
package model;

public class DailyRevenue {
    private String dayName;
    private int dayNumber;
    private int orderCount;
    private double totalRevenue;
    private double avgRevenue;
    
    // Constructors
    public DailyRevenue() {}
    
    // Getters and Setters
    public String getDayName() { 
        return dayName; 
    }
    
    public void setDayName(String dayName) { 
        this.dayName = dayName; 
    }
    
    public int getDayNumber() { 
        return dayNumber; 
    }
    
    public void setDayNumber(int dayNumber) { 
        this.dayNumber = dayNumber; 
    }
    
    public int getOrderCount() { 
        return orderCount; 
    }
    
    public void setOrderCount(int orderCount) { 
        this.orderCount = orderCount; 
    }
    
    public double getTotalRevenue() { 
        return totalRevenue; 
    }
    
    public void setTotalRevenue(double totalRevenue) { 
        this.totalRevenue = totalRevenue; 
    }
    
    public double getAvgRevenue() { 
        return avgRevenue; 
    }
    
    public void setAvgRevenue(double avgRevenue) { 
        this.avgRevenue = avgRevenue; 
    }
}