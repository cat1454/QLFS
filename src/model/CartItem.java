package model;

public class CartItem {
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;

    public CartItem(int productId, String productName,int quantity,double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return quantity * unitPrice; }



    public void setQuantity(int quantity) { this.quantity = quantity; }
}
