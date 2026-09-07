<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Transaction History — Digital Wallet</title>
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

<div class="container" style="max-width:700px;">
    <div class="card">
        <h2>Transaction History</h2>

        <c:choose>
            <c:when test="${empty transactions}">
                <p class="muted">No transactions yet.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Date</th>
                        <th>Type</th>
                        <th>Counterparty</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                    <c:forEach var="t" items="${transactions}">
                        <tr>
                            <td><fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${t.transactionType == 'DEPOSIT'}"><span class="tag-deposit">Deposit</span></c:when>
                                    <c:when test="${t.transactionType == 'TRANSFER'}"><span class="tag-transfer">Sent</span></c:when>
                                    <c:when test="${t.transactionType == 'RECEIVED'}"><span class="tag-received">Received</span></c:when>
                                    <c:otherwise>${t.transactionType}</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${empty t.counterpartyName ? '—' : t.counterpartyName}</td>
                            <td><fmt:formatNumber value="${t.amount}" type="currency" currencySymbol="₹"/></td>
                            <td>${t.status}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
