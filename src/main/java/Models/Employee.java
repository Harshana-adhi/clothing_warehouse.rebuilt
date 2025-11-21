package Models;

public class Employee {
    private String employeeId;
    private String empName;
    private String tellNo;
    private double salary;
    private String empPosition;

    // Default constructor
    public Employee() {}

    // Full constructor
    public Employee(String employeeId, String empName, String tellNo, double salary, String empPosition) {
        this.employeeId = employeeId;
        this.empName = empName;
        this.tellNo = tellNo;
        this.salary = salary;
        this.empPosition = empPosition;
    }

    // Getters and setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getTellNo() { return tellNo; }
    public void setTellNo(String tellNo) { this.tellNo = tellNo; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getEmpPosition() { return empPosition; }
    public void setEmpPosition(String empPosition) { this.empPosition = empPosition; }

    // Helper to convert to String array (for table models etc.)
    public String[] toValuesArray() {
        return new String[]{employeeId, empName, tellNo, String.valueOf(salary), empPosition};
    }
}
