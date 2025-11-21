package Models;

public class Supplier {
    private String supplierId;
    private String SupName;
    private String TellNo;

    public Supplier(String supplierId, String SupName, String TellNo) {
        this.supplierId = supplierId;
        this.SupName = SupName;
        this.TellNo = TellNo;
    }

    public String[] toValuesArray() {
        return new String[]{supplierId, SupName, TellNo};
    }
}
