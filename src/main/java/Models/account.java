package Models;

public class account {
    private int accountID;
    private String accountType;
    private double balance;
    private boolean approved;
    private String userFname;
    private String userLname;
    private String approvedFname;
    private String approvedLname;

    public account(int accountID, String accountType, double balance, boolean approved, String userFname, String userLname, String approvedFname, String approvedLname) {
        this.accountID = accountID;
        this.accountType = accountType;
        this.balance = balance;
        this.approved = approved;
        this.userFname = userFname;
        this.userLname = userLname;
        this.approvedFname = approvedFname;
        this.approvedLname = approvedLname;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getApprovedFname() {
        return approvedFname;
    }

    public void setApprovedFname(String approvedFname) {
        this.approvedFname = approvedFname;
    }

    public String getApprovedLname() {
        return approvedLname;
    }

    public void setApprovedLname(String approvedLname) {
        this.approvedLname = approvedLname;
    }
}
