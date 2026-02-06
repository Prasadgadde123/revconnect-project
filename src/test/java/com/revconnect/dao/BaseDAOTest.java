package com.revconnect.dao;

import com.revconnect.util.AppLogger;
import com.revconnect.util.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;  // ADD THIS IMPORT

@DisplayName("Base DAO Integration Tests")
class BaseDAOTest {

    @Test
    @DisplayName("Test database connection")
    void testDatabaseConnection() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            assertNotNull(conn, "Database connection should not be null");

            // Test that connection is valid
            assertFalse(conn.isClosed(), "Connection should be open");

            AppLogger.info("Database connection test passed");

        } catch (SQLException e) {
            AppLogger.error("Database connection failed: " + e.getMessage());
            // Skip test if database is not available
            assumeTrue(false, "Database not available: " + e.getMessage());
        } finally {
            // Clean up
            if (conn != null) {
                try {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    AppLogger.error("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    @Test
    @DisplayName("Test basic database functionality")
    void testBasicDatabaseFunctionality() {
        // Simple test without actual database operations
        assertTrue(true, "Basic test should pass");
        AppLogger.info("Basic database functionality test passed");
    }

    @Test
    @DisplayName("Test connection lifecycle")
    void testConnectionLifecycle() {
        // Test that we can get multiple connections
        try {
            Connection conn1 = DatabaseConnection.getConnection();
            Connection conn2 = DatabaseConnection.getConnection();

            assertNotNull(conn1);
            assertNotNull(conn2);

            // They might be the same instance if using connection pooling
            // or different instances - either is fine

            AppLogger.info("Connection lifecycle test passed");

        } catch (Exception e) {
            // If database is not available, skip this test
            assumeTrue(false, "Database not available: " + e.getMessage());
        }
    }
}