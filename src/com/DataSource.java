package com;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;;

public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
//        config = new HikariConfig("datasource.properties");

//        Properties props = new Properties();
//        props.setProperty("dataSourceClassName", "org.h2.Driver");
//        props.setProperty("dataSource.user", "");
//        props.setProperty("dataSource.password", "");
//        props.put("dataSource.logWriter", new PrintWriter(System.out));
//        config = new HikariConfig(props);

        config.setJdbcUrl("jdbc:mysql://localhost:3306/parser");
        config.setUsername("parser");
        config.setPassword("1234");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

//        ds.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/db.sql'");
//        ds.setUsername("");
//        ds.setPassword("");
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}