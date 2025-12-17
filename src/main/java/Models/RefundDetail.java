package Models;

public class RefundDetail {

    private int refundDetailId;
    private int refundId;
    private int stockId;
    private int quantity;
    private double amount;

    // Default constructor
    public RefundDetail() {}

    // Full constructor
    public RefundDetail(int refundDetailId, int refundId, int stockId,
                        int quantity, double amount) {
        this.refundDetailId = refundDetailId;
        this.refundId = refundId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.amount = amount;
    }

    // Getters and setters
    public int getRefundDetailId() {
        return refundDetailId;
    }

    public void setRefundDetailId(int refundDetailId) {
        this.refundDetailId = refundDetailId;
    }

    public int getRefundId() {
        return refundId;
    }

    public void setRefundId(int refundId) {
        this.refundId = refundId;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // utility method for creating Jtable
    public String[] toValuesArray() {
        return new String[]{
                String.valueOf(refundDetailId),
                String.valueOf(refundId),
                String.valueOf(stockId),
                String.valueOf(quantity),
                String.valueOf(amount)
        };
    }
}
