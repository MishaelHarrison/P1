package PZero.Libs;

import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Exceptions.NoError;
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
        app.before(ctx -> log.trace("Received HTTP request: "+ctx.url()));

        //establish connection is good
        app.get("/yo_we_good",ctx -> {
            log.trace("validating connection");
            ctx.json("yea we good");
        });

        //basic login
        //(username, password)
        app.get("/user/*/*",ctx -> {

            //doing to test that the front end properly shows errors
            try {
                if (ctx.splat(0).equals("buggy"))
                    throw new BusinessException("I done did broke everything for you, your welcome");
                if (ctx.splat(0).equals("wapol"))
                    throw new Exception("I done did broke everything for you, your welcome");
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
                return;
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
                return;
            }

            user user = logic.login(ctx.splat(0), ctx.splat(1));
            if(user == null){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
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
                log.warn("attempted to assign non-unique username");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Username taken"));
            }
            try{
                logic.addUser(user);
                ctx.json(logic.login(user.getUsername(), user.getPassword()));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //get all users accounts
        //(username, password)
        app.get("/accounts/*/*",ctx -> {
            ArrayList<account> ret;
            try{
                ret = logic.getUserAccounts(logic.login(ctx.splat(0), ctx.splat(1)));
                ctx.json(ret);
            }
            catch (BadLogin e){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            } catch (InsufficientFunds e) {
                log.warn("attempted a transaction with insufficient funds");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "insufficient funds"));
            } catch (NumberFormatException e) {
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (InsufficientFunds insufficientFunds) {
                log.warn("attempted a transaction with insufficient funds");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "insufficient funds"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //full transaction history
        //(username, password)
        app.get("/admin/transactions/*/*",ctx -> {
            try{
                ArrayList<transaction> log = logic.getTransactionLog(ctx.splat(0), ctx.splat(1));
                ctx.json(log);
            }catch (BadLogin e){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
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
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //view all users
        //(username, password)
        app.get("/admin/users/*/*",ctx -> {
            try{
                ArrayList<user> log = logic.getAllUsers(ctx.splat(0), ctx.splat(1));
                ctx.json(log);
            }catch (BadLogin e){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //view accounts from user
        //(username, password, userID)
        app.get("/admin/accounts/*/*/*",ctx -> {
            try{
                ArrayList<account> log = logic.getUserAccounts(ctx.splat(0), ctx.splat(1), Integer.parseInt(ctx.splat(2)));
                ctx.json(log);
            }catch (BadLogin e){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //approve account
        //(username, password, accountID)
        app.put("/admin/accounts/*/*/*",ctx -> {
            try{
                logic.approveAccount(ctx.splat(0), ctx.splat(1), Integer.parseInt(ctx.splat(2)));
            }catch (BadLogin e){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //deny account
        //(username, password, accountID)
        app.delete("/admin/accounts/*/*/*",ctx -> {
            try{
                logic.denyAccount(ctx.splat(0), ctx.splat(1), Integer.parseInt(ctx.splat(2)));
            }catch (BadLogin e){
                log.warn("attempted an action with invalid login credentials");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "Bad login"));
            }catch (NumberFormatException e){
                log.warn("made http call with invalid formatting");
                ctx.status(400);
                ctx.json(new ErrorResponse(new NoError(), "invalid formatting"));
            }catch (BusinessException e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "SQL Error"));
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });

        //seed the database
        app.post("/seed",ctx -> {
            try{
                seedData.reset();
                ctx.status(200);
            }catch (Exception e){
                log.error(e);
                ctx.status(500);
                ctx.json(new ErrorResponse(e, "Server Error"));
            }
        });
    }
}
