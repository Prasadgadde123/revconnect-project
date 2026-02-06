package com.revconnect.service;

import com.revconnect.dao.NotificationDAO;
import com.revconnect.model.Notification;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ConsoleColors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class NotificationService {
    private NotificationDAO notificationDAO;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter timeFormatter;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
        this.timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        AppLogger.info("NotificationService initialized");
    }

    public void viewNotifications(int userId) {
        System.out.println("\n" + ConsoleColors.CYAN + "=".repeat(60) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW_BOLD + "NOTIFICATIONS" + ConsoleColors.RESET);

        int unreadCount = notificationDAO.getUnreadNotificationCount(userId);
        if (unreadCount > 0) {
            System.out.println(ConsoleColors.RED_BOLD + "📬 You have " + unreadCount + " unread notification(s)" +
                    ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "-".repeat(60) + ConsoleColors.RESET);
        }

        List<Notification> notifications = notificationDAO.getNotificationsForUser(userId, 50);

        if (notifications.isEmpty()) {
            System.out.println("📭 No notifications.");
        } else {
            System.out.println("Showing " + notifications.size() + " most recent notifications:");
            System.out.println(ConsoleColors.CYAN + "-".repeat(60) + ConsoleColors.RESET);

            for (int i = 0; i < notifications.size(); i++) {
                Notification notification = notifications.get(i);
                displayNotification(notification, i + 1);

                if (i < notifications.size() - 1) {
                    System.out.println(ConsoleColors.DARK_GRAY + "─".repeat(60) + ConsoleColors.RESET);
                }
            }

            System.out.println(ConsoleColors.CYAN + "-".repeat(60) + ConsoleColors.RESET);

            System.out.println("\n" + ConsoleColors.YELLOW + "Options:" + ConsoleColors.RESET);
            System.out.println("  • Enter number (1-" + notifications.size() + ") to mark as read");
            System.out.println("  • Enter 'all' to mark all as read");
            System.out.println("  • Enter 'clear' to delete all read notifications");
            System.out.println("  • Enter 0 to go back");

            System.out.print("\nEnter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("all")) {
                boolean success = notificationDAO.markNotificationsAsRead(userId);
                if (success) {
                    System.out.println(ConsoleColors.GREEN + "✅ All notifications marked as read!" +
                            ConsoleColors.RESET);
                } else {
                    System.out.println("Failed to mark notifications as read.");
                }
            } else if (input.equals("clear")) {
                clearReadNotifications(userId);
            } else {
                try {
                    int choice = Integer.parseInt(input);
                    if (choice > 0 && choice <= notifications.size()) {
                        Notification notification = notifications.get(choice - 1);
                        boolean success = notificationDAO.markNotificationAsRead(notification.getNotificationId());
                        if (success) {
                            System.out.println(ConsoleColors.GREEN + "✅ Notification marked as read!" +
                                    ConsoleColors.RESET);
                        } else {
                            System.out.println("Failed to mark notification as read.");
                        }
                    } else if (choice != 0) {
                        System.out.println("Invalid choice.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displayNotification(Notification notification, int index) {
        // Determine icon and color based on notification type
        String icon = getNotificationIcon(notification.getType());
        String typeColor = getNotificationColor(notification.getType());
        String readIndicator = notification.isRead() ? " " : ConsoleColors.RED_BOLD + "● " + ConsoleColors.RESET;

        // Format time
        String timeAgo = getTimeAgo(notification.getCreatedAt());

        // Get notification message
        String message = getNotificationMessage(notification);

        // Print notification
        System.out.printf("%s%d. %s%s %s%n",
                readIndicator,
                index,
                typeColor + icon + ConsoleColors.RESET,
                message,
                ConsoleColors.DARK_GRAY + "(" + timeAgo + ")" + ConsoleColors.RESET);

        // Show additional details if available
        if (notification.getPostContent() != null && !notification.getPostContent().isEmpty()) {
            String preview = notification.getPostContent();
            if (preview.length() > 60) {
                preview = preview.substring(0, 57) + "...";
            }
            System.out.println("   📝 " + ConsoleColors.DARK_GRAY + "\"" + preview + "\"" +
                    ConsoleColors.RESET);
        }
    }

    private String getNotificationIcon(Notification.NotificationType type) {
        switch (type) {
            case CONNECTION_REQUEST: return "🤝";
            case CONNECTION_ACCEPTED: return "✅";
            case NEW_FOLLOWER: return "👤";
            case LIKE: return "❤️";
            case COMMENT: return "💬";
            case SHARE: return "🔄";
            case NEW_POST: return "📝";
            default: return "🔔";
        }
    }

    private String getNotificationColor(Notification.NotificationType type) {
        switch (type) {
            case CONNECTION_REQUEST: return ConsoleColors.BLUE;
            case CONNECTION_ACCEPTED: return ConsoleColors.GREEN;
            case NEW_FOLLOWER: return ConsoleColors.CYAN;
            case LIKE: return ConsoleColors.RED;
            case COMMENT: return ConsoleColors.YELLOW;
            case SHARE: return ConsoleColors.PURPLE;
            case NEW_POST: return ConsoleColors.ORANGE;
            default: return ConsoleColors.WHITE;
        }
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);

        if (minutes < 1) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + "m ago";
        } else if (hours < 24) {
            return hours + "h ago";
        } else if (days < 7) {
            return days + "d ago";
        } else {
            return createdAt.format(dateFormatter);
        }
    }

    private String getNotificationMessage(Notification notification) {
        String sourceName = notification.getSourceFullName() != null ?
                notification.getSourceFullName() :
                (notification.getSourceUsername() != null ?
                        "@" + notification.getSourceUsername() : "Someone");

        switch (notification.getType()) {
            case CONNECTION_REQUEST:
                return sourceName + " wants to connect with you";
            case CONNECTION_ACCEPTED:
                return sourceName + " accepted your connection request";
            case NEW_FOLLOWER:
                return sourceName + " started following you";
            case LIKE:
                return sourceName + " liked your post";
            case COMMENT:
                return sourceName + " commented on your post";
            case SHARE:
                return sourceName + " shared your post";
            case NEW_POST:
                return sourceName + " created a new post";
            default:
                return "You have a new notification";
        }
    }

    public int getUnreadCount(int userId) {
        return notificationDAO.getUnreadNotificationCount(userId);
    }

    public void clearReadNotifications(int userId) {
        System.out.print("Are you sure you want to delete all read notifications? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            // Delete old notifications (this will keep only recent ones)
            boolean success = notificationDAO.deleteOldNotifications(userId);
            if (success) {
                System.out.println(ConsoleColors.GREEN + "✅ Old notifications cleared!" +
                        ConsoleColors.RESET);
            } else {
                System.out.println("Failed to clear notifications.");
            }
        } else {
            System.out.println("Clear cancelled.");
        }
    }

    public void deleteNotification(int notificationId) {
        boolean success = notificationDAO.deleteNotification(notificationId);
        if (success) {
            System.out.println("Notification deleted.");
        } else {
            System.out.println("Failed to delete notification.");
        }
    }

    // Methods to send notifications
    public void sendConnectionRequestNotification(int recipientId, int requesterId) {
        Notification notification = new Notification(recipientId,
                Notification.NotificationType.CONNECTION_REQUEST);
        notification.setSourceUserId(requesterId);
        boolean success = notificationDAO.createNotification(notification);

        if (success) {
            AppLogger.debug("Connection request notification sent to user: " + recipientId);
        }
    }

    public void sendConnectionAcceptedNotification(int recipientId, int accepterId) {
        Notification notification = new Notification(recipientId,
                Notification.NotificationType.CONNECTION_ACCEPTED);
        notification.setSourceUserId(accepterId);
        boolean success = notificationDAO.createNotification(notification);

        if (success) {
            AppLogger.debug("Connection accepted notification sent to user: " + recipientId);
        }
    }

    public void sendNewFollowerNotification(int recipientId, int followerId) {
        Notification notification = new Notification(recipientId,
                Notification.NotificationType.NEW_FOLLOWER);
        notification.setSourceUserId(followerId);
        boolean success = notificationDAO.createNotification(notification);

        if (success) {
            AppLogger.debug("New follower notification sent to user: " + recipientId);
        }
    }

    public void sendLikeNotification(int recipientId, int likerId, int postId) {
        Notification notification = new Notification(recipientId,
                Notification.NotificationType.LIKE);
        notification.setSourceUserId(likerId);
        notification.setPostId(postId);
        boolean success = notificationDAO.createNotification(notification);

        if (success) {
            AppLogger.debug("Like notification sent to user " + recipientId);
        }
    }

    public void sendCommentNotification(int recipientId, int commenterId, int postId, int commentId) {
        Notification notification = new Notification(recipientId,
                Notification.NotificationType.COMMENT);
        notification.setSourceUserId(commenterId);
        notification.setPostId(postId);
        notification.setCommentId(commentId);
        boolean success = notificationDAO.createNotification(notification);

        if (success) {
            AppLogger.debug("Comment notification sent to user " + recipientId);
        }
    }

    public void sendNewPostNotification(int followerId, int posterId, int postId) {
        Notification notification = new Notification(followerId,
                Notification.NotificationType.NEW_POST);
        notification.setSourceUserId(posterId);
        notification.setPostId(postId);
        boolean success = notificationDAO.createNotification(notification);

        if (success) {
            AppLogger.debug("New post notification sent to user " + followerId);
        }
    }
}