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

@WebServlet("/app/transfer")
public class TransferServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/transfer.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("userId");
        String receiverEmail = req.getParameter("receiverEmail");
        String amountStr = req.getParameter("amount");

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            String errorMessage = new WalletDAO().transfer(userId, receiverEmail == null ? "" : receiverEmail.trim().toLowerCase(), amount);

            if (errorMessage == null) {
                req.setAttribute("success", "Transfer of " + amount + " completed.");
            } else {
                req.setAttribute("error", errorMessage);
            }
            req.getRequestDispatcher("/transfer.jsp").forward(req, resp);

        } catch (NumberFormatException nfe) {
            req.setAttribute("error", "Enter a valid amount.");
            req.getRequestDispatcher("/transfer.jsp").forward(req, resp);
        } catch (SQLException e) {
            // Any DB-level failure means the transaction was rolled back in WalletDAO.transfer()
            req.setAttribute("error", "Transfer failed and was rolled back: " + e.getMessage());
            req.getRequestDispatcher("/transfer.jsp").forward(req, resp);
        }
    }
}
