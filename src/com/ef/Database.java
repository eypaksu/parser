package com.ef;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {

    private static final String USER = "parser";
    private static final String PASS = "1234";
    private static final String DATABASE = "parser";
    private static final String SERVER = "localhost";

    MysqlDataSource dataSource = new MysqlDataSource();
    private final Connection conn;

    public Database() {
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);
        dataSource.setDatabaseName(DATABASE);
        dataSource.setServerName(SERVER);

        try {
            this.conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    public int executeUpdate(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            return stmt.executeUpdate(query);
        } finally {
            stmt.close();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    public void closeStatement(Statement statement) throws SQLException {
        statement.close();
    }

}
