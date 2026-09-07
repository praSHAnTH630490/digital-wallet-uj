<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Expenses — Digital Wallet</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="navbar">
    <span class="brand">💰 Digital Wallet</span>
    <span>
        <a href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/app/transfer">Send Money</a>
        <a href="${pageContext.request.contextPath}/app/add-money">Add Money</a>
        <a href="${pageContext.request.contextPath}/app/expenses">Expenses</a>
        <a href="${pageContext.request.contextPath}/app/history">History</a>
        <a href="${pageContext.request.contextPath}/app/logout">Logout</a>
    </span>
</div>

<div class="container" style="max-width:760px;">

    <div class="card">
        <h2>Add Expense</h2>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/app/expenses">
            <input type="hidden" name="action" value="add">
            <label>Category</label>
            <select name="category" required>
                <option value="Food">Food</option>
                <option value="Travel">Travel</option>
                <option value="Shopping">Shopping</option>
                <option value="Bills">Bills</option>
                <option value="Entertainment">Entertainment</option>
                <option value="Other">Other</option>
            </select>

            <label>Amount (₹)</label>
            <input type="number" step="0.01" min="0.01" name="amount" required>

            <label>Description</label>
            <input type="text" name="description" placeholder="Optional note">

            <label>Date</label>
            <input type="date" name="expenseDate">

            <button type="submit">Add Expense</button>
        </form>
    </div>

    <div class="card">
        <h3>This Month (${currentMonth}) — Total:
            <fmt:formatNumber value="${monthlyTotal}" type="currency" currencySymbol="₹"/>
        </h3>
        <c:choose>
            <c:when test="${empty monthlyReport}">
                <p class="muted">No expenses recorded this month.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr><th>Category</th><th>Total Spent</th></tr>
                    <c:forEach var="entry" items="${monthlyReport}">
                        <tr>
                            <td>${entry.key}</td>
                            <td><fmt:formatNumber value="${entry.value}" type="currency" currencySymbol="₹"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <h3>All-Time by Category</h3>
        <c:choose>
            <c:when test="${empty categoryReport}">
                <p class="muted">No expenses recorded yet.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr><th>Category</th><th>Total Spent</th></tr>
                    <c:forEach var="entry" items="${categoryReport}">
                        <tr>
                            <td>${entry.key}</td>
                            <td><fmt:formatNumber value="${entry.value}" type="currency" currencySymbol="₹"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="card">
        <h3>All Expenses</h3>
        <c:choose>
            <c:when test="${empty expenses}">
                <p class="muted">Nothing here yet — add your first expense above.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr><th>Date</th><th>Category</th><th>Description</th><th>Amount</th><th></th></tr>
                    <c:forEach var="e" items="${expenses}">
                        <tr>
                            <td><fmt:formatDate value="${e.expenseDate}" pattern="dd MMM yyyy"/></td>
                            <td>${e.category}</td>
                            <td>${empty e.description ? '—' : e.description}</td>
                            <td><fmt:formatNumber value="${e.amount}" type="currency" currencySymbol="₹"/></td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/app/expenses" style="display:inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="expenseId" value="${e.expenseId}">
                                    <button type="submit" class="btn danger" onclick="return confirm('Delete this expense?');">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

</div>
</body>
</html>
