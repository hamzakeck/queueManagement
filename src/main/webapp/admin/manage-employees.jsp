<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Employee, models.Agency, models.Service" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Employees</title>
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
        select.form-control {
            cursor: pointer;
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
                <div class="logo">Manage Employees</div>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="nav-link">← Back to Dashboard</a>
            </div>
        </div>
    </header>

    <main class="container">
        <div class="dashboard-header">
            <h1>Manage Employees</h1>
            <button onclick="showAddForm()" class="btn btn-primary">Add New Employee</button>
        </div>

        <% String success = request.getParameter("success");
           String error = request.getParameter("error");
           if (success != null) { %>
            <div class="alert alert-success">
                <%= success.equals("added") ? "Employee added successfully!" : 
                    success.equals("updated") ? "Employee updated successfully!" : 
                    "Employee deleted successfully!" %>
            </div>
        <% } else if (error != null) { %>
            <div class="alert alert-danger">Error: <%= error %></div>
        <% } %>

        <% List<Agency> agencies = (List<Agency>) request.getAttribute("agencies");
           List<Service> services = (List<Service>) request.getAttribute("services"); %>

        <div id="addForm" style="display:none; margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
            <h3>Add New Employee</h3>
            <form method="post" action="<%=request.getContextPath()%>/admin/ManageEmployeesServlet">
                <input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label>First Name:</label>
                    <input type="text" name="firstName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Last Name:</label>
                    <input type="text" name="lastName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Password:</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Agency:</label>
                    <select name="agencyId" class="form-control" required>
                        <option value="">Select Agency</option>
                        <% if (agencies != null) {
                            for (Agency agency : agencies) { %>
                        <option value="<%=agency.getId()%>"><%=agency.getName()%></option>
                        <% } } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Service:</label>
                    <select name="serviceId" class="form-control" required>
                        <option value="">Select Service</option>
                        <% if (services != null) {
                            for (Service service : services) { %>
                        <option value="<%=service.getId()%>"><%=service.getName()%></option>
                        <% } } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Counter ID:</label>
                    <input type="number" name="counterId" class="form-control" min="1" required>
                </div>
                <button type="submit" class="btn btn-primary">Add Employee</button>
                <button type="button" onclick="hideAddForm()" class="btn btn-secondary">Cancel</button>
            </form>
        </div>

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
    </main>

    <script>
        function showAddForm() {
            document.getElementById('addForm').style.display = 'block';
        }
        function hideAddForm() {
            document.getElementById('addForm').style.display = 'none';
        }
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
