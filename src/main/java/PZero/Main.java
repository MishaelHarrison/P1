package PZero;

import PZero.Libs.BusinessLogic;
import PZero.Libs.DAO.BankData;
import PZero.Libs.DAO.postgresConnector;
import PZero.Libs.UserFront;

public class Main {
    public static void main(String[] args) {
        postgresConnector.beginConnection(
                "jdbc:postgresql://mishaelrevaturedb.ctfx5jeazmpz.us-east-2.rds.amazonaws.com:5432/postgres",
                "Mishael",
                "ToastToast"
        );
        new UserFront(System.in,
                new BusinessLogic(
                new BankData("p0")))
                .menu();
    }
}
