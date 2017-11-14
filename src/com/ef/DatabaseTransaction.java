package com.ef;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class DatabaseTransaction {

    private static final String USER = "parser";
    private static final String PASS = "1234";
    private static final String DATABASE="parser";
    private static final String SERVER="localhost";
    private static final String DAILY = "daily";
    private static final String HOURLY = "hourly";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    MysqlDataSource dataSource = new MysqlDataSource();

    public void connectDb(MysqlDataSource dataSource, String query) throws SQLException {
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);
        dataSource.setDatabaseName(DATABASE);
        dataSource.setServerName(SERVER);

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate(query);

        stmt.close();
        conn.close();
    }

    public void createDb() throws SQLException, IOException {

        connectDb(dataSource, "DROP TABLE IF EXISTS `logs`;");
        connectDb(dataSource, "CREATE TABLE logs (id int NOT NULL AUTO_INCREMENT, date TIMESTAMP,ip varchar(255),request varchar(255),status varchar(255),user_agent varchar(255),PRIMARY KEY (id));");

    }

    public void loadLog(String logFilePath) throws SQLException {
        URL url = getClass().getResource("access.log");
        String windowsPath = url.getPath().substring(1).replaceAll("/","\\\\\\\\\\\\\\\\");

        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("LOAD DATA LOCAL INFILE "+windowsPath+" INTO TABLE logs\n" +
                " FIELDS TERMINATED BY '|' (date, ip, request, status, user_agent) SET ID = NULL;");
        statement.execute();

    }

    public void getRecord(String startDate, String duration, String threshold) throws SQLException {

        LocalDateTime beginDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDateTime endDate = null;
        if (duration.equals(DAILY)) {
            endDate = beginDate.plusHours(23).plusMinutes(59).plusSeconds(59);
        } else
            endDate = beginDate.plusMinutes(59).plusSeconds(59);
        int limit = Integer.valueOf(threshold);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        String beginDateParam = beginDate.format(formatter);
        String endDateParam = endDate.format(formatter);

        String query = "SELECT ip,COUNT(*) count FROM logs where date BETWEEN " .concat("'") + beginDateParam.concat("'") + " AND " .concat("'") + endDateParam.concat("'") + "GROUP BY ip HAVING COUNT(*)>=" + limit;

        System.out.println(query);

        ResultSet resultSet = statement.executeQuery(query);
        List<String>ipList = new ArrayList<>();
        while(resultSet.next()){
            ipList.add(resultSet.getString("ip"));
        }
        System.out.print(ipList);
        statement.close();
    }
}


