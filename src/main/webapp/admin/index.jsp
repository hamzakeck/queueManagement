<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Queue Management</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css">
</head>
<body>
    <% 
    if (session.getAttribute("adminId") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
        return;
    }
    String adminEmail = (String) session.getAttribute("adminEmail");
    %>
    
    <header class="navbar">
        <div class="container">
            <div class="nav-brand">Queue Management - Admin</div>
            <nav>
                <span class="nav-user"><%=adminEmail%></span>
                <a href="<%=request.getContextPath()%>/LogoutServlet" class="btn btn-secondary">Logout</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="dashboard-header">
            <h1>Admin Dashboard</h1>
            <p>Manage your queue system</p>
        </div>

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
    </main>
</body>
</html>