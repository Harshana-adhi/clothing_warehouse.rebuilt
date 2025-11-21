package Models;
public class Invoice {
    private String issueDate;
    private String BillId;

    public Invoice(String issueDate, String BillId) {
        this.issueDate = issueDate;
        this.BillId = BillId;
    }

    public String[] toValuesArray() {
        return new String[]{issueDate, BillId};
    }
}
