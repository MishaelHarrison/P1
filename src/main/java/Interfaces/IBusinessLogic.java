package Interfaces;

import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Models.account;
import Models.pendingTransaction;
import Models.transaction;
import Models.user;

import java.util.ArrayList;

public interface IBusinessLogic {

    boolean adminLogin(String adminUsername, String adminPassword);

    ArrayList<transaction> getTransactionLog(String admin, String adminPassword) throws BadLogin, BusinessException;

    ArrayList<pendingTransaction> getPendingTransactions(user user) throws BadLogin, BusinessException;

    void approveTransaction(int id) throws InsufficientFunds, BusinessException;

    ArrayList<user> getAllUsers(String admin, String adminPassword) throws BadLogin, BusinessException;

    ArrayList<account> getUserAccounts(String admin, String adminPassword, int userID) throws BadLogin, BusinessException;

    ArrayList<transaction> getTransactionHistory(String admin, String adminPassword, int accountID) throws BadLogin, BusinessException;

    void approveAccount(String admin, String adminPassword, int accountID) throws BadLogin, BusinessException;

    void addAccount(user loggedUser, String name, double amount) throws BadLogin, BusinessException;

    user login(String username, String password) throws BusinessException;

    boolean isUsernameTaken(String username) throws BusinessException;

    void addUser(user user) throws BusinessException;

    ArrayList<account> getUserAccounts(user loggedUser) throws BadLogin, BusinessException;

    boolean accountIdExists(int id) throws BusinessException;

    void createTransaction(user loggedUser, int accountID, double amount, int id) throws BadLogin, BusinessException, InsufficientFunds;

    void cashDeposit(user loggedUser, int accountID, double amount) throws BadLogin, BusinessException, InsufficientFunds;

    void cashWithdrawal(user loggedUser, int accountID, double amount) throws BadLogin, InsufficientFunds, BusinessException;

    ArrayList<transaction> transactionsFromAccount(user user, int accountID) throws BusinessException, BadLogin;

    void denyTransaction(int id) throws BusinessException;
}
