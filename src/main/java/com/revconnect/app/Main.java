package com.revconnect.app;

import com.revconnect.model.User;
import com.revconnect.service.*;
import com.revconnect.util.ConsoleColors;
import com.revconnect.util.DatabaseConnection;
import com.revconnect.util.AppLogger;
import java.util.Scanner;

public class Main {
    private static AuthenticationService authService;
    private static UserService userService;
    private static PostService postService;
    private static ConnectionService connectionService;
    private static FollowService followService;
    private static NotificationService notificationService;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            initializeServices();
            displayWelcomeBanner();

            boolean running = true;
            while (running) {
                if (authService.getCurrentUser() == null) {
                    running = showGuestMenu();
                } else {
                    running = showUserMenu();
                }
            }

            DatabaseConnection.closeConnection();
            System.out.println("\nThank you for using RevConnect!");

        } catch (Exception e) {
            AppLogger.error("Application error", e);
            System.out.println("\nAn error occurred. Please check the logs.");
            e.printStackTrace();
        }
    }

    private static void initializeServices() {
        AppLogger.info("Initializing services...");
        authService = new AuthenticationService();
        userService = new UserService();
        postService = new PostService();
        connectionService = new ConnectionService();
        followService = new FollowService();
        notificationService = new NotificationService();
        scanner = new Scanner(System.in);
    }

    private static void displayWelcomeBanner() {
        System.out.println(ConsoleColors.CYAN_BOLD);
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                     " + ConsoleColors.YELLOW_BOLD + "RevConnect" + ConsoleColors.CYAN_BOLD + "                        ║");
        System.out.println("║         Social Media Platform v1.0                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println(ConsoleColors.RESET);
    }

    private static boolean showGuestMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    authService.register();
                    break;
                case 2:
                    authService.login();
                    break;
                case 3:
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }

        return true;
    }

    private static boolean showUserMenu() {
        User currentUser = authService.getCurrentUser();
        int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());

        System.out.println("\n" + ConsoleColors.GREEN + "=".repeat(60) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "Welcome, " + currentUser.getFullName() + "!" + ConsoleColors.RESET);

        if (unreadCount > 0) {
            System.out.println(ConsoleColors.RED_BOLD + "📬 You have " + unreadCount + " unread notifications" + ConsoleColors.RESET);
        }

        System.out.println("\n=== DASHBOARD ===");
        System.out.println("1. View Feed");
        System.out.println("2. Create Post");
        System.out.println("3. My Profile");
        System.out.println("4. Notifications (" + unreadCount + ")");
        System.out.println("5. Connections");
        System.out.println("6. Follow System");
        System.out.println("7. Search");
        System.out.println("8. Trending");
        System.out.println("9. Account Settings");
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewFeed(currentUser);
                    break;
                case 2:
                    createPost(currentUser);
                    break;
                case 3:
                    myProfile(currentUser);
                    break;
                case 4:
                    notifications(currentUser);
                    break;
                case 5:
                    connectionsMenu(currentUser);
                    break;
                case 6:
                    followMenu(currentUser);
                    break;
                case 7:
                    searchMenu(currentUser);
                    break;
                case 8:
                    trendingMenu();
                    break;
                case 9:
                    accountSettings(currentUser);
                    break;
                case 0:
                    authService.logout();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }

        return true;
    }

    private static void viewFeed(User user) {
        postService.viewFeed(user);

        System.out.print("\nEnter post ID to interact (0 to go back): ");
        try {
            int postId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (postId > 0) {
                postInteractionMenu(postId, user);
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    // FIXED: Now properly uses PostService
    private static void createPost(User user) {
        postService.createPost(user);
    }

    private static void myProfile(User user) {
        System.out.println("\n=== MY PROFILE ===");
        userService.viewProfile(user);

        System.out.println("\n1. Edit Profile");
        System.out.println("2. My Posts");
        System.out.println("3. Follow Stats");
        System.out.println("4. View Followers");
        System.out.println("5. View Following");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    userService.editProfile(user);
                    break;
                case 2:
                    myPostsMenu(user);
                    break;
                case 3:
                    followService.getFollowStats(user);
                    break;
                case 4:
                    followService.viewFollowers(user);
                    break;
                case 5:
                    followService.viewFollowing(user);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private static void myPostsMenu(User user) {
        System.out.println("\n=== MY POSTS ===");
        postService.viewMyPosts(user);

        System.out.println("\n1. Edit a Post");
        System.out.println("2. Delete a Post");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    postService.editMyPost(user);
                    break;
                case 2:
                    postService.deleteMyPost(user);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    // REMOVED: editMyPost method - now handled by PostService
    // REMOVED: deleteMyPost method - now handled by PostService

    private static void notifications(User user) {
        notificationService.viewNotifications(user.getUserId());
    }

    private static void connectionsMenu(User user) {
        System.out.println("\n=== CONNECTIONS ===");
        System.out.println("1. Send Connection Request");
        System.out.println("2. View Pending Requests");
        System.out.println("3. View My Connections");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    connectionService.sendConnectionRequest(user);
                    break;
                case 2:
                    connectionService.viewPendingConnections(user);
                    break;
                case 3:
                    connectionService.viewConnections(user);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private static void followMenu(User user) {
        System.out.println("\n=== FOLLOW SYSTEM ===");
        System.out.println("1. Follow User");
        System.out.println("2. Unfollow User");
        System.out.println("3. View Followers");
        System.out.println("4. View Following");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    followService.followUser(user);
                    break;
                case 2:
                    followService.unfollowUser(user);
                    break;
                case 3:
                    followService.viewFollowers(user);
                    break;
                case 4:
                    followService.viewFollowing(user);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private static void searchMenu(User user) {
        System.out.println("\n=== SEARCH ===");
        System.out.println("1. Search Users");
        System.out.println("2. Search Posts by Hashtag");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    userService.searchUsers();
                    break;
                case 2:
                    postService.searchPostsByHashtag();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    // REMOVED: searchPostsByHashtag method - now handled by PostService

    private static void trendingMenu() {
        System.out.println("\n=== TRENDING ===");
        postService.viewTrendingPosts();

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void accountSettings(User user) {
        System.out.println("\n=== ACCOUNT SETTINGS ===");
        System.out.println("1. Change Password");
        System.out.println("2. View Profile");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    authService.changePassword();
                    break;
                case 2:
                    userService.viewProfile(user);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private static void postInteractionMenu(int postId, User user) {
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\n=== POST ACTIONS ===");
            System.out.println("1. Like/Unlike");
            System.out.println("2. Comment");
            System.out.println("3. View Comments");
            System.out.println("0. Back to Feed");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        likeOnPost(postId, user);
                        break;
                    case 2:
                        commentOnPost(postId, user);
                        break;
                    case 3:
                        postService.viewComments(postId);
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }
    }

    // FIXED: Uses PostService's likePost method
    private static void likeOnPost(int postId, User user) {
        postService.likePost(postId, user);
    }

    // FIXED: Uses PostService's commentOnPost method
    private static void commentOnPost(int postId, User user) {
        postService.commentOnPost(postId, user);
    }
}