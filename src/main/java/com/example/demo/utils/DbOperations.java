package com.example.demo.utils;

import com.example.demo.models.Interview;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DbOperations {
    private static void createTable()
    {
        try{
            Connection conn = DbConnection.getInstance().getConnection();
            String createTableQuery = "CREATE TABLE interviews (" +
                              "id INT AUTO_INCREMENT PRIMARY KEY," +
                              "dateOfInterview VARCHAR(255) NOT NULL,"+
                              "team VARCHAR(255) NOT NULL,"+
                              "panelName VARCHAR(255) NOT NULL,"+
                              "round VARCHAR(255) NOT NULL,"+
                              "skill VARCHAR(255) NOT NULL,"+
                              "time VARCHAR(255) NOT NULL,"+
                              "candidateCurrentLoc VARCHAR(255) NOT NULL,"+
                              "preferredLocation VARCHAR(255) NOT NULL,"+
                              "candidateName VARCHAR(255) NOT NULL"+
                              ")";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableQuery);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static boolean tableExists(String tableName)
    {
        try(Connection conn = DbConnection.getInstance().getConnection()){
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null,tableName,new String[]{"TABLE"});
            return res.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public static void insertIntoDataBase(List<Interview> list)
    {
        if(tableExists("interviews"))
            return;

        createTable();
        AtomicInteger counter = new AtomicInteger(0);

        ExecutorService ex = Executors.newFixedThreadPool(100);


        for(Interview interview:list)
        {
            ex.submit(() ->{
                try(Connection conn = DbConnection.getInstance().getConnection()){
                String insertQuery = "INSERT INTO interviews (dateOfInterview, team, panelName, round, skill, time, candidateCurrentLoc, preferredLocation, candidateName) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

                preparedStatement.setString(1,interview.getDate());
                preparedStatement.setString(2,interview.getTeam());
                preparedStatement.setString(3,interview.getPanelName());
                preparedStatement.setString(4,interview.getRound());
                preparedStatement.setString(5,interview.getSkill());
                preparedStatement.setString(6,interview.getTime());
                preparedStatement.setString(7,interview.getCandidateCurrentLoc());
                preparedStatement.setString(8,interview.getPreferredLocation());
                preparedStatement.setString(9,interview.getCandidateName());
                preparedStatement.executeUpdate();

                if(counter.incrementAndGet()%100 == 0)
                    System.out.println("Processed "+counter.get()+" entries");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            });
        }

        ex.shutdown();

        try {
            if(!ex.awaitTermination(60, TimeUnit.SECONDS)){
                ex.shutdownNow();
            }
        } catch (InterruptedException e) {
            ex.shutdownNow();
        }
    }
}
