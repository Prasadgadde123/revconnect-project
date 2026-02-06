package com.revconnect.dao;

import com.revconnect.model.*;
import com.revconnect.util.AppLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO {

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, user_type, full_name, " +
                "bio, profile_pic_path, location, website, is_private) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getUserType().name());
            ps.setString(5, user.getFullName());
            ps.setString(6, user.getBio());
            ps.setString(7, user.getProfilePicPath());
            ps.setString(8, user.getLocation());
            ps.setString(9, user.getWebsite());
            ps.setBoolean(10, user.isPrivate());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                        AppLogger.info("User created: " + user.getUsername());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error creating user", e);
        }
        return false;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting user by ID", e);
        }
        return null;
    }

    public User getUserByUsernameOrEmail(String identifier) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting user by username/email", e);
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, bio = ?, profile_pic_path = ?, " +
                "location = ?, website = ?, is_private = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getBio());
            ps.setString(3, user.getProfilePicPath());
            ps.setString(4, user.getLocation());
            ps.setString(5, user.getWebsite());
            ps.setBoolean(6, user.isPrivate());
            ps.setInt(7, user.getUserId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                AppLogger.info("User updated: " + user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating user", e);
        }
        return false;
    }

    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE (full_name LIKE ? OR username LIKE ?) " +
                "ORDER BY full_name LIMIT 50";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(createUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error searching users", e);
        }
        return users;
    }

    public boolean changePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Password changed for user ID: " + userId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error changing password", e);
        }
        return false;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User.UserType userType = User.UserType.valueOf(rs.getString("user_type"));
        User user;

        switch (userType) {
            case BUSINESS:
                user = new BusinessUser();
                break;
            case CREATOR:
                user = new CreatorUser();
                break;
            default:
                user = new PersonalUser();
        }

        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setUserType(userType);
        user.setFullName(rs.getString("full_name"));
        user.setBio(rs.getString("bio"));
        user.setProfilePicPath(rs.getString("profile_pic_path"));
        user.setLocation(rs.getString("location"));
        user.setWebsite(rs.getString("website"));
        user.setPrivate(rs.getBoolean("is_private"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return user;
    }


}