package PZero.Libs;

import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Interfaces.IBankData;
import Interfaces.IBusinessLogic;
import Models.*;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;

import java.util.ArrayList;

public class BusinessLogic implements IBusinessLogic {

    private IBankData data;

    public BusinessLogic(IBankData data) {
        this.data = data;
    }

    @Override
    public employee adminLogin(String adminUsername, String adminPassword) throws BusinessException {
        userEntity user = data.adminLogin(adminUsername, adminPassword);
        if(user == null) return null;
        else return new employee(
                user.getID(), user.getFname(), user.getLname(), user.getUsername(), user.getPassword()
        );
    }

    @Override
    public ArrayList<transaction> getTransactionLog(String admin, String adminPassword) throws BadLogin, BusinessException {
        ArrayList<transaction> ret = new ArrayList<>();
        if (adminLogin(admin, adminPassword)!= null){
            ArrayList<transactionEntity> query = data.fullTransactionLog();
            for (transactionEntity i : query) {
                ret.add(new transaction(
                        i.getID(), i.getReceivingID(), i.getIssuingID(), i.getAmount(), i.getTimestamp().toString(),
                        i.getReceivingAccount() == null ? null : i.getReceivingAccount().getUser().getUsername(),
                        i.getIssuingAccount() == null ? null : i.getIssuingAccount().getUser().getUsername()
                ));
            }
        }else {
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public ArrayList<pendingTransaction> getPendingTransactions(user user) throws BadLogin, BusinessException {
        ArrayList<pendingTransaction> ret = new ArrayList<>();
        if (login(user.getUsername(), user.getPassword()) != null){
            ArrayList<transactionEntity> query = data.transactionsFromUser(user.getId(), false);
            for (transactionEntity i : query) {
                if (i.getIssuingAccount() != null && i.getReceivingAccount() != null) {
                    ret.add(new pendingTransaction(
                            i.getID(), i.getIssuingAccount().getUser().getFname(),i.getIssuingAccount().getUser().getLname(),i.getAmount(),i.getReceivingAccount().getName()
                    ));
                }
            }
        }else {
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public void approveTransaction(int id) throws InsufficientFunds, BusinessException {
        data.approveTransaction(id);
    }

    @Override
    public ArrayList<user> getAllUsers(String admin, String adminPassword) throws BadLogin, BusinessException {
        ArrayList<user> ret = new ArrayList<>();
        if (adminLogin(admin, adminPassword)!= null){
            ArrayList<userEntity> query = data.getAllUsers();
            for (userEntity i :query) {
                ret.add(new user(
                        i.getID(), i.getFname(), i.getLname(), i.getUsername(),i.getPassword()
                ));
            }
        }else {
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public ArrayList<account> getUserAccounts(String admin, String adminPassword, int userID) throws BadLogin, BusinessException {
        ArrayList<account> ret = new ArrayList<>();
        if (adminLogin(admin, adminPassword)!= null){
            ArrayList<accountEntity> query = data.getAccountsFromUser(userID);
            for (accountEntity i :query) {
                ret.add(new account(
                        i.getID(), i.getName(), i.getBalance(), i.isApproved(), i.getUser().getFname(), i.getUser().getLname(),
                        i.getAprover()==null?"":i.getAprover().getFname(),
                        i.getAprover()==null?"":i.getAprover().getLname()
                ));
            }
        }else{
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public ArrayList<transaction> getTransactionHistory(String admin, String adminPassword, int accountID) throws BadLogin, BusinessException {
        ArrayList<transaction> ret = new ArrayList<>();
        if (adminLogin(admin, adminPassword) != null){
            ArrayList<transactionEntity> query = data.getTransactionsFromAccount(accountID);
            for (transactionEntity i :query) {
                ret.add(new transaction(
                        i.getID(), i.getReceivingID(), i.getIssuingID(), i.getAmount(), i.getTimestamp().toString(),
                        i.getReceivingAccount().getUser().getUsername(), i.getIssuingAccount().getUser().getUsername()
                ));
            }
        }else{
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public void approveAccount(String admin, String adminPassword, int accountID) throws BadLogin, BusinessException {
        employee employee = adminLogin(admin, adminPassword);
        if (employee != null) data.approveAccount(accountID, employee.getEmployeeID());
        else {throw new BadLogin();}
    }

    @Override
    public void addAccount(user loggedUser, String name, double amount) throws BadLogin, BusinessException {
        if (login(loggedUser.getUsername(), loggedUser.getPassword()) != null){
            data.addAccount(loggedUser.getId(), name, amount);
        }else {
            throw new BadLogin();
        }
    }

    @Override
    public user login(String username, String password) throws BusinessException {
        userEntity user = data.login(username, password);
        if(user == null) return null;
        else return new user(
                user.getID(), user.getFname(), user.getLname(), user.getUsername(), user.getPassword()
        );
    }

    @Override
    public boolean isUsernameTaken(String username) throws BusinessException {
        return data.doesUsernameExist(username);
    }

    @Override
    public void addUser(user user) throws BusinessException {
        data.addUser(new userEntity(
                user.getId(), user.getUsername(), user.getPassword(), user.getFname(), user.getLname()
        ));
    }

    @Override
    public ArrayList<account> getUserAccounts(user loggedUser) throws BadLogin, BusinessException {
        ArrayList<account> ret = new ArrayList<>();
        if (loggedUser != null && login(loggedUser.getUsername(), loggedUser.getPassword())!= null){
            ArrayList<accountEntity> query = data.getAccountsFromUser(loggedUser.getId());
            for (accountEntity i :query) {
                ret.add(new account(
                        i.getID(), i.getName(), i.getBalance(), i.isApproved(), i.getUser().getFname(), i.getUser().getLname(),
                        i.getAprover()==null?"":i.getAprover().getFname(),
                        i.getAprover()==null?"":i.getAprover().getLname()
                ));
            }
        }else {
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public boolean accountIdExists(int id) throws BusinessException {
        return data.getAccount(id) != null;
    }

    @Override
    public void createTransaction(user loggedUser, int accountID, double amount, int id) throws BadLogin, BusinessException, InsufficientFunds {
        if(login(loggedUser.getUsername(), loggedUser.getPassword()).getId() == data.getAccount(accountID).getUserID()) {
            if (!data.validBalance(accountID, amount)) throw new InsufficientFunds();
            data.createTransaction(accountID, id, amount, false);
        }else {throw new BadLogin();}
    }

    @Override
    public void cashDeposit(user loggedUser, int accountID, double amount) throws BadLogin, BusinessException, InsufficientFunds {
        if(login(loggedUser.getUsername(), loggedUser.getPassword()).getId() == data.getAccount(accountID).getUserID())
            data.createTransaction(null, accountID, amount, true);
        else {throw new BadLogin();}
    }

    @Override
    public void cashWithdrawal(user loggedUser, int accountID, double amount) throws BadLogin, InsufficientFunds, BusinessException {
        if(login(loggedUser.getUsername(), loggedUser.getPassword()).getId() == data.getAccount(accountID).getUserID()) {
            if (!data.validBalance(accountID, amount)) throw new InsufficientFunds();
            data.createTransaction(accountID, null, amount, true);
        }else {throw new BadLogin();}
    }

    @Override
    public ArrayList<transaction> transactionsFromAccount(user user, int accountID) throws BusinessException, BadLogin {
        ArrayList<transaction> ret = new ArrayList<>();
        if (login(user.getUsername(), user.getPassword()) != null){
            ArrayList<transactionEntity> query = data.getTransactionsFromAccount(accountID);
            for (transactionEntity i :query) {
                ret.add(new transaction(
                        i.getID(), i.getReceivingID(), i.getIssuingID(), i.getAmount(), i.getTimestamp().toString(),
                        i.getReceivingAccount() == null ? null : i.getReceivingAccount().getUser().getUsername(),
                        i.getIssuingAccount() == null ? null : i.getIssuingAccount().getUser().getUsername()
                ));
            }
        }else{
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public void denyTransaction(int id) throws BusinessException {
        data.deleteTransaction(id);
    }

    @Override
    public ArrayList<transaction> getTransactionLog(String username, String password, String filterMethod, String variable) throws BusinessException, BadLogin {
        ArrayList<transaction> ret = new ArrayList<>();
        if (adminLogin(username, password)!= null){
            ArrayList<transactionEntity> query = data.fullTransactionLog(filterMethod, variable);
            for (transactionEntity i : query) {
                ret.add(new transaction(
                        i.getID(), i.getReceivingID(), i.getIssuingID(), i.getAmount(), i.getTimestamp().toString(),
                        i.getReceivingAccount() == null ? null : i.getReceivingAccount().getUser().getUsername(),
                        i.getIssuingAccount() == null ? null : i.getIssuingAccount().getUser().getUsername()
                ));
            }
        }else {
            throw new BadLogin();
        }
        return ret;
    }

    @Override
    public void denyAccount(String username, String password, int accountID) throws BadLogin, BusinessException {
        if (adminLogin(username, password) != null) data.denyAccount(accountID);
        else {throw new BadLogin();}
    }
}
