package Models;

public class Payment {
    private int PaymentId;      // Auto-generated
    private String PayType;
    private double Amount;
    private String PayDate;
    private String EmployeeId;
    private String CustomerId;

    // Constructor without PaymentId (for insert)
    public Payment(String PayType, double Amount, String PayDate, String EmployeeId, String CustomerId) {
        this.PayType = PayType;
        this.Amount = Amount;
        this.PayDate = PayDate;
        this.EmployeeId = EmployeeId;
        this.CustomerId = CustomerId;
    }

    // Constructor with PaymentId (optional)
    public Payment(int PaymentId, String PayType, double Amount, String PayDate, String EmployeeId, String CustomerId) {
        this.PaymentId = PaymentId;
        this.PayType = PayType;
        this.Amount = Amount;
        this.PayDate = PayDate;
        this.EmployeeId = EmployeeId;
        this.CustomerId = CustomerId;
    }

    // Setter for PaymentId (used after auto-generated insert)
    public Payment withPaymentId(int PaymentId) {
        this.PaymentId = PaymentId;
        return this;
    }

    // Getter for PaymentId
    public int getPaymentId() {
        return PaymentId;
    }

    // Optional: other getters if needed
    public String getPayType() { return PayType; }
    public double getAmount() { return Amount; }
    public String getPayDate() { return PayDate; }
    public String getEmployeeId() { return EmployeeId; }
    public String getCustomerId() { return CustomerId; }

    // Convert to array for DAO insert/update
    public String[] toValuesArray() {
        return new String[]{PayType, String.valueOf(Amount), PayDate, EmployeeId, CustomerId};
    }
}
