<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Service" %>
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
<<<<<<< HEAD
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Services</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: #f5f5f5;
            color: #333;
        }
        header {
            background: #fff;
            border-bottom: 1px solid #e9ecef;
            padding: 1rem 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .logo {
            font-size: 1.5rem;
            font-weight: 600;
            color: #333;
        }
        .nav-link {
            color: #666;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            transition: background-color 0.2s;
        }
        .nav-link:hover {
            background-color: #f8f9fa;
        }
        main {
            padding: 2rem 0;
        }
        .dashboard-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        h1 {
            font-size: 2rem;
            font-weight: 600;
        }
        .btn {
            padding: 0.6rem 1.2rem;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.95rem;
            text-decoration: none;
            display: inline-block;
            transition: all 0.2s;
        }
        .btn-primary {
            background: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background: #0056b3;
        }
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background: #545b62;
        }
        .btn-sm {
            padding: 0.4rem 0.8rem;
            font-size: 0.85rem;
        }
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        .btn-danger:hover {
            background: #c82333;
        }
        .alert {
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1.5rem;
        }
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-danger {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .form-group {
            margin-bottom: 1.2rem;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }
        .form-control {
            width: 100%;
            padding: 0.6rem;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 1rem;
        }
        .form-control:focus {
            outline: none;
            border-color: #80bdff;
            box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
        }
        textarea.form-control {
            resize: vertical;
        }
        .data-table {
            width: 100%;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .data-table thead {
            background: #f8f9fa;
        }
        .data-table th,
        .data-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }
        .data-table th {
            font-weight: 600;
            color: #495057;
        }
        .data-table tbody tr:hover {
            background: #f8f9fa;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <div class="header-content">
                <div class="logo">Manage Services</div>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="nav-link">← Back to Dashboard</a>
            </div>
=======
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Manage Services</title>
<style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8f9fa; color: #212529; line-height: 1.6; }
    .header { background: #fff; border-bottom: 1px solid #e9ecef; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
    .header h1 { font-size: 1.25rem; font-weight: 600; color: #212529; }
    .user-section { display: flex; align-items: center; gap: 1.5rem; }
    .user-info { text-align: right; }
    .user-info .email { font-size: 0.875rem; color: #6c757d; }
    .user-info .role { font-size: 0.75rem; font-weight: 500; color: #fff; background: #dc3545; padding: 0.125rem 0.5rem; border-radius: 0.25rem; display: inline-block; margin-top: 0.25rem; }
    .logout-btn { background: #dc3545; color: white; border: none; padding: 0.5rem 1rem; border-radius: 0.375rem; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: background 0.2s; }
    .logout-btn:hover { background: #c82333; }
    .container { max-width: 1400px; margin: 2rem auto; padding: 0 1rem; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
    .page-header h2 { font-size: 1.5rem; font-weight: 600; color: #212529; }
    .btn { padding: 0.5rem 1rem; border: none; border-radius: 0.375rem; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.2s; text-decoration: none; display: inline-block; }
    .btn-primary { background: #007bff; color: white; }
    .btn-primary:hover { background: #0056b3; }
    .btn-secondary { background: #6c757d; color: white; }
    .btn-secondary:hover { background: #545b62; }
    .btn-danger { background: #dc3545; color: white; }
    .btn-danger:hover { background: #c82333; }
    .alert { padding: 1rem; border-radius: 0.375rem; margin-bottom: 1rem; }
    .alert-success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; }
    .alert-danger { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; margin-bottom: 0.25rem; font-weight: 500; color: #495057; }
    .form-control { width: 100%; padding: 0.5rem; border: 1px solid #ced4da; border-radius: 0.375rem; font-size: 0.875rem; }
    .data-table { width: 100%; background: #fff; border-radius: 0.5rem; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .data-table thead { background: #f8f9fa; }
    .data-table th { padding: 1rem; text-align: left; font-weight: 600; color: #495057; border-bottom: 2px solid #dee2e6; }
    .data-table td { padding: 1rem; border-bottom: 1px solid #dee2e6; }
    .data-table tbody tr:hover { background: #f8f9fa; }
    .badge { padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; font-weight: 500; }
    .badge-active { background: #d4edda; color: #155724; }
    .badge-inactive { background: #f8d7da; color: #721c24; }
    textarea.form-control { resize: vertical; }
</style>
</head>
<body>
    <div class="header">
        <h1>Manage Services</h1>
        <div class="user-section">
            <div class="user-info">
                <div class="email"><%=userEmail%></div>
                <div class="role">Admin</div>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST" style="margin: 0;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
>>>>>>> 92e8e02bba67ed8989d3c6f914288999ed303c63
        </div>
    </div>

    <div class="container">
        <div class="page-header">
            <h2>Manage Services</h2>
            <button onclick="showAddForm()" class="btn btn-primary">Add New Service</button>
        </div>

        <% String success = request.getParameter("success");
           String error = request.getParameter("error");
           if (success != null) { %>
            <div class="alert alert-success">
                <%= success.equals("added") ? "Service added successfully!" : 
                    success.equals("updated") ? "Service updated successfully!" : 
                    "Service deleted successfully!" %>
            </div>
        <% } else if (error != null) { %>
            <div class="alert alert-danger">Error: <%= error %></div>
        <% } %>

        <div id="addForm" style="display:none; margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
            <h3>Add New Service</h3>
            <form method="post" action="<%=request.getContextPath()%>/admin/ManageServicesServlet">
                <input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Description:</label>
                    <textarea name="description" class="form-control" rows="3" required></textarea>
                </div>
                <div class="form-group">
                    <label>Estimated Time (minutes):</label>
                    <input type="number" name="estimatedTime" class="form-control" min="1" required>
                </div>
                <button type="submit" class="btn btn-primary">Add Service</button>
                <button type="button" onclick="hideAddForm()" class="btn btn-secondary">Cancel</button>
            </form>
        </div>

        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Est. Time (min)</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% List<Service> services = (List<Service>) request.getAttribute("services");
                   if (services != null && !services.isEmpty()) {
                       for (Service service : services) { %>
                <tr>
                    <td><%=service.getId()%></td>
                    <td><%=service.getName()%></td>
                    <td><%=service.getDescription()%></td>
                    <td><%=service.getEstimatedTime()%></td>
                    <td><%=service.isActive() ? "Yes" : "No"%></td>
                    <td>
                        <button onclick="editService(<%=service.getId()%>, '<%=service.getName().replace("'", "\\'")%>', '<%=service.getDescription().replace("'", "\\'")%>', <%=service.getEstimatedTime()%>, <%=service.isActive()%>)" class="btn btn-sm">Edit</button>
                        <form method="post" style="display:inline;" onsubmit="return confirm('Are you sure?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%=service.getId()%>">
                            <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                        </form>
                    </td>
                </tr>
                <% } } else { %>
                <tr><td colspan="6">No services found</td></tr>
                <% } %>
            </tbody>
        </table>

        <div id="editFormModal" style="display:none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); padding: 30px; border: 1px solid #ddd; border-radius: 8px; background: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); z-index: 1000; max-width: 500px; width: 90%;">
            <h3>Edit Service</h3>
            <form method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editId">
                <div class="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" id="editName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Description:</label>
                    <textarea name="description" id="editDescription" class="form-control" rows="3" required></textarea>
                </div>
                <div class="form-group">
                    <label>Estimated Time (minutes):</label>
                    <input type="number" name="estimatedTime" id="editEstimatedTime" class="form-control" min="1" required>
                </div>
                <div class="form-group">
                    <label><input type="checkbox" name="active" id="editActive"> Active</label>
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
                <button type="button" onclick="hideEditForm()" class="btn btn-secondary">Cancel</button>
            </form>
        </div>
        <div id="modalOverlay" style="display:none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999;" onclick="hideEditForm()"></div>
    </div>

    <script>
        function showAddForm() {
            document.getElementById('addForm').style.display = 'block';
        }
        function hideAddForm() {
            document.getElementById('addForm').style.display = 'none';
        }
        function editService(id, name, description, estimatedTime, active) {
            document.getElementById('editId').value = id;
            document.getElementById('editName').value = name;
            document.getElementById('editDescription').value = description;
            document.getElementById('editEstimatedTime').value = estimatedTime;
            document.getElementById('editActive').checked = active;
            document.getElementById('editFormModal').style.display = 'block';
            document.getElementById('modalOverlay').style.display = 'block';
        }
        function hideEditForm() {
            document.getElementById('editFormModal').style.display = 'none';
            document.getElementById('modalOverlay').style.display = 'none';
        }
    </script>
</body>
</html>
