package com.artvideo.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://art-video.c76oggwkiypw.us-east-1.rds.amazonaws.com:3306/art-video";
    private static final String USER = "admin";
    private static final String PASSWORD = "Trabalhando.120";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
