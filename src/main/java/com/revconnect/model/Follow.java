package com.revconnect.model;

import java.time.LocalDateTime;

public class Follow {
    private int followId;
    private int followerId;
    private int followingId;
    private LocalDateTime createdAt;

    // Transient fields for display
    private String followerUsername;
    private String followerFullName;
    private String followingUsername;
    private String followingFullName;

    // Constructors
    public Follow() {}

    public Follow(int followerId, int followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    // Getters and Setters
    public int getFollowId() { return followId; }
    public void setFollowId(int followId) { this.followId = followId; }

    public int getFollowerId() { return followerId; }
    public void setFollowerId(int followerId) { this.followerId = followerId; }

    public int getFollowingId() { return followingId; }
    public void setFollowingId(int followingId) { this.followingId = followingId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Transient fields
    public String getFollowerUsername() { return followerUsername; }
    public void setFollowerUsername(String followerUsername) { this.followerUsername = followerUsername; }

    public String getFollowerFullName() { return followerFullName; }
    public void setFollowerFullName(String followerFullName) { this.followerFullName = followerFullName; }

    public String getFollowingUsername() { return followingUsername; }
    public void setFollowingUsername(String followingUsername) { this.followingUsername = followingUsername; }

    public String getFollowingFullName() { return followingFullName; }
    public void setFollowingFullName(String followingFullName) { this.followingFullName = followingFullName; }

    @Override
    public String toString() {
        return String.format("Follow{followId=%d, followerId=%d, followingId=%d}",
                followId, followerId, followingId);
    }
}