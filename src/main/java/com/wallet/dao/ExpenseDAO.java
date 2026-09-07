package com.wallet.dao;

import com.wallet.model.Expense;
import com.wallet.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDAO {

    public void addExpense(Expense e) throws SQLException {
        String sql = "INSERT INTO expenses (user_id, category, amount, description, expense_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getUserId());
            ps.setString(2, e.getCategory());
            ps.setBigDecimal(3, e.getAmount());
            ps.setString(4, e.getDescription());
            ps.setDate(5, e.getExpenseDate());
            ps.executeUpdate();
        }
    }

    public boolean deleteExpense(int expenseId, int userId) throws SQLException {
        // scope delete to the owning user so one account can't delete another's rows
        String sql = "DELETE FROM expenses WHERE expense_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expenseId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Expense> getExpensesForUser(int userId) throws SQLException {
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY expense_date DESC, expense_id DESC";
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /** Category -> total spent, for the given user (all time). */
    public Map<String, BigDecimal> getCategoryReport(int userId) throws SQLException {
        String sql = "SELECT category, SUM(amount) AS total FROM expenses WHERE user_id = ? GROUP BY category ORDER BY total DESC";
        Map<String, BigDecimal> report = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    report.put(rs.getString("category"), rs.getBigDecimal("total"));
                }
            }
        }
        return report;
    }

    /** Category -> total spent, for the given user within one calendar month (yearMonth = "2026-09"). */
    public Map<String, BigDecimal> getMonthlyCategoryReport(int userId, String yearMonth) throws SQLException {
        String sql = "SELECT category, SUM(amount) AS total FROM expenses " +
                     "WHERE user_id = ? AND DATE_FORMAT(expense_date, '%Y-%m') = ? " +
                     "GROUP BY category ORDER BY total DESC";
        Map<String, BigDecimal> report = new LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    report.put(rs.getString("category"), rs.getBigDecimal("total"));
                }
            }
        }
        return report;
    }

    public BigDecimal getMonthlyTotal(int userId, String yearMonth) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses " +
                     "WHERE user_id = ? AND DATE_FORMAT(expense_date, '%Y-%m') = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setExpenseId(rs.getInt("expense_id"));
        e.setUserId(rs.getInt("user_id"));
        e.setCategory(rs.getString("category"));
        e.setAmount(rs.getBigDecimal("amount"));
        e.setDescription(rs.getString("description"));
        e.setExpenseDate(rs.getDate("expense_date"));
        return e;
    }
}
