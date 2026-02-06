package com.revconnect.dao;

import com.revconnect.model.Post;
import com.revconnect.util.AppLogger;
import com.revconnect.util.ValidationUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO extends BaseDAO {

    // Create a new post
    public boolean createPost(Post post) {
        String sql = "INSERT INTO posts (user_id, content, hashtags, post_type, call_to_action, " +
                "tagged_products, scheduled_time, is_pinned, reach_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getContent());

            // Extract hashtags from content
            String hashtags = ValidationUtil.extractHashtags(post.getContent());
            ps.setString(3, hashtags);

            ps.setString(4, post.getPostType().name());
            ps.setString(5, post.getCallToAction());
            ps.setString(6, post.getTaggedProducts());

            if (post.getScheduledTime() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(post.getScheduledTime()));
            } else {
                ps.setTimestamp(7, null);
            }

            ps.setBoolean(8, post.isPinned());
            ps.setInt(9, post.getReachCount());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        post.setPostId(rs.getInt(1));
                        AppLogger.info("Post created - ID: " + post.getPostId() + ", User: " + post.getUserId());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error creating post", e);
        }
        return false;
    }

    // UPDATE POST - NEW METHOD
    public boolean updatePost(Post post) {
        String sql = "UPDATE posts SET content = ?, hashtags = ?, post_type = ?, " +
                "call_to_action = ?, tagged_products = ?, is_pinned = ?, " +
                "reach_count = ?, updated_at = CURRENT_TIMESTAMP WHERE post_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, post.getContent());

            // Extract hashtags from content
            String hashtags = ValidationUtil.extractHashtags(post.getContent());
            ps.setString(2, hashtags);

            ps.setString(3, post.getPostType().name());
            ps.setString(4, post.getCallToAction());
            ps.setString(5, post.getTaggedProducts());
            ps.setBoolean(6, post.isPinned());
            ps.setInt(7, post.getReachCount());
            ps.setInt(8, post.getPostId());
            ps.setInt(9, post.getUserId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                AppLogger.info("Post updated - ID: " + post.getPostId() + ", User: " + post.getUserId());
                return true;
            } else {
                AppLogger.warn("Post update failed - ID: " + post.getPostId() + " (user may not own this post)");
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating post", e);
        }
        return false;
    }

    // Get post by ID
    public Post getPostById(int postId) {
        String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id WHERE p.post_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createPostFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting post by ID", e);
        }
        return null;
    }

    // Get posts by user ID
    public List<Post> getPostsByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id WHERE p.user_id = ? " +
                "ORDER BY p.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(createPostFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting posts by user ID", e);
        }
        return posts;
    }

    // Get feed for user (posts from connections and followed accounts)
    public List<Post> getFeedForUser(int userId, int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT DISTINCT p.*, u.username, u.full_name FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN connections c ON (c.user_id1 = ? OR c.user_id2 = ?) " +
                "AND (p.user_id = c.user_id1 OR p.user_id = c.user_id2) AND c.status = 'ACCEPTED' " +
                "LEFT JOIN follows f ON f.following_id = p.user_id AND f.follower_id = ? " +
                "WHERE p.user_id = ? OR c.connection_id IS NOT NULL OR f.follow_id IS NOT NULL " +
                "ORDER BY p.created_at DESC LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(createPostFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting feed for user", e);
        }
        return posts;
    }

    // Delete post
    public boolean deletePost(int postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                AppLogger.info("Post deleted - ID: " + postId);
                return true;
            }
        } catch (SQLException e) {
            AppLogger.error("Error deleting post", e);
        }
        return false;
    }

    // Search posts by hashtag
    public List<Post> searchPostsByHashtag(String hashtag) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.hashtags LIKE ? ORDER BY p.created_at DESC LIMIT 50";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + hashtag + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(createPostFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error searching posts by hashtag", e);
        }
        return posts;
    }

    // Get trending posts
    public List<Post> getTrendingPosts(int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as like_count, " +
                "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.post_id) as comment_count, " +
                "(SELECT COUNT(*) FROM shares s WHERE s.original_post_id = p.post_id) as share_count " +
                "FROM posts p JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                "ORDER BY (like_count * 2 + comment_count * 3 + share_count * 5) DESC LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post post = createPostFromResultSet(rs);
                    post.setLikeCount(rs.getInt("like_count"));
                    post.setCommentCount(rs.getInt("comment_count"));
                    post.setShareCount(rs.getInt("share_count"));
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting trending posts", e);
        }
        return posts;
    }

    // Get posts by user ID with pagination
    public List<Post> getPostsByUserIdWithPagination(int userId, int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id WHERE p.user_id = ? " +
                "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(createPostFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting posts with pagination", e);
        }
        return posts;
    }

    // Get pinned posts for a user
    public List<Post> getPinnedPosts(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.user_id = ? AND p.is_pinned = true " +
                "ORDER BY p.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(createPostFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting pinned posts", e);
        }
        return posts;
    }

    // Pin/Unpin a post
    public boolean togglePinPost(int postId, int userId, boolean pin) {
        String sql = "UPDATE posts SET is_pinned = ? WHERE post_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, pin);
            ps.setInt(2, postId);
            ps.setInt(3, userId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                AppLogger.info("Post " + (pin ? "pinned" : "unpinned") + " - ID: " + postId);
                return true;
            }
        } catch (SQLException e) {
            AppLogger.error("Error toggling pin status", e);
        }
        return false;
    }

    // Increment reach count
    public boolean incrementReachCount(int postId) {
        String sql = "UPDATE posts SET reach_count = reach_count + 1 WHERE post_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.error("Error incrementing reach count", e);
        }
        return false;
    }

    // Get post count for user
    public int getPostCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM posts WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting post count", e);
        }
        return 0;
    }

    // Helper method to create Post from ResultSet
    private Post createPostFromResultSet(ResultSet rs) throws SQLException {
        Post post = new Post();

        post.setPostId(rs.getInt("post_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setHashtags(rs.getString("hashtags"));

        try {
            post.setPostType(Post.PostType.valueOf(rs.getString("post_type")));
        } catch (IllegalArgumentException e) {
            post.setPostType(Post.PostType.TEXT);
        }

        post.setCallToAction(rs.getString("call_to_action"));
        post.setTaggedProducts(rs.getString("tagged_products"));

        Timestamp scheduledTime = rs.getTimestamp("scheduled_time");
        if (scheduledTime != null) {
            post.setScheduledTime(scheduledTime.toLocalDateTime());
        }

        post.setPinned(rs.getBoolean("is_pinned"));
        post.setReachCount(rs.getInt("reach_count"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            post.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            post.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        // Transient fields
        post.setUsername(rs.getString("username"));
        post.setFullName(rs.getString("full_name"));

        return post;
    }
}