package com.revconnect.service;

import com.revconnect.dao.UserDAO;
import com.revconnect.model.User;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ConsoleColors;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private UserDAO userDAO;
    private Scanner scanner;

    public UserService() {
        this.userDAO = new UserDAO();
        this.scanner = new Scanner(System.in);
        AppLogger.info("UserService initialized");
    }

    public void viewProfile(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "PROFILE" + ConsoleColors.RESET);
        System.out.println("Username: " + user.getUsername());
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Account Type: " + user.getUserType());
        System.out.println("Bio: " + (user.getBio() != null ? user.getBio() : "Not set"));
        System.out.println("Location: " + (user.getLocation() != null ? user.getLocation() : "Not set"));
        System.out.println("Website: " + (user.getWebsite() != null ? user.getWebsite() : "Not set"));
        System.out.println("Profile Privacy: " + (user.isPrivate() ? "Private" : "Public"));
        if (user.getCreatedAt() != null) {
            System.out.println("Member Since: " + user.getCreatedAt().toLocalDate());
        }
    }

    public void editProfile(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "EDIT PROFILE" + ConsoleColors.RESET);

        System.out.println("Current Bio: " + (user.getBio() != null ? user.getBio() : "Not set"));
        System.out.print("Enter new bio (press Enter to keep current): ");
        String newBio = scanner.nextLine();
        if (!newBio.trim().isEmpty()) {
            user.setBio(newBio);
        }

        System.out.println("Current Location: " + (user.getLocation() != null ? user.getLocation() : "Not set"));
        System.out.print("Enter new location (press Enter to keep current): ");
        String newLocation = scanner.nextLine();
        if (!newLocation.trim().isEmpty()) {
            user.setLocation(newLocation);
        }

        System.out.println("Current Website: " + (user.getWebsite() != null ? user.getWebsite() : "Not set"));
        System.out.print("Enter new website (press Enter to keep current): ");
        String newWebsite = scanner.nextLine();
        if (!newWebsite.trim().isEmpty()) {
            user.setWebsite(newWebsite);
        }

        System.out.print("Make profile private? (yes/no): ");
        String privateChoice = scanner.nextLine();
        user.setPrivate(privateChoice.equalsIgnoreCase("yes"));

        boolean success = userDAO.updateUser(user);
        if (success) {
            System.out.println(ConsoleColors.GREEN + "✅ Profile updated successfully!" + ConsoleColors.RESET);
        } else {
            System.out.println("Failed to update profile.");
        }
    }

    public void searchUsers() {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "SEARCH USERS" + ConsoleColors.RESET);
        System.out.print("Enter search term (name or username): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            System.out.println("Please enter a search term.");
            return;
        }

        List<User> users = userDAO.searchUsers(searchTerm);

        if (users.isEmpty()) {
            System.out.println("No users found matching '" + searchTerm + "'");
        } else {
            System.out.println("\nFound " + users.size() + " users:");
            System.out.println("┌──┬──────────────────┬─────────────────┬────────────┐");
            System.out.println("│# │ Username         │ Full Name       │ Type       │");
            System.out.println("├──┼──────────────────┼─────────────────┼────────────┤");

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                System.out.printf("│%-2d│ %-16s │ %-15s │ %-10s │\n",
                        i + 1,
                        user.getUsername(),
                        user.getFullName(),
                        user.getUserType());
            }
            System.out.println("└──┴──────────────────┴─────────────────┴────────────┘");
        }
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsernameOrEmail(username);
    }
}