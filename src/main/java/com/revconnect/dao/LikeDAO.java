package com.revconnect.dao;

import com.revconnect.util.AppLogger;
import java.sql.*;

public class LikeDAO extends BaseDAO {

    public boolean addLike(int postId, int userId) {
        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            if (result > 0) {
                AppLogger.info("✅ Like added - Post: " + postId + ", User: " + userId);
                return true;
            }
        } catch (SQLException e) {
            // Check if it's a duplicate entry error
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error code
                AppLogger.debug("User already liked this post");
                return false;
            }
            AppLogger.error("❌ Error adding like", e);
            System.err.println("SQL Error adding like: " + e.getMessage());
        }
        return false;
    }

    public boolean removeLike(int postId, int userId) {
        String sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            if (result > 0) {
                AppLogger.info("✅ Like removed - Post: " + postId + ", User: " + userId);
                return true;
            } else {
                AppLogger.debug("No like found to remove");
                return false;
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error removing like", e);
            System.err.println("SQL Error removing like: " + e.getMessage());
        }
        return false;
    }

    public boolean hasUserLikedPost(int postId, int userId) {
        String sql = "SELECT 1 FROM likes WHERE post_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean liked = rs.next();
                AppLogger.debug("Check if user liked post - Post: " + postId +
                        ", User: " + userId + ", Liked: " + liked);
                return liked;
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error checking if user liked post", e);
            System.err.println("SQL Error checking like: " + e.getMessage());
        }
        return false;
    }

    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) as count FROM likes WHERE post_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    AppLogger.debug("Like count for post " + postId + ": " + count);
                    return count;
                }
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error getting like count", e);
            System.err.println("SQL Error getting like count: " + e.getMessage());
        }
        return 0;
    }
}