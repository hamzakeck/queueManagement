<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Agency" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Agencies</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css">
</head>
<body>
    <header class="navbar">
        <div class="container">
            <div class="nav-brand">Manage Agencies</div>
            <nav>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="btn btn-secondary">Back to Dashboard</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="dashboard-header">
            <h1>Manage Agencies</h1>
            <button onclick="showAddForm()" class="btn btn-primary">Add New Agency</button>
        </div>

        <% String success = request.getParameter("success");
           String error = request.getParameter("error");
           if (success != null) { %>
            <div class="alert alert-success">
                <%= success.equals("added") ? "Agency added successfully!" : 
                    success.equals("updated") ? "Agency updated successfully!" : 
                    "Agency deleted successfully!" %>
            </div>
        <% } else if (error != null) { %>
            <div class="alert alert-danger">Error: <%= error %></div>
        <% } %>

        <div id="addForm" style="display:none; margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
            <h3>Add New Agency</h3>
            <form method="post" action="<%=request.getContextPath()%>/admin/ManageAgenciesServlet">
                <input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Address:</label>
                    <input type="text" name="address" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>City:</label>
                    <input type="text" name="city" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary">Add Agency</button>
                <button type="button" onclick="hideAddForm()" class="btn btn-secondary">Cancel</button>
            </form>
        </div>

        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Address</th>
                    <th>City</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% List<Agency> agencies = (List<Agency>) request.getAttribute("agencies");
                   if (agencies != null && !agencies.isEmpty()) {
                       for (Agency agency : agencies) { %>
                <tr>
                    <td><%=agency.getId()%></td>
                    <td><%=agency.getName()%></td>
                    <td><%=agency.getAddress()%></td>
                    <td><%=agency.getCity()%></td>
                    <td><%=agency.isActive() ? "Yes" : "No"%></td>
                    <td>
                        <button onclick="editAgency(<%=agency.getId()%>, '<%=agency.getName()%>', '<%=agency.getAddress()%>', '<%=agency.getCity()%>', <%=agency.isActive()%>)" class="btn btn-sm">Edit</button>
                        <form method="post" style="display:inline;" onsubmit="return confirm('Are you sure?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%=agency.getId()%>">
                            <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                        </form>
                    </td>
                </tr>
                <% } } else { %>
                <tr><td colspan="6">No agencies found</td></tr>
                <% } %>
            </tbody>
        </table>

        <div id="editFormModal" style="display:none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); padding: 30px; border: 1px solid #ddd; border-radius: 8px; background: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); z-index: 1000;">
            <h3>Edit Agency</h3>
            <form method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editId">
                <div class="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" id="editName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Address:</label>
                    <input type="text" name="address" id="editAddress" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>City:</label>
                    <input type="text" name="city" id="editCity" class="form-control" required>
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
        function editAgency(id, name, address, city, active) {
            document.getElementById('editId').value = id;
            document.getElementById('editName').value = name;
            document.getElementById('editAddress').value = address;
            document.getElementById('editCity').value = city;
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
