package com.wallet.servlet;

import com.wallet.dao.ExpenseDAO;
import com.wallet.model.Expense;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

@WebServlet("/app/expenses")
public class ExpenseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession().getAttribute("userId");
        try {
            ExpenseDAO expenseDAO = new ExpenseDAO();
            String currentMonth = YearMonth.now().toString();

            req.setAttribute("expenses", expenseDAO.getExpensesForUser(userId));
            req.setAttribute("categoryReport", expenseDAO.getCategoryReport(userId));
            req.setAttribute("monthlyReport", expenseDAO.getMonthlyCategoryReport(userId, currentMonth));
            req.setAttribute("monthlyTotal", expenseDAO.getMonthlyTotal(userId, currentMonth));
            req.setAttribute("currentMonth", currentMonth);

            req.getRequestDispatcher("/expenses.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, NumberFormatException {
        int userId = (int) req.getSession().getAttribute("userId");
        String action = req.getParameter("action");

        try {
            ExpenseDAO expenseDAO = new ExpenseDAO();

            if ("delete".equals(action)) {
                int expenseId = Integer.parseInt(req.getParameter("expenseId"));
                expenseDAO.deleteExpense(expenseId, userId);
            } else {
                // default: add
                String category = req.getParameter("category");
                String amountStr = req.getParameter("amount");
                String description = req.getParameter("description");
                String dateStr = req.getParameter("expenseDate");

                Expense e = new Expense();
                e.setUserId(userId);
                e.setCategory(category);
                e.setAmount(new BigDecimal(amountStr));
                e.setDescription(description);
                e.setExpenseDate(dateStr == null || dateStr.isBlank()
                        ? Date.valueOf(LocalDate.now())
                        : Date.valueOf(dateStr));

                expenseDAO.addExpense(e);
            }

            resp.sendRedirect(req.getContextPath() + "/app/expenses");
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Invalid input: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
