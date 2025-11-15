<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Service" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Services</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css">
</head>
<body>
    <header class="navbar">
        <div class="container">
            <div class="nav-brand">Manage Services</div>
            <nav>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="btn btn-secondary">Back to Dashboard</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="dashboard-header">
            <h1>Manage Services</h1>
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
    </main>

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
