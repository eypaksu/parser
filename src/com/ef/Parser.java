package com.ef;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DAILY = "daily";
    private static final String LOG_FILE = "src/com/resource/access.log";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    Database database = new Database();

    public static void main(String args[]) throws SQLException, IOException, ParseException {

        String startDate="2017-01-01 00:00:00";
        String duration="hourly";
        String threshold="200";

        Parser parser = new Parser();
        parser.createDb();
        parser.loadLog();
        parser.findAndPrintLogs(startDate, duration, threshold);
    }

    public void createDb() throws SQLException {

        database.executeUpdate("DROP TABLE IF EXISTS `logs`;");
        database.executeUpdate("CREATE TABLE logs (id int NOT NULL AUTO_INCREMENT, date TIMESTAMP,ip varchar(255),request varchar(255),status varchar(255),user_agent varchar(255),PRIMARY KEY (id));");

    }

    public void loadLog() throws SQLException {


        database.executeUpdate("LOAD DATA LOCAL INFILE '"+LOG_FILE+"' INTO TABLE logs" +
                " FIELDS TERMINATED BY '|' (date, ip, request, status, user_agent) SET ID = NULL;");

    }

    public void findAndPrintLogs(String startDate, String duration, String threshold) throws SQLException {

        String query = buildQuery(startDate, duration, Integer.valueOf(threshold));
        System.out.println(query);

        ResultSet resultSet = database.executeQuery(query);


        List<String> ipList = new ArrayList<>();
        while (resultSet.next()) {
            ipList.add(resultSet.getString("ip"));
        }
        database.closeStatement(resultSet.getStatement());
        System.out.print(ipList);
    }

    public String buildQuery(String startDate, String duration, int threshold) {
        LocalDateTime beginDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        LocalDateTime endDate;
        if (duration.equals(DAILY)) {
            endDate = beginDate.plusHours(23).plusMinutes(59).plusSeconds(59);
        } else {
            endDate = beginDate.plusMinutes(59).plusSeconds(59);
        }

        String beginDateParam = beginDate.format(formatter);
        String endDateParam = endDate.format(formatter);

        String query = "SELECT ip, COUNT(*) count FROM logs WHERE date BETWEEN '"+beginDateParam+"' and '"+endDateParam+"' GROUP BY ip HAVING COUNT(*) >="+threshold;

        return query;
    }



}