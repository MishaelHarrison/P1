package PZero.Libs.DAO.Entities;

import java.sql.Timestamp;

public class transactionEntity {
    private int ID;
    private Integer issuingID;
    private Integer receivingID;
    private double amount;
    private boolean approved;
    private Timestamp timestamp;

    private accountEntity issuingAccount;
    private accountEntity receivingAccount;

    public transactionEntity(int ID, Integer issuingID, Integer receivingID, double amount, boolean approved, Timestamp timestamp) {
        this.ID = ID;
        this.issuingID = issuingID;
        this.receivingID = receivingID;
        this.amount = amount;
        this.approved = approved;
        this.timestamp = timestamp;
    }

    public accountEntity getIssuingAccount() {
        return issuingAccount;
    }

    public void setIssuingAccount(accountEntity issuingAccount) {
        this.issuingAccount = issuingAccount;
    }

    public accountEntity getReceivingAccount() {
        return receivingAccount;
    }

    public void setReceivingAccount(accountEntity receivingAccount) {
        this.receivingAccount = receivingAccount;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Integer getIssuingID() {
        return issuingID;
    }

    public void setIssuingID(Integer issuingID) {
        this.issuingID = issuingID;
    }

    public Integer getReceivingID() {
        return receivingID;
    }

    public void setReceivingID(Integer receivingID) {
        this.receivingID = receivingID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
