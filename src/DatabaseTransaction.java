import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTransaction {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    static final String USER = "parser";
    static final String PASS = "1234";

    Connection conn = null;
    Statement stmt = null;


    public void createDb() throws SQLException {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setUser(USER);
        dataSource.setPassword(PASS);
        dataSource.setDatabaseName("parser");
        dataSource.setServerName("localhost");

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate("CREATE TABLE Log (\n" +
                "    logId int,\n" +
                "    Date DATE,\n" +
                "    Ip varchar(255), \n" +
                "    Request varchar(255),\n" +
                "    Status varchar(255), \n" +
                "    UserAgent varchar(255) \n" +
                "); ");

        System.out.print(rs);
//        rs.close();
//        stmt.close();
//        conn.close();

        System.out.print("DB closed");
    }

    public void loadLogs(String logFilePath){

    }



}
