<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard — Digital Wallet</title>
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

<div class="container">
    <div class="card">
        <h2>Welcome, ${sessionScope.userName}</h2>
        <p class="muted">Wallet balance</p>
        <div class="balance">
            <fmt:formatNumber value="${wallet.balance}" type="currency" currencySymbol="₹" />
        </div>

        <div class="grid-actions">
            <a href="${pageContext.request.contextPath}/app/add-money">➕ Add Money</a>
            <a href="${pageContext.request.contextPath}/app/transfer">🔁 Send Money</a>
            <a href="${pageContext.request.contextPath}/app/expenses">🧾 Expenses</a>
        </div>
    </div>

    <div class="card">
        <h3>This month's spending</h3>
        <p class="balance" style="color:#dc2626; font-size:26px;">
            <fmt:formatNumber value="${monthlyExpenseTotal}" type="currency" currencySymbol="₹" />
        </p>
        <a href="${pageContext.request.contextPath}/app/expenses" class="muted">View category breakdown →</a>
    </div>
</div>
</body>
</html>
