package com.revconnect.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Connection Tests")
class DatabaseConnectionTest {

    @Test
    @Order(1)
    @DisplayName("Test database connection")
    void testGetConnection() {
        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection, "Database connection should not be null");

        try {
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("Failed to check connection status: " + e.getMessage());
        }

        AppLogger.info("Database connection test passed");
    }

    @Test
    @Order(2)
    @DisplayName("Test connection reuse")
    void testConnectionReuse() {
        Connection connection1 = DatabaseConnection.getConnection();
        Connection connection2 = DatabaseConnection.getConnection();

        assertSame(connection1, connection2, "Should return the same connection instance");

        AppLogger.info("Connection reuse test passed");
    }

    @Test
    @Order(3)
    @DisplayName("Test close connection")
    void testCloseConnection() {
        // Get connection first
        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection);

        // Close connection
        DatabaseConnection.closeConnection();

        try {
            // Try to get connection again
            Connection newConnection = DatabaseConnection.getConnection();
            assertNotNull(newConnection, "Should create new connection after close");
            assertNotSame(connection, newConnection, "Should be different connection instance");
        } catch (Exception e) {
            fail("Failed to get new connection: " + e.getMessage());
        }

        AppLogger.info("Close connection test passed");
    }
}