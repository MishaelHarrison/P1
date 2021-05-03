package Models;

import java.sql.Timestamp;

public class transaction implements Comparable {
    private int transactionID;
    private Integer receivingAccountID;
    private Integer issuingAccountID;
    private double amount;
    private Timestamp timestamp;
    private String receivingUsername;
    private String issuingUsername;

    public transaction(int transactionID, Integer receivingAccountID, Integer issuingAccountID, double amount, Timestamp timestamp, String receivingUsername, String issuingUsername) {
        this.transactionID = transactionID;
        this.receivingAccountID = receivingAccountID;
        this.issuingAccountID = issuingAccountID;
        this.amount = amount;
        this.timestamp = timestamp;
        this.receivingUsername = receivingUsername;
        this.issuingUsername = issuingUsername;
    }

    public String getReceivingUsername() {
        return receivingUsername;
    }

    public void setReceivingUsername(String receivingUsername) {
        this.receivingUsername = receivingUsername;
    }

    public String getIssuingUsername() {
        return issuingUsername;
    }

    public void setIssuingUsername(String issuingUsername) {
        this.issuingUsername = issuingUsername;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public Integer getReceivingAccountID() {
        return receivingAccountID;
    }

    public void setReceivingAccountID(Integer receivingAccountID) {
        this.receivingAccountID = receivingAccountID;
    }

    public Integer getIssuingAccountID() {
        return issuingAccountID;
    }

    public void setIssuingAccountID(Integer issuingAccountID) {
        this.issuingAccountID = issuingAccountID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Object o) {
        return timestamp.compareTo(((transaction)o).timestamp);
    }
}
