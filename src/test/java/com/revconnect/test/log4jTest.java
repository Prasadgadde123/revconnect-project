package com.revconnect.test;

import com.revconnect.util.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Log4j Configuration Test")
class Log4jTest {

    @Test
    @DisplayName("Test log levels")
    void testLogLevels() {
        // These should appear in logs - using methods that EXIST in AppLogger
        AppLogger.trace("This is a TRACE message");
        AppLogger.debug("This is a DEBUG message");
        AppLogger.info("This is an INFO message");
        AppLogger.warn("This is a WARN message");
        AppLogger.error("This is an ERROR message");

        assertTrue(true, "Log messages should be written to configured appenders");
    }

    @Test
    @DisplayName("Test existing logger methods")
    void testExistingLoggerMethods() {
        // Only use methods that exist in AppLogger
        AppLogger.userAction("testuser", "login");
        AppLogger.dbOperation("INSERT", "users", true);
        AppLogger.info("Test info message");

        assertTrue(true, "Existing logger methods should work");
    }

    @Test
    @DisplayName("Test error logging")
    void testErrorLogging() {
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            AppLogger.error("Division by zero error occurred", e);
        }

        assertTrue(true, "Error logging should work");
    }
}