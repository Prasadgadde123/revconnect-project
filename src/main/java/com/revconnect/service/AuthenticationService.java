package com.revconnect.service;

import com.revconnect.dao.UserDAO;
import com.revconnect.model.*;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ValidationUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class AuthenticationService {
    private UserDAO userDAO;
    private Scanner scanner;
    private User currentUser;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.scanner = new Scanner(System.in);
        AppLogger.info("AuthenticationService initialized");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean register() {
        AppLogger.info("Starting registration process");
        System.out.println("\n=== REGISTER ===");

        try {
            System.out.print("Choose account type (1: Personal, 2: Business, 3: Creator): ");
            int accountType = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            if (!ValidationUtil.isValidUsername(username)) {
                System.out.println("Invalid username! Username must be 3-50 characters and contain only letters, numbers, and underscores.");
                return false;
            }

            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            if (!ValidationUtil.isValidEmail(email)) {
                System.out.println("Invalid email format!");
                return false;
            }

            System.out.print("Enter password (min 6 characters): ");
            String password = scanner.nextLine();

            if (!ValidationUtil.isValidPassword(password)) {
                System.out.println("Password must be at least 6 characters!");
                return false;
            }

            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match!");
                return false;
            }

            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine().trim();

            User user;
            switch (accountType) {
                case 1:
                    user = new PersonalUser(username, email, hashPassword(password), fullName);
                    break;
                case 2:
                    user = new BusinessUser(username, email, hashPassword(password), fullName);
                    System.out.print("Enter business category: ");
                    ((BusinessUser) user).setCategory(scanner.nextLine());
                    System.out.print("Enter industry: ");
                    ((BusinessUser) user).setIndustry(scanner.nextLine());
                    break;
                case 3:
                    user = new CreatorUser(username, email, hashPassword(password), fullName);
                    System.out.print("Enter creator category: ");
                    ((CreatorUser) user).setCategory(scanner.nextLine());
                    break;
                default:
                    System.out.println("Invalid account type!");
                    return false;
            }

            // Check if username or email already exists
            User existingUser = userDAO.getUserByUsernameOrEmail(username);
            if (existingUser != null) {
                System.out.println("Username already exists!");
                return false;
            }

            existingUser = userDAO.getUserByUsernameOrEmail(email);
            if (existingUser != null) {
                System.out.println("Email already registered!");
                return false;
            }

            boolean success = userDAO.createUser(user);
            if (success) {
                AppLogger.info("User registered successfully: " + username);
                System.out.println("Registration successful! Please login.");
                return true;
            } else {
                System.out.println("Registration failed. Please try again.");
                return false;
            }

        } catch (Exception e) {
            AppLogger.error("Error during registration", e);
            System.out.println("An error occurred during registration. Please try again.");
            return false;
        }
    }

    public boolean login() {
        AppLogger.info("Starting login process");
        System.out.println("\n=== LOGIN ===");

        try {
            System.out.print("Enter username or email: ");
            String identifier = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User user = userDAO.getUserByUsernameOrEmail(identifier);
            if (user == null) {
                System.out.println("User not found!");
                return false;
            }

            if (!user.getPasswordHash().equals(hashPassword(password))) {
                System.out.println("Invalid password!");
                return false;
            }

            currentUser = user;
            AppLogger.info("User logged in successfully: " + user.getUsername());
            System.out.println("Login successful! Welcome, " + user.getFullName() + "!");
            return true;

        } catch (Exception e) {
            AppLogger.error("Error during login", e);
            System.out.println("An error occurred during login. Please try again.");
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            AppLogger.info("User logged out: " + currentUser.getUsername());
        }
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    public boolean changePassword() {
        if (currentUser == null) {
            System.out.println("You must be logged in to change password.");
            return false;
        }

        AppLogger.info("Starting password change for user: " + currentUser.getUsername());
        System.out.println("\n=== CHANGE PASSWORD ===");

        try {
            System.out.print("Enter current password: ");
            String currentPassword = scanner.nextLine();

            if (!currentUser.getPasswordHash().equals(hashPassword(currentPassword))) {
                System.out.println("Current password is incorrect!");
                return false;
            }

            System.out.print("Enter new password (min 6 characters): ");
            String newPassword = scanner.nextLine();

            if (!ValidationUtil.isValidPassword(newPassword)) {
                System.out.println("Password must be at least 6 characters!");
                return false;
            }

            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match!");
                return false;
            }

            boolean success = userDAO.changePassword(currentUser.getUserId(), hashPassword(newPassword));
            if (success) {
                currentUser.setPasswordHash(hashPassword(newPassword));
                AppLogger.info("Password changed successfully for user: " + currentUser.getUsername());
                System.out.println("Password changed successfully!");
                return true;
            } else {
                System.out.println("Failed to change password. Please try again.");
                return false;
            }
        } catch (Exception e) {
            AppLogger.error("Error changing password", e);
            System.out.println("An error occurred while changing password. Please try again.");
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            AppLogger.error("Error hashing password", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }
}