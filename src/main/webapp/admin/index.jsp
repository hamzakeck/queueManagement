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
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif; background: #f8f9fa; color: #212529; line-height: 1.6; }
        .header { background: #fff; border-bottom: 1px solid #e9ecef; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
        .header h1 { font-size: 1.25rem; font-weight: 600; color: #212529; }
        .user-section { display: flex; align-items: center; gap: 1.5rem; }
        .user-info { text-align: right; }
        .user-info .email { font-size: 0.875rem; color: #6c757d; }
        .user-info .role { font-size: 0.75rem; font-weight: 500; color: #fff; background: #dc3545; padding: 0.125rem 0.5rem; border-radius: 0.25rem; display: inline-block; margin-top: 0.25rem; }
        .logout-btn { background: #dc3545; color: white; border: none; padding: 0.5rem 1rem; border-radius: 0.375rem; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: background 0.2s; }
        .logout-btn:hover { background: #c82333; }
        .container { max-width: 1200px; margin: 2rem auto; padding: 0 1rem; }
        .page-title { font-size: 1.75rem; font-weight: 600; color: #212529; margin-bottom: 0.5rem; }
        .page-subtitle { color: #6c757d; margin-bottom: 2rem; }
        .dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem; }
        .action-card { background: #fff; border: 1px solid #e9ecef; border-radius: 0.5rem; padding: 1.5rem; text-decoration: none; color: inherit; transition: all 0.2s; }
        .action-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-color: #007bff; }
        .action-card h3 { font-size: 1.125rem; font-weight: 600; color: #007bff; margin-bottom: 0.5rem; }
        .action-card p { color: #6c757d; font-size: 0.875rem; }
    </style>
    <script>
      // Prevent form resubmission warning on back/refresh
      if (window.history.replaceState) {
        window.history.replaceState(null, null, window.location.href);
      }
    </script>
</head>
<body>
    <div class="header">
        <h1>Admin Dashboard</h1>
        <div class="user-section">
            <div class="user-info">
                <div class="email"><%=userEmail%></div>
                <div class="role">Admin</div>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST" style="margin: 0;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <h2 class="page-title">Welcome, Administrator</h2>
        <p class="page-subtitle">Manage your queue management system</p>

        <div class="dashboard-grid">
            <a href="<%=request.getContextPath()%>/admin/ManageUsersServlet" class="action-card">
                <h3>Manage Users</h3>
                <p>View all users and change roles</p>
            </a>

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