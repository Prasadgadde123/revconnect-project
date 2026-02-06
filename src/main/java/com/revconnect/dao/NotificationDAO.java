package com.revconnect.dao;

import com.revconnect.model.Notification;
import com.revconnect.util.AppLogger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends BaseDAO {

    public boolean createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, type, source_user_id, post_id, " +
                "comment_id, connection_id, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getType().name());

            if (notification.getSourceUserId() != null) {
                ps.setInt(3, notification.getSourceUserId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (notification.getPostId() != null) {
                ps.setInt(4, notification.getPostId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (notification.getCommentId() != null) {
                ps.setInt(5, notification.getCommentId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (notification.getConnectionId() != null) {
                ps.setInt(6, notification.getConnectionId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setBoolean(7, notification.isRead());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        notification.setNotificationId(rs.getInt(1));

                        // FIX: Set created_at to current time
                        // The database will set it automatically, but for the Java object
                        // we use current time which will be very close
                        notification.setCreatedAt(LocalDateTime.now());

                        AppLogger.debug("Notification created for user: " + notification.getUserId());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error creating notification", e);
        }
        return false;
    }

    // Alternative: Get the full notification with created_at after insertion
    public boolean createNotificationWithTimestamp(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, type, source_user_id, post_id, " +
                "comment_id, connection_id, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getType().name());

            if (notification.getSourceUserId() != null) {
                ps.setInt(3, notification.getSourceUserId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (notification.getPostId() != null) {
                ps.setInt(4, notification.getPostId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (notification.getCommentId() != null) {
                ps.setInt(5, notification.getCommentId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (notification.getConnectionId() != null) {
                ps.setInt(6, notification.getConnectionId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setBoolean(7, notification.isRead());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int notificationId = rs.getInt(1);
                        notification.setNotificationId(notificationId);

                        // Now fetch the complete notification including created_at
                        Notification fullNotification = getNotificationById(notificationId);
                        if (fullNotification != null) {
                            notification.setCreatedAt(fullNotification.getCreatedAt());
                        } else {
                            // Fallback to current time
                            notification.setCreatedAt(LocalDateTime.now());
                        }

                        AppLogger.debug("Notification created for user: " + notification.getUserId());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error creating notification", e);
        }
        return false;
    }

    // NEW METHOD: Get notification by ID
    public Notification getNotificationById(int notificationId) {
        String sql = "SELECT n.*, " +
                "u.username as source_username, u.full_name as source_full_name, " +
                "p.content as post_content " +
                "FROM notifications n " +
                "LEFT JOIN users u ON n.source_user_id = u.user_id " +
                "LEFT JOIN posts p ON n.post_id = p.post_id " +
                "WHERE n.notification_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createNotificationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting notification by ID", e);
        }
        return null;
    }

    // NEW METHOD: Get notifications for user
    public List<Notification> getNotificationsForUser(int userId, int limit) {
        List<Notification> notifications = new ArrayList<>();

        String sql = "SELECT n.*, " +
                "u.username as source_username, u.full_name as source_full_name, " +
                "p.content as post_content " +
                "FROM notifications n " +
                "LEFT JOIN users u ON n.source_user_id = u.user_id " +
                "LEFT JOIN posts p ON n.post_id = p.post_id " +
                "WHERE n.user_id = ? " +
                "ORDER BY n.created_at DESC " +
                "LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification notification = createNotificationFromResultSet(rs);
                    notifications.add(notification);
                }
            }
            AppLogger.debug("Retrieved " + notifications.size() + " notifications for user: " + userId);
        } catch (SQLException e) {
            AppLogger.error("Error getting notifications for user", e);
        }
        return notifications;
    }

    public int getUnreadNotificationCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = false";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting unread notification count", e);
        }
        return 0;
    }

    public boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = true WHERE notification_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Notification marked as read: " + notificationId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error marking notification as read", e);
        }
        return false;
    }

    public boolean markNotificationsAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("All notifications marked as read for user: " + userId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error marking notifications as read", e);
        }
        return false;
    }

    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Notification deleted: " + notificationId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error deleting notification", e);
        }
        return false;
    }

    // NEW METHOD: Delete old notifications (keep only last 100)
    public boolean deleteOldNotifications(int userId) {
        String sql = "DELETE FROM notifications " +
                "WHERE user_id = ? AND notification_id NOT IN (" +
                "SELECT notification_id FROM (" +
                "SELECT notification_id FROM notifications " +
                "WHERE user_id = ? ORDER BY created_at DESC LIMIT 100" +
                ") as recent_notifications)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Old notifications deleted for user: " + userId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error deleting old notifications", e);
        }
        return false;
    }

    // Helper method to create Notification from ResultSet
    private Notification createNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();

        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));

        // Set type
        String typeStr = rs.getString("type");
        try {
            notification.setType(Notification.NotificationType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            AppLogger.warn("Unknown notification type: " + typeStr);
        }

        // Set optional fields
        int sourceUserId = rs.getInt("source_user_id");
        if (!rs.wasNull()) {
            notification.setSourceUserId(sourceUserId);
        }

        int postId = rs.getInt("post_id");
        if (!rs.wasNull()) {
            notification.setPostId(postId);
        }

        int commentId = rs.getInt("comment_id");
        if (!rs.wasNull()) {
            notification.setCommentId(commentId);
        }

        int connectionId = rs.getInt("connection_id");
        if (!rs.wasNull()) {
            notification.setConnectionId(connectionId);
        }

        notification.setRead(rs.getBoolean("is_read"));

        // Safely get created_at
        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                notification.setCreatedAt(createdAt.toLocalDateTime());
            } else {
                notification.setCreatedAt(LocalDateTime.now());
            }
        } catch (SQLException e) {
            // Column might not exist in this result set
            notification.setCreatedAt(LocalDateTime.now());
        }

        // Set transient fields
        notification.setSourceUsername(rs.getString("source_username"));
        notification.setSourceFullName(rs.getString("source_full_name"));
        notification.setPostContent(rs.getString("post_content"));

        return notification;
    }
}