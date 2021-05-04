package PZero.Libs;

import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Interfaces.IBusinessLogic;
import Interfaces.IUserFront;
import Models.account;
import Models.pendingTransaction;
import Models.transaction;
import Models.user;
import io.javalin.Javalin;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class UserFront implements IUserFront {

    private static final Logger log=Logger.getLogger(UserFront.class);

    private user loggedUser;
    private String userState = "none";
    private String adminPassword = "";
    private final String doubleRegex = "^(?:0|[1-9]\\d{0,2}(?:,?\\d{3})*)(?:\\.\\d+)?$";
    private final String adminUsername = "admin";
    private final IBusinessLogic logic;

    private Javalin app;

    public UserFront(IBusinessLogic logic){
        app = Javalin.create(javalinConfig -> {
            javalinConfig.enableCorsForAllOrigins();
        }).start(8001);
        this.logic = logic;
    }

    @Override
    public void start() {
        //basic login
        app.get("/user/*/*",ctx -> {
            user user = logic.login(ctx.splat(0), ctx.splat(1));
            if(user == null){
                ctx.status(400);
            }else {
                ctx.json(user);
            }
        });

        //get all users accounts
        app.get("/accounts/*/*",ctx -> {
            ArrayList<account> ret = null;
            try{
                ret = logic.getUserAccounts(logic.login(ctx.splat(0), ctx.splat(1)));
            }
            catch (BadLogin e){
                ctx.status(400);
            }
            ctx.json(ret);
        });
    }
}
