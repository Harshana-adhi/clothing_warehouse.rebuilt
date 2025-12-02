package Models;

public class Supplier {
    private String supplierId;
    private String supName;
    private String tellNo;

    public Supplier(String supplierId, String supName, String tellNo) {
        this.supplierId = supplierId;
        this.supName = supName;
        this.tellNo = tellNo;
    }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getSupName() { return supName; }
    public void setSupName(String supName) { this.supName = supName; }

    public String getTellNo() { return tellNo; }
    public void setTellNo(String tellNo) { this.tellNo = tellNo; }

    public String[] toValuesArray() {
        return new String[]{supplierId, supName, tellNo};
    }
}
