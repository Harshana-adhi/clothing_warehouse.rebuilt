package Models;

public class Customer {
    private String customerId;
    private String CusName;
    private String Address;
    private String TelNo;

    public Customer(String customerId, String CusName, String Address, String TelNo) {
        this.customerId = customerId;
        this.CusName = CusName;
        this.Address = Address;
        this.TelNo = TelNo;
    }

    public String[] toValuesArray() {
        return new String[]{customerId, CusName, Address, TelNo};
    }
}
