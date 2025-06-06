/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class DatabaseConnection {
    private static final String HOST = EnvConfig.get("DB_HOST");
    private static final String PORT = EnvConfig.get("DB_PORT");
    private static final String DB_NAME = EnvConfig.get("DB_NAME");
    private static final String USER = EnvConfig.get("DB_USER");
    private static final String PASSWORD = EnvConfig.get("DB_PASS");

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}