package Models;

import java.util.Date;

public class Refund {

    private int refundId;
    private Date refundDate;
    private double amount;
    private String reason;
    private String refundMethod;
    private String customerId;
    private int billId;

    // Default constructor
    public Refund() {}

    // Full constructor
    public Refund(int refundId, Date refundDate, double amount, String reason,
                  String refundMethod, String customerId, int billId) {
        this.refundId = refundId;
        this.refundDate = refundDate;
        this.amount = amount;
        this.reason = reason;
        this.refundMethod = refundMethod;
        this.customerId = customerId;
        this.billId = billId;
    }

    // Getters and setters
    public int getRefundId() {
        return refundId;}

    public void setRefundId(int refundId) {
        this.refundId = refundId;}

    public Date getRefundDate() {
        return refundDate;}

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;}

    public double getAmount() {
        return amount;}

    public void setAmount(double amount) {
        this.amount = amount;}

    public String getReason() {
        return reason;}

    public void setReason(String reason) {
        this.reason = reason;}

    public String getRefundMethod() {
        return refundMethod;}

    public void setRefundMethod(String refundMethod) {
        this.refundMethod = refundMethod;}

    public String getCustomerId() {
        return customerId;}

    public void setCustomerId(String customerId) {
        this.customerId = customerId;}

    public int getBillId() {
        return billId;}

    public void setBillId(int billId) {
        this.billId = billId;}

    // Helper for tables
    public String[] toValuesArray() {
        return new String[]{
                String.valueOf(refundId),
                String.valueOf(refundDate),
                String.valueOf(amount),
                reason,
                refundMethod,
                customerId,
                String.valueOf(billId)
        };
    }
}
