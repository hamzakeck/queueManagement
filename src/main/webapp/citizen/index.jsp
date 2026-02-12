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
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Citizen Dashboard</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        background: #f8f9fa;
        color: #212529;
        line-height: 1.6;
    }

    .header {
        background: #fff;
        border-bottom: 1px solid #e9ecef;
        padding: 1rem 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .header h1 {
        font-size: 1.25rem;
        font-weight: 600;
        color: #212529;
    }

    .user-section {
        display: flex;
        align-items: center;
        gap: 1.5rem;
    }

    .user-info {
        text-align: right;
        font-size: 0.875rem;
        color: #6c757d;
    }

    .logout-btn {
        background: transparent;
        border: 1px solid #dee2e6;
        padding: 0.5rem 1rem;
        border-radius: 0.25rem;
        cursor: pointer;
        font-size: 0.875rem;
        color: #495057;
        transition: all 0.2s;
    }

    .logout-btn:hover {
        background: #f8f9fa;
        border-color: #adb5bd;
    }

    .container {
        max-width: 900px;
        margin: 3rem auto;
        padding: 0 1.5rem;
    }

    .welcome {
        margin-bottom: 2.5rem;
    }

    .welcome h2 {
        font-size: 1.75rem;
        font-weight: 600;
        color: #212529;
        margin-bottom: 0.5rem;
    }

    .welcome p {
        color: #6c757d;
        font-size: 0.9375rem;
    }

    .actions {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
        gap: 1rem;
    }

    .action-card {
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        padding: 1.5rem;
        text-decoration: none;
        color: inherit;
        transition: all 0.2s;
        display: flex;
        flex-direction: column;
    }

    .action-card:hover {
        border-color: #495057;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        transform: translateY(-2px);
    }

    .action-card .icon {
        font-size: 2rem;
        margin-bottom: 1rem;
    }

    .action-card h3 {
        font-size: 1.125rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
        color: #212529;
    }

    .action-card p {
        font-size: 0.875rem;
        color: #6c757d;
        margin: 0;
    }

    @media (max-width: 640px) {
        .header {
            flex-direction: column;
            align-items: flex-start;
            gap: 1rem;
        }

        .user-section {
            width: 100%;
            justify-content: space-between;
        }

        .actions {
            grid-template-columns: 1fr;
        }
    }
</style>
</head>
<body>
    <div class="header">
        <h1>Queue Management</h1>
        <div class="user-section">
            <div class="user-info">
                <div><%= userEmail %></div>
                <div style="font-weight: 500; color: #212529;">Citizen</div>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <div class="welcome">
            <h2>Welcome back</h2>
            <p>Manage your queue tickets and appointments</p>
        </div>

        <div class="actions">
            <a href="<%= request.getContextPath() %>/citizen/create-ticket.jsp" class="action-card">
                <h3>New Ticket</h3>
                <p>Create a new service ticket</p>
            </a>

            <a href="<%= request.getContextPath() %>/citizen/track-ticket.jsp" class="action-card">
                <h3>Track Ticket</h3>
                <p>View your ticket status in real-time</p>
            </a>
        </div>
    </div>
</body>
</html>
