package com.revconnect.dao;

import com.revconnect.model.Comment;
import com.revconnect.util.AppLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO extends BaseDAO {

    public boolean createComment(Comment comment) {
        String sql = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getPostId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getContent());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        comment.setCommentId(rs.getInt(1));
                        AppLogger.info("✅ Comment created - ID: " + comment.getCommentId() +
                                ", Post: " + comment.getPostId() + ", User: " + comment.getUserId());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error creating comment", e);
            System.err.println("SQL Error creating comment: " + e.getMessage());
        }
        return false;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.full_name FROM comments c " +
                "JOIN users u ON c.user_id = u.user_id WHERE c.post_id = ? " +
                "ORDER BY c.created_at ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setPostId(rs.getInt("post_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    // Get username and full name
                    comment.setUsername(rs.getString("username"));
                    comment.setFullName(rs.getString("full_name"));

                    comments.add(comment);
                }
                AppLogger.debug("Retrieved " + comments.size() + " comments for post " + postId);
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error getting comments by post ID", e);
            System.err.println("SQL Error getting comments: " + e.getMessage());
        }
        return comments;
    }

    public boolean deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM comments WHERE comment_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                AppLogger.info("✅ Comment deleted - ID: " + commentId);
                return true;
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error deleting comment", e);
            System.err.println("SQL Error deleting comment: " + e.getMessage());
        }
        return false;
    }

    public boolean updateComment(Comment comment) {
        String sql = "UPDATE comments SET content = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE comment_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, comment.getContent());
            ps.setInt(2, comment.getCommentId());
            ps.setInt(3, comment.getUserId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                AppLogger.info("✅ Comment updated - ID: " + comment.getCommentId());
                return true;
            }
        } catch (SQLException e) {
            AppLogger.error("❌ Error updating comment", e);
            System.err.println("SQL Error updating comment: " + e.getMessage());
        }
        return false;
    }
}