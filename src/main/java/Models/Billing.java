package Models;

import java.math.BigDecimal;
import java.util.Date;

public class Billing {
    private int billId;
    private Date billDate;
    private BigDecimal amount;
    private String billDescription;
    private String billStatus;
    private Integer paymentId; // Nullable
    private String customerId;

    public Billing() {}

    // Constructor for new bills (ID auto-generated)
    public Billing(Date billDate, BigDecimal amount, String billDescription, String billStatus, Integer paymentId, String customerId) {
        this.billDate = billDate;
        this.amount = amount;
        this.billDescription = billDescription;
        this.billStatus = billStatus;
        this.paymentId = paymentId;
        this.customerId = customerId;
    }

    // Full constructor with ID
    public Billing(int billId, Date billDate, BigDecimal amount, String billDescription, String billStatus, Integer paymentId, String customerId) {
        this.billId = billId;
        this.billDate = billDate;
        this.amount = amount;
        this.billDescription = billDescription;
        this.billStatus = billStatus;
        this.paymentId = paymentId;
        this.customerId = customerId;
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public Date getBillDate() { return billDate; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getBillDescription() { return billDescription; }
    public void setBillDescription(String billDescription) { this.billDescription = billDescription; }

    public String getBillStatus() { return billStatus; }
    public void setBillStatus(String billStatus) { this.billStatus = billStatus; }

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
}
