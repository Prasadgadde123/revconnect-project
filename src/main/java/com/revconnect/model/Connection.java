package com.revconnect.model;

import java.time.LocalDateTime;

public class Connection {
    private int connectionId;
    private int userId1;
    private int userId2;
    private ConnectionStatus status;
    private int requestedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for display
    private String user1Username;
    private String user1FullName;
    private String user2Username;
    private String user2FullName;
    private String requestedByUsername;

    public enum ConnectionStatus {
        PENDING, ACCEPTED, REJECTED
    }

    // Constructors
    public Connection() {}

    public Connection(int userId1, int userId2, int requestedBy) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.requestedBy = requestedBy;
        this.status = ConnectionStatus.PENDING;
    }

    // Getters and Setters
    public int getConnectionId() { return connectionId; }
    public void setConnectionId(int connectionId) { this.connectionId = connectionId; }

    public int getUserId1() { return userId1; }
    public void setUserId1(int userId1) { this.userId1 = userId1; }

    public int getUserId2() { return userId2; }
    public void setUserId2(int userId2) { this.userId2 = userId2; }

    public ConnectionStatus getStatus() { return status; }
    public void setStatus(ConnectionStatus status) { this.status = status; }

    public int getRequestedBy() { return requestedBy; }
    public void setRequestedBy(int requestedBy) { this.requestedBy = requestedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Transient fields
    public String getUser1Username() { return user1Username; }
    public void setUser1Username(String user1Username) { this.user1Username = user1Username; }

    public String getUser1FullName() { return user1FullName; }
    public void setUser1FullName(String user1FullName) { this.user1FullName = user1FullName; }

    public String getUser2Username() { return user2Username; }
    public void setUser2Username(String user2Username) { this.user2Username = user2Username; }

    public String getUser2FullName() { return user2FullName; }
    public void setUser2FullName(String user2FullName) { this.user2FullName = user2FullName; }

    public String getRequestedByUsername() { return requestedByUsername; }
    public void setRequestedByUsername(String requestedByUsername) { this.requestedByUsername = requestedByUsername; }

    @Override
    public String toString() {
        return String.format("Connection{connectionId=%d, userId1=%d, userId2=%d, status=%s}",
                connectionId, userId1, userId2, status);
    }
}