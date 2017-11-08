import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.*;

public class Parser {


    public static void main(String args[]) throws SQLException {

        DatabaseTransaction dbTransaction= new DatabaseTransaction();
        dbTransaction.createDb();

    }


}