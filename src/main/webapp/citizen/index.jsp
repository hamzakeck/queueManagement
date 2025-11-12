<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // Check if user is logged in and is citizen
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Queue Management System - Citizen Dashboard</title>
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
        <h1>🔗 Queue Management System - Citizen</h1>
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
        <h2>Welcome, <%= session.getAttribute("userName") != null ? session.getAttribute("userName") : userEmail %>!</h2>
        
        <div class="dashboard-cards">
            <div class="card">
                <div class="card-icon">🎫</div>
                <h3>Create New Ticket</h3>
                <p>Request a new service ticket</p>
                <a href="<%= request.getContextPath() %>/citizen/create-ticket.jsp" class="card-btn">Create Ticket</a>
            </div>

            <div class="card">
                <div class="card-icon">📍</div>
                <h3>Track My Ticket</h3>
                <p>Check your ticket status</p>
                <a href="<%= request.getContextPath() %>/citizen/track-ticket.jsp" class="card-btn">Track Ticket</a>
            </div>

            <div class="card">
                <div class="card-icon">📜</div>
                <h3>Ticket History</h3>
                <p>View past tickets</p>
                <a href="<%= request.getContextPath() %>/citizen/history.jsp" class="card-btn">View History</a>
            </div>
        </div>
    </div>

    <style>
        .dashboard-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }

        .card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
            transition: transform 0.3s, box-shadow 0.3s;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.3);
        }

        .card-icon {
            font-size: 48px;
            margin-bottom: 15px;
        }

        .card h3 {
            color: #333;
            margin-bottom: 10px;
            font-size: 20px;
        }

        .card p {
            color: #666;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .card-btn {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 30px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
        }

        .card-btn:hover {
            opacity: 0.9;
            transform: scale(1.05);
        }
    </style>
</body>
</html>
