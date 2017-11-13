package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DatabaseTransaction {

    private static final String USER = "parser";
    private static final String PASS = "1234";
    private static final String DATABASE="parser";
    private static final String SERVER="localhost";
    private static final String DAILY = "daily";
    private static final String HOURLY = "hourly";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public void connectDb(String query) throws SQLException {

        Connection conn = DataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate(query);

        stmt.close();
        conn.close();
    }

    public void createDb() throws SQLException, IOException {



        connectDb("DROP TABLE IF EXISTS `logs`;");
        connectDb( "CREATE TABLE logs (log_id varchar(36) NOT NULL PRIMARY KEY,date TIMESTAMP,ip varchar(255),request varchar(255),status varchar(255),user_agent varchar(255));");

    }

    public void loadLog(String logFilePath) throws SQLException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            br = new BufferedReader(new FileReader(logFilePath));
            List<ParserModel> logs = new ArrayList<>();
            String line = "";

            while ((line = br.readLine()) != null) {
                line = line.replace("|", ",");
                String[] data = line.split("\\s*,\\s*");
                ParserModel parserModel = new ParserModel();
                parserModel.setId(UUID.randomUUID().toString());
                LocalDateTime date = LocalDateTime.parse(data[0], DateTimeFormatter.ofPattern(DATE_FORMAT));
                parserModel.setDate(date);
                parserModel.setIp(data[1]);
                String request = data[2].substring(1);
                parserModel.setRequest(request.replaceAll(".$", ""));
                parserModel.setStatus(data[3]);
                String userAgent = data[4].substring(1);
                parserModel.setUserAgent(userAgent.replaceAll(".$", ""));
                logs.add(parserModel);
            }

            saveToDb(logs);

        } catch (
                IOException e) {
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
                Connection connection = DataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO logs (log_id, date, ip,request,status,user_agent) VALUES (?, ?, ?, ?, ?, ?)");
        ) {
            int i = 0;
            for (ParserModel parserModel : list) {
                statement.setString(1, parserModel.getId());
                statement.setTimestamp(2, Timestamp.valueOf(parserModel.getDate()));
                statement.setString(3, parserModel.getIp());
                statement.setString(4, parserModel.getRequest());
                statement.setString(5, parserModel.getStatus());
                statement.setString(6, parserModel.getUserAgent());

                statement.addBatch();
                i++;

                if (i % 10000 == 0 || i == list.size()) {
                    statement.executeLargeBatch(); // Execute every 1000 items.
                }

            }
        }
    }

    public void getRecord(String startDate, String duration, String threshold) throws SQLException {

        LocalDateTime beginDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDateTime endDate = null;
        if (duration.equals(DAILY)) {
            endDate = beginDate.plusDays(1);
        } else
            endDate = beginDate.plusHours(1);
        int limit = Integer.valueOf(threshold);

        Connection connection = DataSource.getConnection();
        Statement statement = connection.createStatement();

        String beginDateParam = beginDate.format(formatter);
        String endDateParam = endDate.format(formatter);

        String query = "SELECT ip,COUNT(*) count FROM logs where date BETWEEN " .concat("'") + beginDateParam.concat("'") + " AND " .concat("'") + endDateParam.concat("'") + "GROUP BY ip HAVING COUNT(*)>=" + limit;

        ResultSet resultSet = statement.executeQuery(query);
        List<String>ipList = new ArrayList<String >();
        while(resultSet.next()){
            ipList.add(resultSet.getString("ip"));
        }
        System.out.print(ipList);
        statement.close();
    }
}


