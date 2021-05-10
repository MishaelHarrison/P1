package Interfaces;

import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;

import java.util.ArrayList;

public interface IBankData {
    void createTransaction(Integer issuingID, Integer receivingID, double amount, boolean preApproved) throws BusinessException, InsufficientFunds;

    accountEntity getAccount(int accountID) throws BusinessException;

    ArrayList<accountEntity> getAccountsFromUser(int id) throws BusinessException;

    void addUser(userEntity userEntity) throws BusinessException;

    boolean doesUsernameExist(String username) throws BusinessException;

    userEntity login(String username, String password) throws BusinessException;

    void addAccount(int userID, String name, double amount) throws BusinessException;

    void approveAccount(int accountID, int employeeID) throws BusinessException;

    ArrayList<transactionEntity> getTransactionsFromAccount(int accountID) throws BusinessException;

    ArrayList<userEntity> getAllUsers() throws BusinessException;

    void approveTransaction(int id) throws BusinessException, InsufficientFunds;

    ArrayList<transactionEntity> transactionsFromUser(int id, boolean isApproved) throws BusinessException;

    ArrayList<transactionEntity> fullTransactionLog() throws BusinessException;

    boolean validBalance(int accountID, double amount) throws BusinessException;

    void deleteTransaction(int id) throws BusinessException;

    int GetPendingCount(int id) throws BusinessException;

    userEntity adminLogin(String adminUsername, String adminPassword) throws BusinessException;

    ArrayList<transactionEntity> fullTransactionLog(String filterMethod, String variable) throws BusinessException;

    void denyAccount(int accountID) throws BusinessException;
}