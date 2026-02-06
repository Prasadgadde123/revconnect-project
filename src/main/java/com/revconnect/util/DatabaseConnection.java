package com.revconnect.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private static final String URL = "jdbc:mysql://localhost:3306/revconnect";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Prasad1234@";

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Failed to load MySQL JDBC Driver: " + e.getMessage());
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connection established successfully");
            } catch (SQLException e) {
                System.err.println("❌ Failed to establish database connection: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to establish database connection", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✅ Database connection closed successfully");
            } catch (SQLException e) {
                System.err.println("❌ Failed to close database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Test method to verify connection
    public static boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("✅ Database connection test successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}