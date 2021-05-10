package PZero.Libs.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class seedData {
    private static final String query =
            "truncate p0.transactions, p0.accounts, p0.users, p0.employees restart identity;\n" +
            "\n" +
            "CREATE OR REPLACE FUNCTION randMon(low INT, high INT) \n" +
            "   RETURNS float AS\n" +
            "$$\n" +
            "BEGIN\n" +
            "   RETURN ((select floor(random() * 100 * (high - low) + 1)::float / 100) + low);\n" +
            "END;\n" +
            "$$ language 'plpgsql' STRICT;\n" +
            "\n" +
            "CREATE OR REPLACE FUNCTION randMon() \n" +
            "   RETURNS float AS\n" +
            "$$\n" +
            "BEGIN\n" +
            "   RETURN (select floor(random() * 10000 + 1)::float / 100);\n" +
            "END;\n" +
            "$$ language 'plpgsql' STRICT;\n" +
            "\n" +
            "INSERT INTO p0.users\n" +
            "(username, \"password\", fname, lname)\n" +
            "VALUES('mishH', 'passwrd', 'mishael', 'harrison'),\n" +
            "('pear', 'badphones', 'steve', 'jobs'),\n" +
            "('elongated', 'musketgun', 'elon', 'musk'),\n" +
            "('barrel', 'house', 'Diogenes', 'philosopher'),\n" +
            "('admin', 'apache', 'gabe', 'newell'),\n" +
            "('admin2', 'apache', 'morgan', 'freeman');\n" +
            "\n" +
            "insert into p0.employees (id) values (5),(6);\n" +
            "\n" +
            "INSERT INTO p0.accounts\n" +
            "(accounttype, balance, userid, approved, activatorid)\n" +
            "VALUES('checking', randMon(100, 90), 1, true, 5),\n" +
            "('saveings', randMon(10000, 9000), 1, true, 5),\n" +
            "('buisiness', randMon(2,0), 2, true, 6),\n" +
            "('personal', randMon(1000000,900000), 2, true, 6),\n" +
            "('worldDominationFunds', 2, 1, false, null),\n" +
            "('newPhoneFunds', 60, 2, false, null);\n" +
            "\n" +
            "INSERT INTO p0.transactions\n" +
            "(issuingid, recievingid, amount, \"timestamp\", approved)\n" +
            "values\n" +
            "(1, 4, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(2, 3, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(3, 2, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(4, 1, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(1, 3, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(2, 1, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(3, 4, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(4, 2, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(1, 2, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(2, 4, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(3, 1, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(4, 3, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(null, 1, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(null, 2, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(null, 3, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(null, 4, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(1, null, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(2, null, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(3, null, randMon(), CURRENT_TIMESTAMP, true),\n" +
            "(4, null, randMon(), CURRENT_TIMESTAMP, true);";

    public static void reset() throws SQLException {
        Connection connection = postgresConnector.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
    }
}
