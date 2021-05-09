package Models;

public class employee {
    private int employeeID;
    private String username;
    private String password;
    private String fname;
    private String lname;

    public employee(int employeeID, String username, String password, String fname, String lname) {
        this.employeeID = employeeID;
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
}
