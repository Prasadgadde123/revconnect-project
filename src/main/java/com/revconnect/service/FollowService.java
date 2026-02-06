package com.revconnect.service;

import com.revconnect.dao.FollowDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.model.Follow;
import com.revconnect.model.User;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ConsoleColors;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class FollowService {
    private FollowDAO followDAO;
    private UserDAO userDAO;
    private NotificationService notificationService;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;

    public FollowService() {
        this.followDAO = new FollowDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        AppLogger.info("FollowService initialized");
    }

    public void followUser(User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "FOLLOW USER" + ConsoleColors.RESET);

        System.out.print("Enter username to follow: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        User targetUser = userDAO.getUserByUsernameOrEmail(username);
        if (targetUser == null) {
            System.out.println(ConsoleColors.RED + "❌ User not found." + ConsoleColors.RESET);
            return;
        }

        if (targetUser.getUserId() == currentUser.getUserId()) {
            System.out.println(ConsoleColors.YELLOW + "⚠️ You cannot follow yourself." + ConsoleColors.RESET);
            return;
        }

        if (followDAO.isFollowing(currentUser.getUserId(), targetUser.getUserId())) {
            System.out.println("You are already following " +
                    ConsoleColors.YELLOW + targetUser.getFullName() + ConsoleColors.RESET + ".");
            return;
        }

        System.out.print("Are you sure you want to follow " +
                ConsoleColors.YELLOW + targetUser.getFullName() + ConsoleColors.RESET + "? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes")) {
            System.out.println("Follow cancelled.");
            return;
        }

        // FIXED: Changed method call to match FollowDAO
        boolean success = followDAO.followUser(currentUser.getUserId(), targetUser.getUserId());
        if (success) {
            System.out.println(ConsoleColors.GREEN + "✅ You are now following " +
                    targetUser.getFullName() + "!" + ConsoleColors.RESET);

            // Send notification
            notificationService.sendNewFollowerNotification(
                    targetUser.getUserId(), currentUser.getUserId());

            // Update follow stats
            getFollowStats(currentUser);
        } else {
            System.out.println(ConsoleColors.RED + "❌ Failed to follow user." + ConsoleColors.RESET);
        }
    }

    public void unfollowUser(User currentUser) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "UNFOLLOW USER" + ConsoleColors.RESET);

        List<Follow> following = followDAO.getFollowingForUser(currentUser.getUserId());
        if (following.isEmpty()) {
            System.out.println("You are not following anyone.");
            return;
        }

        System.out.println("Users you follow:");
        System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

        for (int i = 0; i < following.size(); i++) {
            Follow follow = following.get(i);
            System.out.printf("%d. %s (@%s)%n",
                    i + 1,
                    ConsoleColors.YELLOW + follow.getFollowingFullName() + ConsoleColors.RESET,
                    follow.getFollowingUsername());
        }

        System.out.print("\nEnter number to unfollow (0 to cancel): ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice > 0 && choice <= following.size()) {
                Follow follow = following.get(choice - 1);
                System.out.print("Are you sure you want to unfollow " +
                        ConsoleColors.YELLOW + follow.getFollowingFullName() +
                        ConsoleColors.RESET + "? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("yes")) {
                    boolean success = followDAO.unfollowUser(currentUser.getUserId(), follow.getFollowingId());
                    if (success) {
                        System.out.println(ConsoleColors.GREEN + "✅ Unfollowed " +
                                follow.getFollowingFullName() + "." + ConsoleColors.RESET);
                        // Update follow stats
                        getFollowStats(currentUser);
                    } else {
                        System.out.println(ConsoleColors.RED + "❌ Failed to unfollow user." + ConsoleColors.RESET);
                    }
                } else {
                    System.out.println("Unfollow cancelled.");
                }
            } else if (choice != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    public void viewFollowers(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "FOLLOWERS" + ConsoleColors.RESET);

        List<Follow> followers = followDAO.getFollowersForUser(user.getUserId());

        if (followers.isEmpty()) {
            System.out.println("You have no followers yet.");
        } else {
            System.out.println(ConsoleColors.GREEN + "You have " + followers.size() + " followers:" +
                    ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            for (int i = 0; i < followers.size(); i++) {
                Follow follower = followers.get(i);
                String followDate = follower.getCreatedAt().format(dateFormatter);

                // Check if mutual follow
                boolean isMutual = followDAO.isFollowing(user.getUserId(), follower.getFollowerId());
                String mutualText = isMutual ? ConsoleColors.BLUE + " (Mutual)" + ConsoleColors.RESET : "";

                System.out.printf("%d. %s (@%s)%s%n",
                        i + 1,
                        ConsoleColors.YELLOW + follower.getFollowerFullName() + ConsoleColors.RESET,
                        follower.getFollowerUsername(),
                        mutualText);
                System.out.println("   Followed since: " + followDate);

                // Show additional options
                if (i < followers.size() - 1) {
                    System.out.println();
                }
            }

            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void viewFollowing(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "FOLLOWING" + ConsoleColors.RESET);

        List<Follow> following = followDAO.getFollowingForUser(user.getUserId());

        if (following.isEmpty()) {
            System.out.println("You are not following anyone.");
        } else {
            System.out.println(ConsoleColors.GREEN + "You are following " + following.size() + " users:" +
                    ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            for (int i = 0; i < following.size(); i++) {
                Follow follow = following.get(i);
                String followDate = follow.getCreatedAt().format(dateFormatter);

                // Check if mutual follow
                boolean isMutual = followDAO.isFollowing(follow.getFollowingId(), user.getUserId());
                String mutualText = isMutual ? ConsoleColors.BLUE + " (Mutual)" + ConsoleColors.RESET : "";

                System.out.printf("%d. %s (@%s)%s%n",
                        i + 1,
                        ConsoleColors.YELLOW + follow.getFollowingFullName() + ConsoleColors.RESET,
                        follow.getFollowingUsername(),
                        mutualText);
                System.out.println("   Following since: " + followDate);

                // Show additional options
                if (i < following.size() - 1) {
                    System.out.println();
                }
            }

            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void getFollowStats(User user) {
        int followerCount = followDAO.getFollowerCount(user.getUserId());
        int followingCount = followDAO.getFollowingCount(user.getUserId());

        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "FOLLOW STATS" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

        System.out.println("Followers: " + ConsoleColors.GREEN + followerCount + ConsoleColors.RESET);
        System.out.println("Following: " + ConsoleColors.GREEN + followingCount + ConsoleColors.RESET);

        if (followerCount > 0) {
            List<Follow> recentFollowers = followDAO.getFollowersForUser(user.getUserId());
            if (recentFollowers.size() > 5) {
                System.out.println("\nRecent followers:");
                for (int i = 0; i < Math.min(5, recentFollowers.size()); i++) {
                    Follow follower = recentFollowers.get(i);
                    System.out.println("  • " + follower.getFollowerFullName() + " (@"
                            + follower.getFollowerUsername() + ")");
                }
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void viewMutualFollows(User user) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(50) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "MUTUAL FOLLOWS" + ConsoleColors.RESET);

        List<Follow> mutuals = followDAO.getMutualFollows(user.getUserId());

        if (mutuals.isEmpty()) {
            System.out.println("You have no mutual follows.");
        } else {
            System.out.println(ConsoleColors.GREEN + "You have " + mutuals.size() + " mutual follows:" +
                    ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(50) + ConsoleColors.RESET);

            for (Follow mutual : mutuals) {
                System.out.println("• " + ConsoleColors.YELLOW + mutual.getFollowingFullName() +
                        ConsoleColors.RESET + " (@" + mutual.getFollowingUsername() + ")");
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}