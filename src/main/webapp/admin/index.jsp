<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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
    <title>Admin Dashboard</title>
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
            text-decoration: none;
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

        .page-title {
            margin-bottom: 2rem;
        }

        .page-title h2 {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 0.25rem;
        }

        .page-title p {
            color: #6c757d;
            font-size: 0.875rem;
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
        }

        .action-card {
            background: #fff;
            border: 1px solid #e9ecef;
            border-radius: 0.5rem;
            padding: 2rem 1.5rem;
            text-decoration: none;
            color: inherit;
            transition: all 0.2s;
        }

        .action-card:hover {
            border-color: #adb5bd;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transform: translateY(-2px);
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
        }

        @media (max-width: 640px) {
            .dashboard-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Admin Dashboard</h1>
        <div class="user-section">
            <div class="user-info">
                <div><%= userEmail %></div>
            </div>
            <a href="<%=request.getContextPath()%>/LogoutServlet" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="container">
        <div class="page-title">
            <h2>Manage System</h2>
            <p>Configure agencies, services, employees, and view tickets</p>

        <div class="dashboard-grid">
            <a href="<%=request.getContextPath()%>/admin/ManageAgenciesServlet" class="action-card">
                <h3>Manage Agencies</h3>
                <p>Add, edit, or remove agencies</p>
            </a>

            <a href="<%=request.getContextPath()%>/admin/ManageServicesServlet" class="action-card">
                <h3>Manage Services</h3>
                <p>Configure available services</p>
            </a>

            <a href="<%=request.getContextPath()%>/admin/ManageEmployeesServlet" class="action-card">
                <h3>Manage Employees</h3>
                <p>Add and manage staff members</p>
            </a>

            <a href="<%=request.getContextPath()%>/admin/ViewAllTicketsServlet" class="action-card">
                <h3>All Tickets</h3>
                <p>View all system tickets</p>
            </a>
        </div>
    </div>
</body>
</html>