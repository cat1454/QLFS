package model;

/**
 * Model class representing an item in an invoice
 * Contains product details for order/invoice display
 */
public class InvoiceItem {
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalItem;
    
    // Constructors
    public InvoiceItem() {
    }
    
    public InvoiceItem(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalItem = quantity * unitPrice;
    }
    
    public InvoiceItem(int productId, String productName, int quantity, double unitPrice, double totalItem) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalItem = totalItem;
    }
    
    // Getters and Setters
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Tự động tính lại total khi thay đổi quantity
        this.totalItem = this.quantity * this.unitPrice;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        // Tự động tính lại total khi thay đổi unit price
        this.totalItem = this.quantity * this.unitPrice;
    }
    
    public double getTotalItem() {
        return totalItem;
    }
    
    public void setTotalItem(double totalItem) {
        this.totalItem = totalItem;
    }
    
    // Utility methods
    /**
     * Calculate total item amount (quantity * unit price)
     * @return calculated total
     */
    public double calculateTotal() {
        this.totalItem = this.quantity * this.unitPrice;
        return this.totalItem;
    }
    
    /**
     * Get formatted unit price as string
     * @return formatted unit price
     */
    public String getFormattedUnitPrice() {
        return String.format("%.0f VNĐ", unitPrice);
    }
    
    /**
     * Get formatted total as string
     * @return formatted total amount
     */
    public String getFormattedTotal() {
        return String.format("%.0f VNĐ", totalItem);
    }
    
    /**
     * Check if item is valid (positive quantity and price)
     * @return true if valid
     */
    public boolean isValid() {
        return quantity > 0 && unitPrice > 0 && 
               productName != null && !productName.trim().isEmpty();
    }
    
    // toString method for debugging
    @Override
    public String toString() {
        return String.format("InvoiceItem{id=%d, name='%s', qty=%d, price=%.2f, total=%.2f}", 
                           productId, productName, quantity, unitPrice, totalItem);
    }
    
    // equals and hashCode for collections
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        InvoiceItem that = (InvoiceItem) obj;
        return productId == that.productId &&
               quantity == that.quantity &&
               Double.compare(that.unitPrice, unitPrice) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + quantity;
        result = 31 * result + (int) (Double.doubleToLongBits(unitPrice) ^ (Double.doubleToLongBits(unitPrice) >>> 32));
        return result;
    }
}