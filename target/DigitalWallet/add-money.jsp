<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Money — Digital Wallet</title>
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

<div class="container" style="max-width:460px;">
    <div class="card">
        <h2>Add Money to Wallet</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/app/add-money">
            <label>Amount (₹)</label>
            <input type="number" step="0.01" min="0.01" name="amount" required autofocus>

            <button type="submit">Add Money</button>
        </form>
    </div>
</div>
</body>
</html>
