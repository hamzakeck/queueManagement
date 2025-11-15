<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Employee, models.Agency, models.Service" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Employees</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css">
</head>
<body>
    <header class="navbar">
        <div class="container">
            <div class="nav-brand">Manage Employees</div>
            <nav>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="btn btn-secondary">Back to Dashboard</a>
            </nav>
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
