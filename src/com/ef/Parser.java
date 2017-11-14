package com.ef;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class Parser {

    public static void main(String args[]) throws SQLException, IOException, ParseException {

        DatabaseTransaction dbTransaction= new DatabaseTransaction();
        dbTransaction.createDb();

        dbTransaction.loadLog("access.log");
        String startDate=args[0];
        String duration=args[1];
        String threshold=args[2];
        dbTransaction.getRecord(startDate, duration, threshold);

    }


}