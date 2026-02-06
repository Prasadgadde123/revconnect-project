package com.revconnect.service;

import com.revconnect.dao.ConnectionDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.model.Connection;
import com.revconnect.model.User;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ConsoleColors;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConnectionService {
    private ConnectionDAO connectionDAO;
    private UserDAO userDAO;
    private NotificationService notificationService;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;

    public ConnectionService() {
        this.connectionDAO = new ConnectionDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        AppLogger.info("ConnectionService initialized");
    }

    public void sendConnectionRequest(User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "SEND CONNECTION REQUEST" + ConsoleColors.RESET);

        System.out.print("Enter username to connect with: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println(ConsoleColors.RED + "❌ Username cannot be empty." + ConsoleColors.RESET);
            return;
        }

        User targetUser = userDAO.getUserByUsernameOrEmail(username);
        if (targetUser == null) {
            System.out.println(ConsoleColors.RED + "❌ User not found." + ConsoleColors.RESET);
            return;
        }

        if (targetUser.getUserId() == currentUser.getUserId()) {
            System.out.println(ConsoleColors.YELLOW + "⚠️ You cannot send a connection request to yourself." + ConsoleColors.RESET);
            return;
        }

        if (targetUser.getUserType() != User.UserType.PERSONAL) {
            System.out.println("You can only send connection requests to personal users. Try following business/creator accounts instead.");
            return;
        }

        if (connectionDAO.areUsersConnected(currentUser.getUserId(), targetUser.getUserId())) {
            System.out.println("You are already connected with this user.");
            return;
        }

        System.out.print("Are you sure you want to send connection request to " +
                ConsoleColors.YELLOW + targetUser.getFullName() + ConsoleColors.RESET + "? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes")) {
            System.out.println("Connection request cancelled.");
            return;
        }

        boolean success = connectionDAO.sendConnectionRequest(
                currentUser.getUserId(), targetUser.getUserId(), currentUser.getUserId());

        if (success) {
            System.out.println(ConsoleColors.GREEN + "✅ Connection request sent to " +
                    targetUser.getFullName() + "!" + ConsoleColors.RESET);

            // Send notification
            notificationService.sendConnectionRequestNotification(
                    targetUser.getUserId(), currentUser.getUserId());
        } else {
            System.out.println(ConsoleColors.RED + "❌ Failed to send connection request." + ConsoleColors.RESET);
        }
    }

    public void viewPendingConnections(User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "PENDING CONNECTION REQUESTS" + ConsoleColors.RESET);

        List<Connection> pendingConnections = connectionDAO.getPendingConnectionsForUser(currentUser.getUserId());

        if (pendingConnections.isEmpty()) {
            System.out.println("No pending connection requests.");
        } else {
            System.out.println(ConsoleColors.GREEN + "You have " + pendingConnections.size() +
                    " pending connection request(s):" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            for (int i = 0; i < pendingConnections.size(); i++) {
                Connection connection = pendingConnections.get(i);

                // Determine which user is the other user (not current user)
                String otherUserFullName;
                String otherUserUsername;
                if (connection.getUserId1() == currentUser.getUserId()) {
                    otherUserFullName = connection.getUser2FullName();
                    otherUserUsername = connection.getUser2Username();
                } else {
                    otherUserFullName = connection.getUser1FullName();
                    otherUserUsername = connection.getUser1Username();
                }

                String requestDate = connection.getCreatedAt().format(dateFormatter);

                System.out.printf("%d. %s (@%s)%n",
                        i + 1,
                        ConsoleColors.YELLOW + otherUserFullName + ConsoleColors.RESET,
                        otherUserUsername);
                System.out.println("   Requested by: " + connection.getRequestedByUsername());
                System.out.println("   Date: " + requestDate);

                if (i < pendingConnections.size() - 1) {
                    System.out.println();
                }
            }

            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            System.out.print("\nEnter number to respond (0 to cancel): ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice > 0 && choice <= pendingConnections.size()) {
                    Connection connection = pendingConnections.get(choice - 1);

                    System.out.print("Accept or reject this connection request? (a/r): ");
                    String response = scanner.nextLine().trim().toLowerCase();

                    if (response.equals("a")) {
                        boolean success = connectionDAO.acceptConnectionRequest(connection.getConnectionId());
                        if (success) {
                            System.out.println(ConsoleColors.GREEN + "✅ Connection request accepted!" + ConsoleColors.RESET);

                            // Send notification to requester
                            notificationService.sendConnectionAcceptedNotification(
                                    connection.getRequestedBy(), currentUser.getUserId());
                        } else {
                            System.out.println(ConsoleColors.RED + "❌ Failed to accept connection request." + ConsoleColors.RESET);
                        }
                    } else if (response.equals("r")) {
                        boolean success = connectionDAO.rejectConnectionRequest(connection.getConnectionId());
                        if (success) {
                            System.out.println(ConsoleColors.YELLOW + "⚠️ Connection request rejected." + ConsoleColors.RESET);
                        } else {
                            System.out.println(ConsoleColors.RED + "❌ Failed to reject connection request." + ConsoleColors.RESET);
                        }
                    } else {
                        System.out.println("Invalid response.");
                    }
                } else if (choice != 0) {
                    System.out.println("Invalid selection.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void viewSentRequests(User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "SENT CONNECTION REQUESTS" + ConsoleColors.RESET);

        List<Connection> sentRequests = connectionDAO.getSentPendingRequests(currentUser.getUserId());

        if (sentRequests.isEmpty()) {
            System.out.println("You have no pending sent requests.");
        } else {
            System.out.println(ConsoleColors.GREEN + "You have " + sentRequests.size() +
                    " pending sent request(s):" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            for (int i = 0; i < sentRequests.size(); i++) {
                Connection connection = sentRequests.get(i);

                // Determine which user is the other user (not current user)
                String otherUserFullName;
                String otherUserUsername;
                if (connection.getUserId1() == currentUser.getUserId()) {
                    otherUserFullName = connection.getUser2FullName();
                    otherUserUsername = connection.getUser2Username();
                } else {
                    otherUserFullName = connection.getUser1FullName();
                    otherUserUsername = connection.getUser1Username();
                }

                String requestDate = connection.getCreatedAt().format(dateFormatter);

                System.out.printf("%d. Sent to %s (@%s)%n",
                        i + 1,
                        ConsoleColors.YELLOW + otherUserFullName + ConsoleColors.RESET,
                        otherUserUsername);
                System.out.println("   Date: " + requestDate);
                System.out.println("   Status: " + ConsoleColors.YELLOW + "Pending" + ConsoleColors.RESET);

                if (i < sentRequests.size() - 1) {
                    System.out.println();
                }
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void viewConnections(User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "MY CONNECTIONS" + ConsoleColors.RESET);

        List<Connection> connections = connectionDAO.getConnectionsForUser(currentUser.getUserId());

        if (connections.isEmpty()) {
            System.out.println("You have no connections yet.");
        } else {
            System.out.println(ConsoleColors.GREEN + "You have " + connections.size() +
                    " connection(s):" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            for (int i = 0; i < connections.size(); i++) {
                Connection connection = connections.get(i);

                // Determine which user is the other user (not current user)
                String otherUserFullName;
                String otherUserUsername;
                if (connection.getUserId1() == currentUser.getUserId()) {
                    otherUserFullName = connection.getUser2FullName();
                    otherUserUsername = connection.getUser2Username();
                } else {
                    otherUserFullName = connection.getUser1FullName();
                    otherUserUsername = connection.getUser1Username();
                }

                String connectedDate = connection.getUpdatedAt().format(dateFormatter);

                System.out.printf("%d. %s (@%s)%n",
                        i + 1,
                        ConsoleColors.YELLOW + otherUserFullName + ConsoleColors.RESET,
                        otherUserUsername);
                System.out.println("   Connected since: " + connectedDate);

                if (i < connections.size() - 1) {
                    System.out.println();
                }
            }

            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            System.out.print("\nEnter number to remove connection (0 to cancel): ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice > 0 && choice <= connections.size()) {
                    Connection connection = connections.get(choice - 1);

                    System.out.print("Are you sure you want to remove connection with " +
                            ConsoleColors.YELLOW + connection.getUser2FullName() + ConsoleColors.RESET +
                            "? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();

                    if (confirm.equals("yes")) {
                        boolean success = connectionDAO.removeConnection(connection.getConnectionId());
                        if (success) {
                            System.out.println(ConsoleColors.GREEN + "✅ Connection removed successfully!" + ConsoleColors.RESET);
                        } else {
                            System.out.println(ConsoleColors.RED + "❌ Failed to remove connection." + ConsoleColors.RESET);
                        }
                    } else {
                        System.out.println("Connection removal cancelled.");
                    }
                } else if (choice != 0) {
                    System.out.println("Invalid selection.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void getConnectionStats(User user) {
        List<Connection> connections = connectionDAO.getConnectionsForUser(user.getUserId());
        List<Connection> pendingRequests = connectionDAO.getPendingConnectionsForUser(user.getUserId());
        List<Connection> sentRequests = connectionDAO.getSentPendingRequests(user.getUserId());

        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "CONNECTION STATS" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

        System.out.println("Total Connections: " + ConsoleColors.GREEN + connections.size() + ConsoleColors.RESET);
        System.out.println("Pending Requests: " + ConsoleColors.YELLOW + pendingRequests.size() + ConsoleColors.RESET);
        System.out.println("Sent Requests: " + ConsoleColors.YELLOW + sentRequests.size() + ConsoleColors.RESET);

        if (!connections.isEmpty()) {
            System.out.println("\nRecent connections:");
            for (int i = 0; i < Math.min(3, connections.size()); i++) {
                Connection connection = connections.get(i);
                String otherUser = connection.getUserId1() == user.getUserId() ?
                        connection.getUser2FullName() : connection.getUser1FullName();
                System.out.println("  • " + otherUser);
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}