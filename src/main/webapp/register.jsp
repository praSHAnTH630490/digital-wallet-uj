<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register — Digital Wallet</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container" style="max-width:420px; margin-top:80px;">
    <div class="card">
        <h1>Create Account</h1>
        <p class="muted">Register to get a wallet with zero balance to start.</p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <label>Full Name</label>
            <input type="text" name="name" required autofocus>

            <label>Email</label>
            <input type="email" name="email" required>

            <label>Password (min 6 characters)</label>
            <input type="password" name="password" minlength="6" required>

            <button type="submit">Register</button>
        </form>

        <p class="muted" style="margin-top:18px;">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a>
        </p>
    </div>
</div>
</body>
</html>
