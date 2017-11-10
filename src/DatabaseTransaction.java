import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class DatabaseTransaction {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    static final String USER = "parser";
    static final String PASS = "1234";

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Connection conn = null;
    Statement stmt = null;
    MysqlDataSource dataSource = new MysqlDataSource();

    public void coonectDb(MysqlDataSource dataSource, String query) throws SQLException {
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);
        dataSource.setDatabaseName("parser");
        dataSource.setServerName("localhost");

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate(query);

        stmt.close();
        conn.close();
    }

    public void createDb() throws SQLException, IOException {

        coonectDb(dataSource, "DROP TABLE IF EXISTS `Log`;");
        coonectDb(dataSource, "CREATE TABLE Log (logId int,date DATE,ip varchar(255),request varchar(255),status varchar(255),user_agent varchar(255));");
        loadLogs("D:\\Users\\212603218\\Desktop\\access.log");

        System.out.print("DB closed");
    }

    public void loadLogs(String logFilePath) throws SQLException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            br = new BufferedReader(new FileReader(logFilePath));
            List<ParserModel> logs = new ArrayList<ParserModel>();
            String line = "";

            while ((line = br.readLine()) != null) {
                line = line.replace("|", ",");
                String[] data = line.split("\\s*,\\s*");
                ParserModel parserModel = new ParserModel();
                parserModel.setId(UUID.randomUUID().toString());
                Date date = formatter.parse(data[0]);
                parserModel.setDate(date);
                parserModel.setIp(data[1]);
                parserModel.setRequest(data[2]);
                parserModel.setStatus(data[3]);
                parserModel.setUserAgent(data[4]);

                logs.add(parserModel);
            }

            saveToDb(logs);

        } catch (
                IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveToDb(List<ParserModel> list) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Logs (id, date, ip,request,status,user_agent, ) VALUES (?, ?, ?, ?, ?; ?)");
        ) {
            int i = 0;
            for (ParserModel parserModel : list) {
                statement.setString(1, parserModel.getId());
                statement.setString(2, String.valueOf(parserModel.getDate()));
                statement.setString(3, parserModel.getIp());
                statement.setString(4, parserModel.getRequest());
                statement.setString(5, parserModel.getStatus());
                statement.setString(6, parserModel.getUserAgent());

                statement.addBatch();
                i++;

                if (i % 1000 == 0 || i == list.size()) {
                    statement.executeBatch(); // Execute every 1000 items.
                }
            }
        }
    }
}


