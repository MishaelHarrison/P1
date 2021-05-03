package Models;

import java.util.List;

public class user {
    private int id;
    private String fname;
    private String lname;
    private String username;
    private String password;
    private boolean isEmployee;
    private List<account> accounts;
    private List<pendingTransaction> pendingTransactions;

    public user(int id, String fname, String lname, String username, String password) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.password = password;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmployee() {
        return isEmployee;
    }

    public void setEmployee(boolean employee) {
        isEmployee = employee;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
