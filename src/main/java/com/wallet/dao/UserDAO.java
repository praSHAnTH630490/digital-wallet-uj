package com.wallet.dao;

import com.wallet.model.User;
import com.wallet.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {

    /** Registers a new user and creates their wallet (balance 0) in one transaction. */
    public boolean registerUser(String name, String email, String plainPassword) throws SQLException {
        String insertUserSql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        String insertWalletSql = "INSERT INTO wallets (user_id, balance) VALUES (?, 0.00)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

            int newUserId;
            try (PreparedStatement ps = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, hashed);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        newUserId = keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve generated user_id");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertWalletSql)) {
                ps.setInt(1, newUserId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLIntegrityConstraintViolationException dup) {
            if (conn != null) conn.rollback();
            return false; // email already registered
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /** Returns the authenticated user if the email/password pair is valid, else null. */
    public User authenticate(String email, String plainPassword) throws SQLException {
        User user = findByEmail(email);
        if (user != null && BCrypt.checkpw(plainPassword, user.getPassword())) {
            return user;
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
