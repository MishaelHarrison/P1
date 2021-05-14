import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Interfaces.IBankData;
import Interfaces.IBusinessLogic;
import Models.account;
import Models.pendingTransaction;
import Models.transaction;
import Models.user;
import PZero.Libs.BusinessLogic;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class BusinessLogicTest {

    private ArrayList<transactionEntity> transactionTestData(){
        ArrayList<transactionEntity> ret = new ArrayList<>();
        ret.add(new transactionEntity(
                1,null,1,1d,true,new Timestamp(1,1,1,1,1,1,1)
        ));
        ret.add(new transactionEntity(
                2,2,null,2d,false,new Timestamp(2,2,2,2,2,2,2)
        ));
        ret.add(new transactionEntity(
                3,3,3,3d,false,new Timestamp(3,3,3,3,3,3,3)
        ));
        ret.get(0).setReceivingAccount(new accountEntity(
                1,1,1,true,"1"
        ));
        ret.get(1).setIssuingAccount(new accountEntity(
                2,2,2,false,"2"
        ));
        ret.get(2).setIssuingAccount(new accountEntity(
                3,3,3,false,"3"
        ));
        ret.get(2).setReceivingAccount(new accountEntity(
                3,3,3,false,"3"
        ));
        ret.get(0).getReceivingAccount().setUser(new userEntity(
                1,"1","1","1","1"
        ));
        ret.get(1).getIssuingAccount().setUser(new userEntity(
                2,"2","2","2","2"
        ));
        ret.get(2).getIssuingAccount().setUser(new userEntity(
                3,"3","3","3","3"
        ));
        ret.get(2).getReceivingAccount().setUser(new userEntity(
                3,"3","3","3","3"
        ));
        return ret;
    }

    private accountEntity accountEntityData(int seed){
        accountEntity ret = new accountEntity(seed, seed, seed, true, seed+"");
        ret.setUser(userEntityData(seed));
        return ret;
    }

    private account accountData(int seed){
        return new account(seed,seed+"",seed,true,seed+"",seed+"",seed+"",seed+"");
    }

    private userEntity userEntityData(int seed){
        return new userEntity(seed, seed+"", seed+"", seed+"", seed+"");
    }

    private user userData(int seed){
        return new user(seed, seed+"", seed+"", seed+"", seed+"");
    }

    @Test
    void testGetTransactionLog() throws BadLogin, BusinessException {
        ArrayList<transactionEntity> query = transactionTestData();
        IBankData mockBank = mock(IBankData.class);
        when(mockBank.fullTransactionLog()).thenReturn(query);
        when(mockBank.adminLogin("1","1")).thenReturn(userEntityData(1));
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        ArrayList<transaction> output = testSubject.getTransactionLog("1","1");
        Assertions.assertEquals(output.get(0).getTransactionID(), query.get(0).getID());
        Assertions.assertEquals(output.get(1).getTransactionID(), query.get(1).getID());
        Assertions.assertEquals(output.get(2).getTransactionID(), query.get(2).getID());
    }

    @Test
    void getPublicTransactionsTest() throws BusinessException, BadLogin {
        ArrayList<transactionEntity> query = transactionTestData();
        IBankData mockBank = mock(IBankData.class);
        when(mockBank.transactionsFromUser(1, false)).thenReturn(query);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        ArrayList<pendingTransaction> output = testSubject.getPendingTransactions(userData(1));
        Assertions.assertEquals(output.get(0).getPendingTransactionID(), query.get(2).getID());
    }

    @Test
    void approveTransactionTest() throws BusinessException, InsufficientFunds {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        testSubject.approveTransaction(1);
        verify(mockBank).approveTransaction(1);
    }

    @Test
    void getAllUsersTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        ArrayList<userEntity> ret = new ArrayList<>();
        ret.add(userEntityData(1));
        ret.add(userEntityData(1));
        when(mockBank.getAllUsers()).thenReturn(ret);
        when(mockBank.adminLogin("1","1")).thenReturn(userEntityData(1));
        ArrayList<user> output = testSubject.getAllUsers("1","1");
        Assertions.assertEquals(1, output.get(0).getId());
    }

    @Test
    void getUserAccountsTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        ArrayList<accountEntity> ret = new ArrayList<>();
        ret.add(accountEntityData(1));
        ret.add(accountEntityData(2));
        when(mockBank.getAccountsFromUser(1)).thenReturn(ret);
        when(mockBank.adminLogin("1","1")).thenReturn(userEntityData(1));
        ArrayList<account> output = testSubject.getUserAccounts("1","1",1);
        Assertions.assertEquals(1, output.get(0).getAccountID());
        Assertions.assertEquals(2, output.get(1).getAccountID());
    }

    @Disabled
    @Test
    void getTransactionHistoryTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.getTransactionsFromAccount(1)).thenReturn(transactionTestData());
        when(mockBank.adminLogin("1","1")).thenReturn(userEntityData(1));
        ArrayList<transaction> output = testSubject.getTransactionHistory("1","1",1);
        Assertions.assertEquals(1, output.get(0).getTransactionID());
        Assertions.assertEquals(2, output.get(1).getTransactionID());
    }

    @Test
    void approveAccountTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.adminLogin("1","1")).thenReturn(userEntityData(1));
        testSubject.approveAccount("1", "1", 1);
        verify(mockBank).approveAccount(1, 1);
    }

    @Test
    void addAccountTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        testSubject.addAccount(userData(1),"1",1);
        verify(mockBank).addAccount(1,"1",1);
    }

    @Test
    void loginTest() throws BusinessException {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        user output = testSubject.login("1","1");
        Assertions.assertEquals(1,output.getId());
    }

    @Test
    void isUsernameTakenTest() throws BusinessException {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        testSubject.isUsernameTaken("1");
        verify(mockBank).doesUsernameExist("1");
    }

    @Test
    void addUserTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        testSubject.addUser(userData(1));
        verify(mockBank).addUser(any(userEntity.class));
    }

    @Test
    void getUserAccountsTest2() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        ArrayList<accountEntity> ret = new ArrayList<>();
        ret.add(accountEntityData(1));
        ret.add(accountEntityData(2));
        when(mockBank.getAccountsFromUser(1)).thenReturn(ret);
        ArrayList<account> output = testSubject.getUserAccounts(userData(1));
        Assertions.assertEquals(1, output.get(0).getAccountID());
        Assertions.assertEquals(2, output.get(1).getAccountID());
    }

    @Test
    void accountIdExistsTest() throws BusinessException, BadLogin {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.getAccount(1)).thenReturn(accountEntityData(1));
        Assertions.assertTrue(testSubject.accountIdExists(1));
    }

    @Test
    void createTransactionTest() throws BusinessException, BadLogin, InsufficientFunds {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        when(mockBank.getAccount(1)).thenReturn(accountEntityData(1));
        when(mockBank.validBalance(1,1)).thenReturn(true);
        testSubject.createTransaction(userData(1), 1, 1, 1);
        verify(mockBank).createTransaction(1, 1, 1, false);
    }

    @Test
    void cashDepositTest() throws BusinessException, BadLogin, InsufficientFunds {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        when(mockBank.getAccount(1)).thenReturn(accountEntityData(1));
        testSubject.cashDeposit(userData(1), 1, 1);
        verify(mockBank).createTransaction(null, 1, 1, true);
    }

    @Test
    void cashWithdrawalTest() throws BusinessException, BadLogin, InsufficientFunds {
        IBankData mockBank = mock(IBankData.class);
        IBusinessLogic testSubject = new BusinessLogic(mockBank);
        when(mockBank.login("1","1")).thenReturn(userEntityData(1));
        when(mockBank.getAccount(1)).thenReturn(accountEntityData(1));
        when(mockBank.validBalance(1,1)).thenReturn(true);
        testSubject.cashWithdrawal(userData(1), 1, 1);
        verify(mockBank).createTransaction(1, null, 1, true);
    }
}
