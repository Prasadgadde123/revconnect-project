package com.revconnect.model;

import java.time.LocalDateTime;
import java.util.List;

public class Post {
    private int postId;
    private int userId;
    private String content;
    private String hashtags;
    private PostType postType;
    private String callToAction;
    private String taggedProducts;
    private LocalDateTime scheduledTime;
    private boolean isPinned;
    private int reachCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for display
    private String username;
    private String fullName;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private boolean isLikedByCurrentUser;
    private List<Comment> recentComments;

    public enum PostType {
        TEXT, PROMOTIONAL, ANNOUNCEMENT
    }

    // Constructors
    public Post() {}

    public Post(int userId, String content) {
        this.userId = userId;
        this.content = content;
        this.postType = PostType.TEXT;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }

    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }

    public String getCallToAction() { return callToAction; }
    public void setCallToAction(String callToAction) { this.callToAction = callToAction; }

    public String getTaggedProducts() { return taggedProducts; }
    public void setTaggedProducts(String taggedProducts) { this.taggedProducts = taggedProducts; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean isPinned) { this.isPinned = isPinned; }

    public int getReachCount() { return reachCount; }
    public void setReachCount(int reachCount) { this.reachCount = reachCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Transient fields getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public int getShareCount() { return shareCount; }
    public void setShareCount(int shareCount) { this.shareCount = shareCount; }

    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; }
    public void setLikedByCurrentUser(boolean isLikedByCurrentUser) { this.isLikedByCurrentUser = isLikedByCurrentUser; }

    public List<Comment> getRecentComments() { return recentComments; }
    public void setRecentComments(List<Comment> recentComments) { this.recentComments = recentComments; }

    @Override
    public String toString() {
        return String.format("Post{postId=%d, userId=%d, content='%s', type=%s}",
                postId, userId, (content.length() > 50 ? content.substring(0, 50) + "..." : content), postType);
    }
}