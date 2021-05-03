package PZero.Libs.DAO.Entities;

public class accountEntity {
    private int ID;
    private int userID;
    private double balance;
    private boolean approved;
    private String name;

    private userEntity user;

    public accountEntity(int ID, int userID, double balance, boolean approved, String name) {
        this.ID = ID;
        this.userID = userID;
        this.balance = balance;
        this.approved = approved;
        this.name = name;
    }

    public userEntity getUser() {
        return user;
    }

    public void setUser(userEntity user) {
        this.user = user;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
