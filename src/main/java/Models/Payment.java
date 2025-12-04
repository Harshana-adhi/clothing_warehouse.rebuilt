package Models;

import java.math.BigDecimal;
import java.util.Date;

public class Payment {
    private int paymentId;
    private String payType;
    private BigDecimal amount;
    private Date payDate;
    private String employeeId;
    private String customerId;

    // Constructor for new payments (ID auto-generated)
    public Payment(String payType, BigDecimal amount, Date payDate, String employeeId, String customerId) {
        this.payType = payType;
        this.amount = amount;
        this.payDate = payDate;
        this.employeeId = employeeId;
        this.customerId = customerId;
    }

    // Full constructor with ID
    public Payment(int paymentId, String payType, BigDecimal amount, Date payDate, String employeeId, String customerId) {
        this.paymentId = paymentId;
        this.payType = payType;
        this.amount = amount;
        this.payDate = payDate;
        this.employeeId = employeeId;
        this.customerId = customerId;
    }

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Date getPayDate() { return payDate; }
    public void setPayDate(Date payDate) { this.payDate = payDate; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
}
