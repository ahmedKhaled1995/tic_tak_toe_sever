package com.iti.project;

import java.sql.*;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:db\\database.db";
    private static Connection CONNECTION;

    public static Connection getConnection(){
        if(CONNECTION == null){
            try {
                CONNECTION = DriverManager.getConnection(DATABASE_URL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return CONNECTION;
    }

    public static void closeConnection(){
        try {
            CONNECTION.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}