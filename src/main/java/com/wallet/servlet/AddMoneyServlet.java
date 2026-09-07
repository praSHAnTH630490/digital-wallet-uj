package com.wallet.servlet;

import com.wallet.dao.WalletDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/app/add-money")
public class AddMoneyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/add-money.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("userId");
        String amountStr = req.getParameter("amount");

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.signum() <= 0) {
                req.setAttribute("error", "Amount must be greater than zero.");
                req.getRequestDispatcher("/add-money.jsp").forward(req, resp);
                return;
            }

            boolean ok = new WalletDAO().addMoney(userId, amount);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/app/dashboard");
            } else {
                req.setAttribute("error", "Could not add money — wallet not found.");
                req.getRequestDispatcher("/add-money.jsp").forward(req, resp);
            }
        } catch (NumberFormatException nfe) {
            req.setAttribute("error", "Enter a valid amount.");
            req.getRequestDispatcher("/add-money.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Add money failed: " + e.getMessage());
            req.getRequestDispatcher("/add-money.jsp").forward(req, resp);
        }
    }
}
