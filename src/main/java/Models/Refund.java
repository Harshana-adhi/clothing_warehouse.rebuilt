package Models;

public class Refund {
    private String RefundDate;
    private double Amount;
    private String Reason;
    private String RefundMethod;
    private String CustomerId;
    private String BillId;

    public Refund(String RefundDate, double Amount, String Reason, String RefundMethod, String CustomerId, String BillId) {
        this.RefundDate = RefundDate;
        this.Amount = Amount;
        this.Reason = Reason;
        this.RefundMethod = RefundMethod;
        this.CustomerId = CustomerId;
        this.BillId = BillId;
    }

    public String[] toValuesArray() {
        return new String[]{RefundDate, String.valueOf(Amount), Reason, RefundMethod, CustomerId, BillId};
    }
}
