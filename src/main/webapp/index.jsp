<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // Check if user is logged in
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    
    // If not logged in, redirect to login page
    if (userEmail == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Queue Management System - Dashboard</title>
<link rel="stylesheet" href="css/dashboard.css">
</head>
<body>
    <div class="navbar">
        <h1>Queue Management System</h1>
        <div class="navbar-right">
            <div class="user-info">
                <p><%= userEmail %></p>
                <p class="role"><%= userRole.toUpperCase() %></p>
            </div>
            <form action="${pageContext.request.contextPath}/LogoutServlet" method="POST" style="display: inline;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <h2>Welcome, <%= userEmail %>!</h2>
        <p>You are logged in as a <strong><%= userRole.toUpperCase() %></strong></p>
        
        <%
            if ("admin".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
            } else if ("employee".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
            } else if ("citizen".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/citizen/index.jsp");
            }
        %>
    </div>
</body>
</html>