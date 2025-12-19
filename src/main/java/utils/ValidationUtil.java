package utils;

import java.util.regex.Pattern;

public class ValidationUtil {

    /*
       1. Name validation (Employees / Customers)
       Letters only
       Spaces allowed
       No numbers or special characters
    */
    public static boolean isValidName(String name) {
        if (name == null) return false;

        String regex = "^[A-Za-z]+(\\s[A-Za-z]+)*$";
        return Pattern.matches(regex, name.trim());
    }

    /*
       2. Sri Lankan Mobile Number validation
       Allowed formats:
        0774445345
        763499494
        +94763474507
        district wise land line numbers
    */
    public static boolean isValidSriLankanMobile(String mobile) {
        if (mobile == null) return false;

        String regex =
                "^(" +
                        //mobiles
                        "(07\\d{8}|7\\d{8}|\\+947\\d{8})" +

                        "|" +

                        //landlines
                        "(" +
                        "(0?(11|33|31|34|38|81|54|91|21|37|25|55|45|26|65|63|" +
                        "47|41|66|35|52|23|24|27|32|57|36))\\d{7}" +
                        "|" +
                        "(\\+94(11|33|31|34|38|81|54|91|21|37|25|55|45|26|65|63|" +
                        "47|41|66|35|52|23|24|27|32|57|36))\\d{7}" +
                        ")" +
                        ")$";

        return Pattern.matches(regex, mobile.trim());
    }

    /*
       3. Password validation
       - Minimum 6 characters
       - At least one English letter
       - At least one number
       - Can contain any character
    */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;

        String regex = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
        return Pattern.matches(regex, password);
    }

    /*
       4. Username validation
       - Letters, numbers, underscore, dot
       - 4–20 characters
       - No spaces
    */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;

        String regex = "^[A-Za-z0-9._]{4,20}$";
        return Pattern.matches(regex, username.trim());
    }

    /*
       5. Employee ID validation
       Format: E001, E002, ...
    */
    public static boolean isValidEmployeeID(String employeeID) {
        if (employeeID == null) return false;

        String regex = "^E\\d{3,}$";
        return Pattern.matches(regex, employeeID.trim());
    }

    /*
       6. Supplier ID validation
       Format: S001, S002, ...
    */
    public static boolean isValidSupplierID(String supplierID) {
        if (supplierID == null) return false;

        String regex = "^S\\d{3,}$";
        return Pattern.matches(regex, supplierID.trim());
    }

    /*
       7. Customer ID validation
       Format: C001, C002, ...
    */
    public static boolean isValidCustomerID(String customerID) {
        if (customerID == null) return false;

        String regex = "^C\\d{3,}$";
        return Pattern.matches(regex, customerID.trim());
    }

    /*
       8. Item ID validation
       Format: I001, I002, ...
    */
    public static boolean isValidItemID(String itemID) {
        if (itemID == null) return false;

        String regex = "^I\\d{3,}$";
        return Pattern.matches(regex, itemID.trim());
    }
}
