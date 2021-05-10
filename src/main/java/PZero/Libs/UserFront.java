package PZero.Libs;

import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Interfaces.IBusinessLogic;
import Interfaces.IUserFront;
import Models.*;
import PZero.Libs.DAO.seedData;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class UserFront implements IUserFront {

    private static final Logger log=Logger.getLogger(UserFront.class);

    private final IBusinessLogic logic;

    private final Javalin app;

    public UserFront(IBusinessLogic logic){
        app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(8001);
        this.logic = logic;
    }

    @Override
    public void start() {
        app.before(ctx -> System.out.println(ctx.url()));

        //establish connection is good
        app.get("/yo_we_good",ctx -> {
            ctx.json("yea we good");
        });

        //basic login
        //(username, password)
        app.get("/user/*/*",ctx -> {
            user user = logic.login(ctx.splat(0), ctx.splat(1));
            if(user == null){
                ctx.status(400);
            }else {
                user.setPendingCount(logic.getPendingTransactions(user).size());
                ctx.json(user);
            }
        });

        //create user
        //(username, password, fname, lname)
        app.post("/user/*/*/*/*",ctx -> {
            user user = new user( 0,
                    ctx.splat(2),
                    ctx.splat(3),
                    ctx.splat(0),
                    ctx.splat(1)
            );
            if(logic.isUsernameTaken(user.getUsername())){
                ctx.status(400);
                return;
            }
            try{
                logic.addUser(user);
                ctx.json(logic.login(user.getUsername(), user.getPassword()));
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //get all users accounts
        //(username, password)
        app.get("/accounts/*/*",ctx -> {
            ArrayList<account> ret = null;
            try{
                ret = logic.getUserAccounts(logic.login(ctx.splat(0), ctx.splat(1)));
                ctx.json(ret);
            }
            catch (BadLogin e){
                ctx.status(400);
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //transaction
        //(username, password, accountID, amount, deposit/withdrawal/receivingAccountID)
        app.post("/transactions/*/*/*/*/*",ctx -> {
            ctx.status(500);
            try{
                if(Double.parseDouble(ctx.splat(3)) < 0){
                    ctx.status(400);
                    return;
                }
                user user = logic.login(ctx.splat(0), ctx.splat(1));
                if (user == null) throw new BadLogin();
                switch (ctx.splat(4)){
                    case "deposit":
                        logic.cashDeposit(user, Integer.parseInt(ctx.splat(2)), Double.parseDouble(ctx.splat(3)));
                        break;
                    case "withdrawal":
                        logic.cashWithdrawal(user, Integer.parseInt(ctx.splat(2)), Double.parseDouble(ctx.splat(3)));
                        break;
                    default:
                        logic.createTransaction(user, Integer.parseInt(ctx.splat(2)), Double.parseDouble(ctx.splat(3)), Integer.parseInt(ctx.splat(4)));
                        break;
                }
                ctx.status(200);
            }
            catch (BadLogin e){
                ctx.status(400);
            } catch (InsufficientFunds e) {
                ctx.status(400);
            } catch (NumberFormatException e) {
                ctx.status(400);
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //add account
        //(username, password, accountName, startingBalance)
        app.post("/accounts/*/*/*/*",ctx -> {
            try{
                user user = logic.login(ctx.splat(0), ctx.splat(1));
                if (user == null) throw new BadLogin();
                logic.addAccount(user, ctx.splat(2), Double.parseDouble(ctx.splat(3)));
            }
            catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //view users transactions
        //(username, password, accountID)
        app.get("/transactions/*/*/*",ctx -> {
            try{
                user user = logic.login(ctx.splat(0), ctx.splat(1));
                if (user == null) throw new BadLogin();
                ctx.json(logic.transactionsFromAccount(user, Integer.parseInt(ctx.splat(2))));
            }
            catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //view users pending transactions
        //(username, password)
        app.get("/transactions/*/*",ctx -> {
            try{
                user user = logic.login(ctx.splat(0), ctx.splat(1));
                if (user == null) throw new BadLogin();
                ctx.json(logic.getPendingTransactions(user));
            }
            catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //accept transaction
        //(username, password, transactionID)
        app.put("/transactions/*/*/*",ctx -> {
            try{
                user user = logic.login(ctx.splat(0), ctx.splat(1));
                if (user == null) throw new BadLogin();
                logic.approveTransaction(Integer.parseInt(ctx.splat(2)));
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            }catch (InsufficientFunds insufficientFunds) {
                ctx.status(400);
            }catch (BusinessException e){
                ctx.status(500);
            }
        });

        //deny transaction
        //(username, password, transactionID)
        app.delete("/transactions/*/*/*",ctx -> {
            try{
                user user = logic.login(ctx.splat(0), ctx.splat(1));
                if (user == null) throw new BadLogin();
                logic.denyTransaction(Integer.parseInt(ctx.splat(2)));
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            }
        });

        //admin login
        //(username, password)
        app.get("/admin/login/*/*",ctx -> {
            try{
                employee admin = logic.adminLogin(ctx.splat(0), ctx.splat(1));
                if (admin == null) throw new BadLogin();
                ctx.json(admin);
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            }
        });

        //full transaction history
        //(username, password)
        app.get("/admin/transactions/*/*",ctx -> {
            try{
                ArrayList<transaction> log = logic.getTransactionLog(ctx.splat(0), ctx.splat(1));
                ctx.json(log);
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            } catch (Throwable e){
                System.out.println(e.getMessage());
            }
        });

        //full transaction history filtered
        //(username, password, filterMethod, variable)
        app.get("/admin/transactionsFiltered/*/*/*/*",ctx -> {
            try{
                ArrayList<transaction> log = logic.getTransactionLog(ctx.splat(0), ctx.splat(1),
                        ctx.splat(2), ctx.splat(3));
                ctx.json(log);
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                System.out.println(e.getCause());
                ctx.status(500);
            } catch (Throwable e){
                System.out.println(e.getMessage());
            }
        });

        //view all users
        //(username, password)
        app.get("/admin/users/*/*",ctx -> {
            try{
                ArrayList<user> log = logic.getAllUsers(ctx.splat(0), ctx.splat(1));
                ctx.json(log);
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            }
        });

        //view accounts from user
        //(username, password, userID)
        app.get("/admin/accounts/*/*/*",ctx -> {
            try{
                ArrayList<account> log = logic.getUserAccounts(ctx.splat(0), ctx.splat(1), Integer.parseInt(ctx.splat(2)));
                ctx.json(log);
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            }
        });

        //approve account
        //(username, password, accountID)
        app.put("/admin/accounts/*/*/*",ctx -> {
            try{
                logic.approveAccount(ctx.splat(0), ctx.splat(1), Integer.parseInt(ctx.splat(2)));
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            }
        });

        //deny account
        //(username, password, accountID)
        app.delete("/admin/accounts/*/*/*",ctx -> {
            try{
                logic.denyAccount(ctx.splat(0), ctx.splat(1), Integer.parseInt(ctx.splat(2)));
            }catch (BadLogin e){
                ctx.status(400);
            }catch (NumberFormatException e){
                ctx.status(400);
            } catch (BusinessException e){
                ctx.status(500);
            }
        });

        //seed the database
        app.post("/seed",ctx -> {
            seedData.reset();
        });
    }
}
