package com.revconnect.model;

import java.time.LocalDateTime;

public class Notification {
    private int notificationId;
    private int userId;
    private NotificationType type;
    private Integer sourceUserId;
    private Integer postId;
    private Integer commentId;
    private Integer connectionId;
    private boolean isRead;
    private LocalDateTime createdAt;

    // Transient fields for display
    private String sourceUsername;
    private String sourceFullName;
    private String postContent;

    public enum NotificationType {
        CONNECTION_REQUEST, CONNECTION_ACCEPTED, NEW_FOLLOWER,
        LIKE, COMMENT, SHARE, NEW_POST
    }

    // Constructors
    public Notification() {}

    public Notification(int userId, NotificationType type) {
        this.userId = userId;
        this.type = type;
    }

    // Getters and Setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public Integer getSourceUserId() { return sourceUserId; }
    public void setSourceUserId(Integer sourceUserId) { this.sourceUserId = sourceUserId; }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public Integer getConnectionId() { return connectionId; }
    public void setConnectionId(Integer connectionId) { this.connectionId = connectionId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Transient fields
    public String getSourceUsername() { return sourceUsername; }
    public void setSourceUsername(String sourceUsername) { this.sourceUsername = sourceUsername; }

    public String getSourceFullName() { return sourceFullName; }
    public void setSourceFullName(String sourceFullName) { this.sourceFullName = sourceFullName; }

    public String getPostContent() { return postContent; }
    public void setPostContent(String postContent) { this.postContent = postContent; }

    @Override
    public String toString() {
        return String.format("Notification{id=%d, type=%s, userId=%d, isRead=%s}",
                notificationId, type, userId, isRead);
    }
}