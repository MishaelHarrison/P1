package PZero.Libs.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class postgresConnector {

    private postgresConnector(){}
    private static Connection connection;
    private static boolean set = false;

    public static void beginConnection(String url, String username, String password){
        try {
            Class.forName("org.postgresql.Driver");
            connection= DriverManager.getConnection(url,username,password);
            set = true;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    public static Connection getConnection(){
        return set ? connection : null;
    }

}
