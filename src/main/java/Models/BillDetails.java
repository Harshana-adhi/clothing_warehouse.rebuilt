package Models;

import java.math.BigDecimal;

public class BillDetails {
    private int billDetailId;
    private int billId;
    private Integer stockId; // Changed from int to Integer to allow null
    private int quantity;
    private BigDecimal totalAmount;

    //  Default constructor
    public BillDetails() {}

    // Full constructor
    public BillDetails(int billDetailId, int billId, Integer stockId, int quantity, BigDecimal totalAmount) {
        this.billDetailId = billDetailId;
        this.billId = billId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Constructor for creating new entries (constructor with null stockID)(polymorphism)
    public BillDetails(int billId, Integer stockId, int quantity, BigDecimal totalAmount) {
        this.billId = billId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getBillDetailId() { return billDetailId; }
    public void setBillDetailId(int billDetailId) { this.billDetailId = billDetailId; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public Integer getStockId() { return stockId; }
    public void setStockId(Integer stockId) { this.stockId = stockId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
