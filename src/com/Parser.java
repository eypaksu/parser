package com;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Parser {


    public static void main(String args[]) throws SQLException, IOException, ParseException {

        java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse("2012-07-10 14:58:00.000000");
        System.out.println(temp);

        DatabaseTransaction dbTransaction= new DatabaseTransaction();
        dbTransaction.createDb();
        dbTransaction.loadLog("D:\\Users\\212603218\\Desktop\\access.log");
        String startDate="2017-01-01.15:00:00";
        String duration="hourly";
        String threshold="250";
        dbTransaction.getRecord(startDate, duration, threshold);

    }


}