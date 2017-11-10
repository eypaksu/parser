import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Parser {


    public static void main(String args[]) throws SQLException, IOException, ParseException {

        java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse("2012-07-10 14:58:00.000000");
        System.out.println(temp);

        DatabaseTransaction dbTransaction= new DatabaseTransaction();
        dbTransaction.createDb();
        dbTransaction.loadLogs("");

    }


}