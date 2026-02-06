package com.revconnect.dao;

import com.revconnect.model.Connection;
import com.revconnect.util.AppLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDAO extends BaseDAO {

    public boolean sendConnectionRequest(int userId1, int userId2, int requestedBy) {
        int smallerId = Math.min(userId1, userId2);
        int largerId = Math.max(userId1, userId2);

        String sql = "INSERT INTO connections (user_id1, user_id2, status, requested_by) " +
                "VALUES (?, ?, 'PENDING', ?) " +
                "ON DUPLICATE KEY UPDATE status = 'PENDING', requested_by = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, smallerId);
            ps.setInt(2, largerId);
            ps.setInt(3, requestedBy);
            ps.setInt(4, requestedBy);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Connection request sent from " + requestedBy + " to " + largerId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error sending connection request", e);
        }
        return false;
    }

    public boolean acceptConnectionRequest(int connectionId) {
        String sql = "UPDATE connections SET status = 'ACCEPTED' WHERE connection_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, connectionId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Connection request accepted: " + connectionId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error accepting connection request", e);
        }
        return false;
    }

    public boolean rejectConnectionRequest(int connectionId) {
        String sql = "UPDATE connections SET status = 'REJECTED' WHERE connection_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, connectionId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Connection request rejected: " + connectionId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error rejecting connection request", e);
        }
        return false;
    }

    public boolean removeConnection(int connectionId) {
        String sql = "DELETE FROM connections WHERE connection_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, connectionId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AppLogger.info("Connection removed: " + connectionId);
            }
            return success;
        } catch (SQLException e) {
            AppLogger.error("Error removing connection", e);
        }
        return false;
    }

    public boolean areUsersConnected(int userId1, int userId2) {
        int smallerId = Math.min(userId1, userId2);
        int largerId = Math.max(userId1, userId2);

        String sql = "SELECT 1 FROM connections " +
                "WHERE user_id1 = ? AND user_id2 = ? AND status = 'ACCEPTED'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, smallerId);
            ps.setInt(2, largerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            AppLogger.error("Error checking if users are connected", e);
        }
        return false;
    }

    // NEW METHOD: Get pending connections for a user
    public List<Connection> getPendingConnectionsForUser(int userId) {
        List<Connection> pendingConnections = new ArrayList<>();

        String sql = "SELECT c.*, " +
                "u1.username as user1_username, u1.full_name as user1_full_name, " +
                "u2.username as user2_username, u2.full_name as user2_full_name, " +
                "ur.username as requested_by_username " +
                "FROM connections c " +
                "JOIN users u1 ON c.user_id1 = u1.user_id " +
                "JOIN users u2 ON c.user_id2 = u2.user_id " +
                "JOIN users ur ON c.requested_by = ur.user_id " +
                "WHERE (c.user_id1 = ? OR c.user_id2 = ?) " +
                "AND c.status = 'PENDING' " +
                "AND c.requested_by != ? " + // Only show requests sent TO the user, not FROM the user
                "ORDER BY c.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Connection connection = createConnectionFromResultSet(rs);
                    pendingConnections.add(connection);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting pending connections for user", e);
        }
        return pendingConnections;
    }

    // NEW METHOD: Get all connections for a user (accepted only)
    public List<Connection> getConnectionsForUser(int userId) {
        List<Connection> connections = new ArrayList<>();

        String sql = "SELECT c.*, " +
                "u1.username as user1_username, u1.full_name as user1_full_name, " +
                "u2.username as user2_username, u2.full_name as user2_full_name, " +
                "ur.username as requested_by_username " +
                "FROM connections c " +
                "JOIN users u1 ON c.user_id1 = u1.user_id " +
                "JOIN users u2 ON c.user_id2 = u2.user_id " +
                "JOIN users ur ON c.requested_by = ur.user_id " +
                "WHERE (c.user_id1 = ? OR c.user_id2 = ?) " +
                "AND c.status = 'ACCEPTED' " +
                "ORDER BY c.updated_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Connection connection = createConnectionFromResultSet(rs);
                    connections.add(connection);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting connections for user", e);
        }
        return connections;
    }

    // NEW METHOD: Get connection by ID
    public Connection getConnectionById(int connectionId) {
        String sql = "SELECT c.*, " +
                "u1.username as user1_username, u1.full_name as user1_full_name, " +
                "u2.username as user2_username, u2.full_name as user2_full_name, " +
                "ur.username as requested_by_username " +
                "FROM connections c " +
                "JOIN users u1 ON c.user_id1 = u1.user_id " +
                "JOIN users u2 ON c.user_id2 = u2.user_id " +
                "JOIN users ur ON c.requested_by = ur.user_id " +
                "WHERE c.connection_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, connectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createConnectionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting connection by ID", e);
        }
        return null;
    }

    // NEW METHOD: Get sent pending requests (requests sent BY the user)
    public List<Connection> getSentPendingRequests(int userId) {
        List<Connection> sentRequests = new ArrayList<>();

        String sql = "SELECT c.*, " +
                "u1.username as user1_username, u1.full_name as user1_full_name, " +
                "u2.username as user2_username, u2.full_name as user2_full_name, " +
                "ur.username as requested_by_username " +
                "FROM connections c " +
                "JOIN users u1 ON c.user_id1 = u1.user_id " +
                "JOIN users u2 ON c.user_id2 = u2.user_id " +
                "JOIN users ur ON c.requested_by = ur.user_id " +
                "WHERE (c.user_id1 = ? OR c.user_id2 = ?) " +
                "AND c.status = 'PENDING' " +
                "AND c.requested_by = ? " + // Only show requests sent BY the user
                "ORDER BY c.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Connection connection = createConnectionFromResultSet(rs);
                    sentRequests.add(connection);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error getting sent pending requests", e);
        }
        return sentRequests;
    }

    // Helper method to create Connection from ResultSet
    private Connection createConnectionFromResultSet(ResultSet rs) throws SQLException {
        Connection connection = new Connection();

        connection.setConnectionId(rs.getInt("connection_id"));
        connection.setUserId1(rs.getInt("user_id1"));
        connection.setUserId2(rs.getInt("user_id2"));
        connection.setRequestedBy(rs.getInt("requested_by"));

        // Set status
        String statusStr = rs.getString("status");
        try {
            connection.setStatus(Connection.ConnectionStatus.valueOf(statusStr));
        } catch (IllegalArgumentException e) {
            connection.setStatus(Connection.ConnectionStatus.PENDING);
        }

        // Set timestamps
        connection.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        connection.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        // Set transient fields
        connection.setUser1Username(rs.getString("user1_username"));
        connection.setUser1FullName(rs.getString("user1_full_name"));
        connection.setUser2Username(rs.getString("user2_username"));
        connection.setUser2FullName(rs.getString("user2_full_name"));
        connection.setRequestedByUsername(rs.getString("requested_by_username"));

        return connection;
    }
}