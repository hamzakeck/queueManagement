<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Employee, models.Agency, models.Service" %>
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
<title>Manage Employees</title>
<style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8f9fa; color: #212529; line-height: 1.6; }
    .header { background: #fff; border-bottom: 1px solid #e9ecef; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
    .header-left { display: flex; align-items: center; gap: 1rem; }
    .header-left h1 { font-size: 1.25rem; font-weight: 600; color: #212529; }
    .user-section { display: flex; align-items: center; gap: 1.5rem; }
    .user-info { text-align: right; }
    .user-info .email { font-size: 0.875rem; color: #6c757d; }
    .user-info .role { font-size: 0.75rem; font-weight: 500; color: #fff; background: #dc3545; padding: 0.125rem 0.5rem; border-radius: 0.25rem; display: inline-block; margin-top: 0.25rem; }
    .logout-btn { background: #dc3545; color: white; border: none; padding: 0.5rem 1rem; border-radius: 0.375rem; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: background 0.2s; }
    .back-btn { background: #6c757d; color: #fff; text-decoration: none; padding: 0.5rem 0.75rem; border-radius: 0.375rem; font-size: 0.75rem; font-weight: 500; display: inline-block; transition: background 0.2s; }
    .back-btn:hover { background: #545b62; }
    .logout-btn:hover { background: #c82333; }
    .back-btn { background: transparent; border: 1px solid #dee2e6; padding: 0.5rem 1rem; border-radius: 0.375rem; cursor: pointer; font-size: 0.875rem; color: #495057; text-decoration: none; transition: all 0.2s; display: inline-block; }
    .back-btn:hover { background: #f8f9fa; border-color: #adb5bd; }
    .header-left { display: flex; align-items: center; gap: 1rem; }
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
</style>
</head>
<body>
    <div class="header">
        <div class="header-left">
            <a href="<%= request.getContextPath() %>/admin/index.jsp" class="back-btn">← Dashboard</a>
            <h1>Manage Employees</h1>
        </div>
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
        <div class="page-header">
            <h2>Manage Employees</h2>
            <p style="color: #6c757d; font-size: 0.875rem; margin-top: 0.5rem;">To add employees, go to <a href="<%=request.getContextPath()%>/admin/ManageUsersServlet" style="color: #007bff; text-decoration: none;">Manage Users</a> and promote a citizen to employee role.</p>
        </div>

        <% String success = request.getParameter("success");
           String error = request.getParameter("error");
           if (success != null) { %>
            <div class="alert alert-success">
                <%= success.equals("updated") ? "Employee updated successfully!" : 
                    "Employee deleted successfully!" %>
            </div>
        <% } else if (error != null) { %>
            <div class="alert alert-danger">Error: <%= error %></div>
        <% } %>

        <% List<Agency> agencies = (List<Agency>) request.getAttribute("agencies");
           List<Service> services = (List<Service>) request.getAttribute("services"); %>

        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Agency ID</th>
                    <th>Service ID</th>
                    <th>Counter</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                   if (employees != null && !employees.isEmpty()) {
                       for (Employee employee : employees) { %>
                <tr>
                    <td><%=employee.getId()%></td>
                    <td><%=employee.getFirstName()%> <%=employee.getLastName()%></td>
                    <td><%=employee.getEmail()%></td>
                    <td><%=employee.getAgencyId()%></td>
                    <td><%=employee.getServiceId()%></td>
                    <td><%=employee.getCounterId()%></td>
                    <td>
                        <button onclick="editEmployee(<%=employee.getId()%>, '<%=employee.getFirstName().replace("'", "\\'")%>', '<%=employee.getLastName().replace("'", "\\'")%>', '<%=employee.getEmail()%>', <%=employee.getAgencyId()%>, <%=employee.getServiceId()%>, <%=employee.getCounterId()%>)" class="btn btn-sm">Edit</button>
                        <form method="post" style="display:inline;" onsubmit="return confirm('Are you sure?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%=employee.getId()%>">
                            <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                        </form>
                    </td>
                </tr>
                <% } } else { %>
                <tr><td colspan="7">No employees found</td></tr>
                <% } %>
            </tbody>
        </table>

        <div id="editFormModal" style="display:none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); padding: 30px; border: 1px solid #ddd; border-radius: 8px; background: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); z-index: 1000; max-width: 500px; width: 90%; max-height: 90vh; overflow-y: auto;">
            <h3>Edit Employee</h3>
            <form method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editId">
                <div class="form-group">
                    <label>First Name:</label>
                    <input type="text" name="firstName" id="editFirstName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Last Name:</label>
                    <input type="text" name="lastName" id="editLastName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" id="editEmail" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Password (leave blank to keep current):</label>
                    <input type="password" name="password" id="editPassword" class="form-control">
                </div>
                <div class="form-group">
                    <label>Agency:</label>
                    <select name="agencyId" id="editAgencyId" class="form-control" required>
                        <% if (agencies != null) {
                            for (Agency agency : agencies) { %>
                        <option value="<%=agency.getId()%>"><%=agency.getName()%></option>
                        <% } } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Service:</label>
                    <select name="serviceId" id="editServiceId" class="form-control" required>
                        <% if (services != null) {
                            for (Service service : services) { %>
                        <option value="<%=service.getId()%>"><%=service.getName()%></option>
                        <% } } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Counter ID:</label>
                    <input type="number" name="counterId" id="editCounterId" class="form-control" min="1" required>
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
                <button type="button" onclick="hideEditForm()" class="btn btn-secondary">Cancel</button>
            </form>
        </div>
        <div id="modalOverlay" style="display:none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999;" onclick="hideEditForm()"></div>
    </div>

    <script>
        function editEmployee(id, firstName, lastName, email, agencyId, serviceId, counterId) {
            document.getElementById('editId').value = id;
            document.getElementById('editFirstName').value = firstName;
            document.getElementById('editLastName').value = lastName;
            document.getElementById('editEmail').value = email;
            document.getElementById('editAgencyId').value = agencyId;
            document.getElementById('editServiceId').value = serviceId;
            document.getElementById('editCounterId').value = counterId;
            document.getElementById('editPassword').value = '';
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
