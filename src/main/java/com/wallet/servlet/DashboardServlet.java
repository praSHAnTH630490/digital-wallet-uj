package com.wallet.servlet;

import com.wallet.dao.ExpenseDAO;
import com.wallet.dao.WalletDAO;
import com.wallet.model.Wallet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.YearMonth;

@WebServlet("/app/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("userId");
        try {
            Wallet wallet = new WalletDAO().findByUserId(userId);
            String currentMonth = YearMonth.now().toString(); // e.g. 2026-09
            var monthlyTotal = new ExpenseDAO().getMonthlyTotal(userId, currentMonth);

            req.setAttribute("wallet", wallet);
            req.setAttribute("monthlyExpenseTotal", monthlyTotal);
            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
