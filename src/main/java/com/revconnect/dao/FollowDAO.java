package com.revconnect.dao;

import com.revconnect.model.Follow;
import com.revconnect.util.AppLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO extends BaseDAO {

    // This method was missing from your code
    public boolean followUser(int followerId, int followingId) {
        String sql = "INSERT INTO follows (follower_id, following_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE created_at = CURRENT_TIMESTAMP";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("User " + followerId + " followed user " + followingId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error following user", e);
        }
        return false;
    }

    // This method was missing from your code
    public boolean unfollowUser(int followerId, int followingId) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("User " + followerId + " unfollowed user " + followingId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error unfollowing user", e);
        }
        return false;
    }

    // This method was missing from your code
    public boolean isFollowing(int followerId, int followingId) {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND following_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            AppLogger.error("Error checking if following", e);
        }
        return false;
    }

    // This method was missing from your code
    public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM follows WHERE following_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting follower count", e);
        }
        return 0;
    }

    // This method was missing from your code
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM follows WHERE follower_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting following count", e);
        }
        return 0;
    }

    public List<Follow> getFollowersForUser(int userId) {
        List<Follow> followers = new ArrayList<>();
        String sql = "SELECT f.*, u.username as follower_username, u.full_name as follower_full_name " +
                "FROM follows f " +
                "JOIN users u ON f.follower_id = u.user_id " +
                "WHERE f.following_id = ? " +
                "ORDER BY f.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Follow follow = new Follow();
                    follow.setFollowId(rs.getInt("follow_id"));
                    follow.setFollowerId(rs.getInt("follower_id"));
                    follow.setFollowingId(rs.getInt("following_id"));
                    follow.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    // Set transient fields
                    follow.setFollowerUsername(rs.getString("follower_username"));
                    follow.setFollowerFullName(rs.getString("follower_full_name"));

                    followers.add(follow);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting followers for user", e);
        }
        return followers;
    }

    public List<Follow> getFollowingForUser(int userId) {
        List<Follow> following = new ArrayList<>();
        String sql = "SELECT f.*, u.username as following_username, u.full_name as following_full_name " +
                "FROM follows f " +
                "JOIN users u ON f.following_id = u.user_id " +
                "WHERE f.follower_id = ? " +
                "ORDER BY f.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Follow follow = new Follow();
                    follow.setFollowId(rs.getInt("follow_id"));
                    follow.setFollowerId(rs.getInt("follower_id"));
                    follow.setFollowingId(rs.getInt("following_id"));
                    follow.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    // Set transient fields
                    follow.setFollowingUsername(rs.getString("following_username"));
                    follow.setFollowingFullName(rs.getString("following_full_name"));

                    following.add(follow);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting following for user", e);
        }
        return following;
    }

    public List<Follow> getMutualFollows(int userId) {
        List<Follow> mutuals = new ArrayList<>();
        String sql = "SELECT f1.*, u.username, u.full_name " +
                "FROM follows f1 " +
                "JOIN follows f2 ON f1.follower_id = f2.following_id AND f1.following_id = f2.follower_id " +
                "JOIN users u ON f1.following_id = u.user_id " +
                "WHERE f1.follower_id = ? " +
                "ORDER BY f1.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Follow follow = new Follow();
                    follow.setFollowId(rs.getInt("follow_id"));
                    follow.setFollowerId(rs.getInt("follower_id"));
                    follow.setFollowingId(rs.getInt("following_id"));
                    follow.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    follow.setFollowingUsername(rs.getString("username"));
                    follow.setFollowingFullName(rs.getString("full_name"));

                    mutuals.add(follow);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting mutual follows", e);
        }
        return mutuals;
    }
}