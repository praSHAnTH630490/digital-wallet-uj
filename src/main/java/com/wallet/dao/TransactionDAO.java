package com.wallet.dao;

import com.wallet.model.Transaction;
import com.wallet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /**
     * Full transaction history for a user's wallet: deposits, money sent, and money received.
     * Joins wallets -> users to show the counterparty's name instead of a raw wallet_id.
     */
    public List<Transaction> getHistoryForUser(int userId) throws SQLException {
        // First find the user's own wallet_id so we can tell which side of each row they're on.
        int ownWalletId;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT wallet_id FROM wallets WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new ArrayList<>();
                ownWalletId = rs.getInt("wallet_id");
            }
        }

        String sql =
            "SELECT t.*, su.name AS sender_name, ru.name AS receiver_name " +
            "FROM transactions t " +
            "LEFT JOIN wallets sw ON sw.wallet_id = t.sender_wallet_id " +
            "LEFT JOIN users su ON su.user_id = sw.user_id " +
            "LEFT JOIN wallets rw ON rw.wallet_id = t.receiver_wallet_id " +
            "LEFT JOIN users ru ON ru.user_id = rw.user_id " +
            "WHERE t.sender_wallet_id = ? OR t.receiver_wallet_id = ? " +
            "ORDER BY t.created_at DESC";

        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownWalletId);
            ps.setInt(2, ownWalletId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setTransactionId(rs.getInt("transaction_id"));
                    int senderWalletId = rs.getInt("sender_wallet_id");
                    t.setSenderWalletId(rs.wasNull() ? null : senderWalletId);
                    int receiverWalletId = rs.getInt("receiver_wallet_id");
                    t.setReceiverWalletId(rs.wasNull() ? null : receiverWalletId);
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setTransactionType(rs.getString("transaction_type"));
                    t.setStatus(rs.getString("status"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));

                    String senderName = rs.getString("sender_name");
                    String receiverName = rs.getString("receiver_name");

                    if ("TRANSFER".equals(t.getTransactionType())) {
                        boolean iAmSender = senderWalletId == ownWalletId;
                        // Relabel from this user's point of view: if I'm the sender, show who
                        // received it (and vice versa) as a "RECEIVED" row for the other party.
                        t.setCounterpartyName(iAmSender ? receiverName : senderName);
                        if (!iAmSender) {
                            t.setTransactionType("RECEIVED");
                        }
                    }
                    list.add(t);
                }
            }
        }
        return list;
    }
}
