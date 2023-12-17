package com.example.demo.utils;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConnection {
    private static DbConnection instance;
    private BasicDataSource ds;

    private DbConnection() {
        ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/apangshudb");
        ds.setUsername(System.getenv("Username"));
        ds.setPassword(System.getenv("Password"));
        ds.setMaxTotal(100);
    }

    public static synchronized DbConnection getInstance(){
        if(instance == null){
            instance = new DbConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException{
        return ds.getConnection();
    }
}
