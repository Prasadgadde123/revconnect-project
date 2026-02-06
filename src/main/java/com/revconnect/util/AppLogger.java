package com.revconnect.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    // Keep your existing constants
    private static final String LOG_FILE = "app.log";
    private static final String ERROR_LOG_FILE = "error.log";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Add Log4j logger
    private static final Logger log4jLogger = LogManager.getLogger(AppLogger.class);

    // Keep your existing file logging methods
    private static void logToFile(String filename, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    // Enhanced logging methods - use both systems
    public static void info(String message) {
        String logMessage = String.format("%s [INFO] %s",
                LocalDateTime.now().format(dtf), message);

        // Keep existing console and file logging
        System.out.println(logMessage);
        logToFile(LOG_FILE, logMessage);

        // Add Log4j logging
        log4jLogger.info(message);
    }

    public static void error(String message) {
        String logMessage = String.format("%s [ERROR] %s",
                LocalDateTime.now().format(dtf), message);

        // Keep existing console and file logging
        System.err.println(logMessage);
        logToFile(ERROR_LOG_FILE, logMessage);
        logToFile(LOG_FILE, logMessage);

        // Add Log4j logging
        log4jLogger.error(message);
    }

    public static void error(String message, Exception e) {
        String logMessage = String.format("%s [ERROR] %s - %s",
                LocalDateTime.now().format(dtf), message, e.getMessage());

        // Keep existing console and file logging
        System.err.println(logMessage);
        logToFile(ERROR_LOG_FILE, logMessage);
        logToFile(LOG_FILE, logMessage);
        e.printStackTrace();

        // Add Log4j logging
        log4jLogger.error(message, e);
    }

    public static void debug(String message) {
        String logMessage = String.format("%s [DEBUG] %s",
                LocalDateTime.now().format(dtf), message);

        // Keep existing console and file logging
        System.out.println(logMessage);
        logToFile(LOG_FILE, logMessage);

        // Add Log4j logging
        log4jLogger.debug(message);
    }

    public static void warn(String message) {
        String logMessage = String.format("%s [WARN] %s",
                LocalDateTime.now().format(dtf), message);

        // Keep existing console and file logging
        System.out.println(logMessage);
        logToFile(LOG_FILE, logMessage);

        // Add Log4j logging
        log4jLogger.warn(message);
    }

    // New methods for Log4j-specific features
    public static void trace(String message) {
        String logMessage = String.format("%s [TRACE] %s",
                LocalDateTime.now().format(dtf), message);

        // Add to file
        logToFile(LOG_FILE, logMessage);

        // Log4j trace
        log4jLogger.trace(message);
    }

    public static void fatal(String message) {
        String logMessage = String.format("%s [FATAL] %s",
                LocalDateTime.now().format(dtf), message);

        System.err.println(logMessage);
        logToFile(ERROR_LOG_FILE, logMessage);
        logToFile(LOG_FILE, logMessage);

        log4jLogger.fatal(message);
    }

    // Utility methods
    public static void sql(String query) {
        String logMessage = String.format("%s [SQL] %s",
                LocalDateTime.now().format(dtf), query);

        logToFile("sql.log", logMessage);
        log4jLogger.debug("SQL Query: {}", query);
    }

    public static void userAction(String username, String action) {
        String logMessage = String.format("%s [USER] %s - %s",
                LocalDateTime.now().format(dtf), username, action);

        logToFile("user_activity.log", logMessage);
        log4jLogger.info("User Action - User: {}, Action: {}", username, action);
    }

    public static void dbOperation(String operation, String table, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        String logMessage = String.format("%s [DB] %s on %s: %s",
                LocalDateTime.now().format(dtf), operation, table, status);

        logToFile("database.log", logMessage);

        if (success) {
            log4jLogger.debug("DB Operation - {} on {}: SUCCESS", operation, table);
        } else {
            log4jLogger.error("DB Operation - {} on {}: FAILED", operation, table);
        }
    }

    // Get Log4j logger for specific class
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}