<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // Check if user is logged in and is admin
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    
    if (userEmail == null || !"admin".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Queue Management System - Admin Dashboard</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f5f7fa;
    }

    .navbar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 20px 40px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .navbar h1 {
        font-size: 24px;
    }

    .navbar-right {
        display: flex;
        align-items: center;
        gap: 30px;
    }

    .user-info {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
    }

    .user-info p {
        font-size: 14px;
        opacity: 0.9;
    }

    .user-info .role {
        font-weight: 600;
        font-size: 16px;
    }

    .logout-btn {
        background-color: rgba(255, 255, 255, 0.2);
        color: white;
        border: 2px solid white;
        padding: 8px 20px;
        border-radius: 5px;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 14px;
        font-weight: 600;
    }

    .logout-btn:hover {
        background-color: white;
        color: #667eea;
    }

    .container {
        max-width: 1200px;
        margin: 40px auto;
        padding: 0 20px;
    }

    .container h2 {
        color: #333;
        margin-bottom: 20px;
    }

    @media (max-width: 768px) {
        .navbar {
            flex-direction: column;
            gap: 15px;
        }

        .navbar-right {
            width: 100%;
            justify-content: space-between;
        }

        .user-info {
            align-items: flex-start;
        }
    }
</style>
</head>
<body>
    <div class="navbar">
        <h1>🔗 Queue Management System - Admin</h1>
        <div class="navbar-right">
            <div class="user-info">
                <p><%= userEmail %></p>
                <p class="role"><%= userRole.toUpperCase() %></p>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST" style="display: inline;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <h2>Welcome to Admin Dashboard</h2>
        <!-- Admin content will be added here -->
    </div>
</body>
</html>
