package com.wallet.dao;

import com.wallet.model.Wallet;
import com.wallet.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;

public class WalletDAO {

    public Wallet findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";
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

    public Wallet findById(int walletId) throws SQLException {
        String sql = "SELECT * FROM wallets WHERE wallet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Adds money to a user's own wallet (a deposit — no sender side).
     * Wrapped in its own short transaction: update balance + insert a DEPOSIT record.
     */
    public boolean addMoney(int userId, BigDecimal amount) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Wallet wallet = findByUserIdForUpdate(conn, userId);
            if (wallet == null) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE wallets SET balance = balance + ? WHERE wallet_id = ?")) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, wallet.getWalletId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO transactions (sender_wallet_id, receiver_wallet_id, amount, transaction_type, status) " +
                    "VALUES (NULL, ?, ?, 'DEPOSIT', 'SUCCESS')")) {
                ps.setInt(1, wallet.getWalletId());
                ps.setBigDecimal(2, amount);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
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

    /**
     * Core transfer logic: debit sender, credit receiver, log transaction.
     * All three steps run in ONE transaction — commit only if every step succeeds,
     * otherwise the entire transfer is rolled back so money is never "lost in transit".
     */
    public String transfer(int senderUserId, String receiverEmail, BigDecimal amount) throws SQLException {
        if (amount == null || amount.signum() <= 0) {
            return "Amount must be greater than zero.";
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Lock sender & receiver rows in a consistent order (by wallet_id) to avoid deadlocks
            UserDAO userDAO = new UserDAO();
            var receiverUser = userDAO.findByEmail(receiverEmail);
            if (receiverUser == null) {
                conn.rollback();
                return "No user found with that email.";
            }
            if (receiverUser.getUserId() == senderUserId) {
                conn.rollback();
                return "You cannot send money to yourself.";
            }

            Wallet senderWallet = findByUserIdForUpdate(conn, senderUserId);
            Wallet receiverWallet = findByUserIdForUpdate(conn, receiverUser.getUserId());

            if (senderWallet == null || receiverWallet == null) {
                conn.rollback();
                return "Wallet not found.";
            }

            if (senderWallet.getBalance().compareTo(amount) < 0) {
                conn.rollback();
                return "Insufficient balance.";
            }

            // 1. Debit sender
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE wallets SET balance = balance - ? WHERE wallet_id = ?")) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, senderWallet.getWalletId());
                ps.executeUpdate();
            }

            // 2. Credit receiver
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE wallets SET balance = balance + ? WHERE wallet_id = ?")) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, receiverWallet.getWalletId());
                ps.executeUpdate();
            }

            // 3. Save transaction record
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO transactions (sender_wallet_id, receiver_wallet_id, amount, transaction_type, status) " +
                    "VALUES (?, ?, ?, 'TRANSFER', 'SUCCESS')")) {
                ps.setInt(1, senderWallet.getWalletId());
                ps.setInt(2, receiverWallet.getWalletId());
                ps.setBigDecimal(3, amount);
                ps.executeUpdate();
            }

            // All three succeeded -> commit
            conn.commit();
            return null; // null = success, no error message

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // any failure -> rollback entire transfer
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /** Reads a wallet row with SELECT ... FOR UPDATE so concurrent transfers can't race. */
    private Wallet findByUserIdForUpdate(Connection conn, int userId) throws SQLException {
        String sql = "SELECT * FROM wallets WHERE user_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Wallet mapRow(ResultSet rs) throws SQLException {
        Wallet w = new Wallet();
        w.setWalletId(rs.getInt("wallet_id"));
        w.setUserId(rs.getInt("user_id"));
        w.setBalance(rs.getBigDecimal("balance"));
        w.setCreatedAt(rs.getTimestamp("created_at"));
        return w;
    }
}
