<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users - Queue Management</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background: #f8f9fa;
            min-height: 100vh;
        }

        .header {
            background: white;
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

        .header-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .btn {
            padding: 0.5rem 1rem;
            border: 1px solid #dee2e6;
            background: white;
            color: #212529;
            font-size: 0.875rem;
            border-radius: 0.25rem;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.2s;
        }

        .btn:hover {
            background: #f8f9fa;
        }

        .btn-primary {
            background: #212529;
            color: white;
            border-color: #212529;
        }

        .btn-primary:hover {
            background: #000;
            border-color: #000;
        }

        .container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .card {
            background: white;
            border: 1px solid #e9ecef;
            border-radius: 0.5rem;
            overflow: hidden;
        }

        .card-header {
            padding: 1.5rem;
            border-bottom: 1px solid #e9ecef;
        }

        .card-header h2 {
            font-size: 1.125rem;
            font-weight: 600;
            color: #212529;
        }

        .card-body {
            padding: 1.5rem;
        }

        .alert {
            padding: 0.75rem 1rem;
            border-radius: 0.25rem;
            margin-bottom: 1.5rem;
            font-size: 0.875rem;
        }

        .alert-success {
            background: #d1e7dd;
            color: #0f5132;
            border: 1px solid #badbcc;
        }

        .alert-error {
            background: #f8d7da;
            color: #842029;
            border: 1px solid #f5c2c7;
        }

        .tabs {
            display: flex;
            gap: 0.5rem;
            margin-bottom: 1.5rem;
            border-bottom: 1px solid #e9ecef;
        }

        .tab {
            padding: 0.75rem 1.5rem;
            background: transparent;
            border: none;
            color: #6c757d;
            cursor: pointer;
            font-size: 0.875rem;
            font-weight: 500;
            border-bottom: 2px solid transparent;
            transition: all 0.2s;
        }

        .tab.active {
            color: #212529;
            border-bottom-color: #212529;
        }

        .tab:hover {
            color: #212529;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: #f8f9fa;
        }

        th {
            padding: 0.75rem 1rem;
            text-align: left;
            font-size: 0.75rem;
            font-weight: 600;
            color: #6c757d;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-bottom: 1px solid #e9ecef;
        }

        td {
            padding: 1rem;
            border-bottom: 1px solid #f1f3f5;
            font-size: 0.875rem;
            color: #212529;
        }

        tr:hover {
            background: #f8f9fa;
        }

        .role-badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: 0.25rem;
            font-size: 0.75rem;
            font-weight: 500;
        }

        .role-citizen {
            background: #e7f5ff;
            color: #1864ab;
        }

        .role-employee {
            background: #fff3bf;
            color: #7d6608;
        }

        .role-admin {
            background: #ffe0e0;
            color: #c41e3a;
        }

        .btn-sm {
            padding: 0.375rem 0.75rem;
            font-size: 0.75rem;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
            border-color: #dc3545;
        }

        .btn-danger:hover {
            background: #bb2d3b;
            border-color: #bb2d3b;
        }

        .btn-info {
            background: #17a2b8;
            color: white;
            border-color: #17a2b8;
        }

        .btn-info:hover {
            background: #138496;
            border-color: #138496;
        }

        .action-buttons {
            display: flex;
            gap: 0.5rem;
        }

        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            align-items: center;
            justify-content: center;
        }

        .modal.active {
            display: flex;
        }

        .modal-content {
            background: white;
            border-radius: 0.5rem;
            width: 90%;
            max-width: 500px;
            padding: 1.5rem;
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .modal-header h3 {
            font-size: 1.125rem;
            font-weight: 600;
            color: #212529;
        }

        .modal-close {
            background: none;
            border: none;
            font-size: 1.5rem;
            color: #6c757d;
            cursor: pointer;
            padding: 0;
            width: 2rem;
            height: 2rem;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .modal-close:hover {
            color: #212529;
        }

        .form-group {
            margin-bottom: 1.25rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #212529;
            font-weight: 500;
            font-size: 0.875rem;
        }

        .form-group select,
        .form-group input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #dee2e6;
            border-radius: 0.25rem;
            font-size: 0.875rem;
            font-family: inherit;
        }

        .form-group select:focus,
        .form-group input:focus {
            outline: none;
            border-color: #495057;
        }

        .hidden {
            display: none;
        }

        .modal-footer {
            display: flex;
            gap: 0.5rem;
            justify-content: flex-end;
            margin-top: 1.5rem;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>User Management</h1>
        <div class="header-actions">
            <a href="<%= request.getContextPath() %>/admin/index.jsp" class="btn">← Back to Dashboard</a>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="post" style="display: inline;">
                <button type="submit" class="btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <div class="card-header">
                <h2>All Users</h2>
            </div>
            <div class="card-body">
                <% String success = request.getParameter("success");
                   String error = request.getParameter("error");
                   if (success != null) { %>
                    <div class="alert alert-success"><%= success %></div>
                <% } if (error != null) { %>
                    <div class="alert alert-error"><%= error %></div>
                <% } %>

                <div class="tabs">
                    <button class="tab active" onclick="showTab('citizens')">Citizens</button>
                    <button class="tab" onclick="showTab('employees')">Employees</button>
                    <button class="tab" onclick="showTab('admins')">Administrators</button>
                </div>

                <!-- Citizens Tab -->
                <div id="citizens-tab" class="tab-content active">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>CIN</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            List<Citizen> citizens = (List<Citizen>) request.getAttribute("citizens");
                            if (citizens != null && !citizens.isEmpty()) {
                                for (Citizen citizen : citizens) { 
                            %>
                            <tr>
                                <td><%= citizen.getId() %></td>
                                <td><%= citizen.getFirstName() %> <%= citizen.getLastName() %></td>
                                <td><%= citizen.getEmail() %></td>
                                <td><%= citizen.getCin() %></td>
                                <td><span class="role-badge role-citizen">Citizen</span></td>
                                <td>
                                    <div class="action-buttons">
                                        <button class="btn btn-sm btn-info" onclick="openChangeRoleModal(<%= citizen.getId() %>, 'citizen', '<%= citizen.getEmail() %>')">Change Role</button>
                                    </div>
                                </td>
                            </tr>
                            <% 
                                }
                            } else { 
                            %>
                            <tr>
                                <td colspan="6" style="text-align: center; color: #6c757d;">No citizens found</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <!-- Employees Tab -->
                <div id="employees-tab" class="tab-content">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Agency ID</th>
                                <th>Service ID</th>
                                <th>Counter</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                            if (employees != null && !employees.isEmpty()) {
                                for (Employee employee : employees) { 
                            %>
                            <tr>
                                <td><%= employee.getId() %></td>
                                <td><%= employee.getFirstName() %> <%= employee.getLastName() %></td>
                                <td><%= employee.getEmail() %></td>
                                <td><%= employee.getAgencyId() %></td>
                                <td><%= employee.getServiceId() %></td>
                                <td><%= employee.getCounterId() != 0 ? employee.getCounterId() : "N/A" %></td>
                                <td><span class="role-badge role-employee">Employee</span></td>
                                <td>
                                    <div class="action-buttons">
                                        <button class="btn btn-sm btn-info" onclick="openChangeRoleModal(<%= employee.getId() %>, 'employee', '<%= employee.getEmail() %>')">Change Role</button>
                                    </div>
                                </td>
                            </tr>
                            <% 
                                }
                            } else { 
                            %>
                            <tr>
                                <td colspan="8" style="text-align: center; color: #6c757d;">No employees found</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <!-- Admins Tab -->
                <div id="admins-tab" class="tab-content">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            List<Administrator> admins = (List<Administrator>) request.getAttribute("admins");
                            if (admins != null && !admins.isEmpty()) {
                                for (Administrator admin : admins) { 
                            %>
                            <tr>
                                <td><%= admin.getId() %></td>
                                <td><%= admin.getFirstName() %> <%= admin.getLastName() %></td>
                                <td><%= admin.getEmail() %></td>
                                <td><span class="role-badge role-admin">Administrator</span></td>
                                <td>
                                    <div class="action-buttons">
                                        <button class="btn btn-sm btn-info" onclick="openChangeRoleModal(<%= admin.getId() %>, 'admin', '<%= admin.getEmail() %>')">Change Role</button>
                                    </div>
                                </td>
                            </tr>
                            <% 
                                }
                            } else { 
                            %>
                            <tr>
                                <td colspan="5" style="text-align: center; color: #6c757d;">No administrators found</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Change Role Modal -->
    <div id="changeRoleModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Change User Role</h3>
                <button class="modal-close" onclick="closeChangeRoleModal()">×</button>
            </div>
            <form action="<%= request.getContextPath() %>/admin/ManageUsersServlet" method="post">
                <input type="hidden" name="action" value="changeRole">
                <input type="hidden" id="userId" name="userId">
                <input type="hidden" id="currentRole" name="currentRole">
                
                <div class="form-group">
                    <label>Email:</label>
                    <input type="text" id="userEmail" readonly>
                </div>

                <div class="form-group">
                    <label for="newRole">New Role:</label>
                    <select id="newRole" name="newRole" required onchange="toggleEmployeeFields()">
                        <option value="">-- Select Role --</option>
                        <option value="citizen">Citizen</option>
                        <option value="employee">Employee</option>
                        <option value="admin">Administrator</option>
                    </select>
                </div>

                <!-- Employee-specific fields (shown only when employee role selected) -->
                <div id="employeeFields" class="hidden">
                    <div class="form-group">
                        <label for="agencyId">Agency ID:</label>
                        <input type="number" id="agencyId" name="agencyId" placeholder="1">
                    </div>

                    <div class="form-group">
                        <label for="serviceId">Service ID:</label>
                        <input type="number" id="serviceId" name="serviceId" placeholder="1">
                    </div>

                    <div class="form-group">
                        <label for="counterId">Counter ID (optional):</label>
                        <input type="number" id="counterId" name="counterId" placeholder="1">
                    </div>
                </div>

                <!-- Citizen-specific field (shown only when citizen role selected) -->
                <div id="citizenFields" class="hidden">
                    <div class="form-group">
                        <label for="cin">CIN (National ID):</label>
                        <input type="text" id="cin" name="cin" placeholder="AB123456">
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn" onclick="closeChangeRoleModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Change Role</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function showTab(tabName) {
            // Hide all tab contents
            document.querySelectorAll('.tab-content').forEach(tab => {
                tab.classList.remove('active');
            });
            
            // Remove active class from all tabs
            document.querySelectorAll('.tab').forEach(tab => {
                tab.classList.remove('active');
            });
            
            // Show selected tab
            document.getElementById(tabName + '-tab').classList.add('active');
            
            // Mark tab as active
            event.target.classList.add('active');
        }

        function openChangeRoleModal(userId, currentRole, email) {
            document.getElementById('userId').value = userId;
            document.getElementById('currentRole').value = currentRole;
            document.getElementById('userEmail').value = email;
            document.getElementById('newRole').value = '';
            document.getElementById('employeeFields').classList.add('hidden');
            document.getElementById('citizenFields').classList.add('hidden');
            document.getElementById('changeRoleModal').classList.add('active');
        }

        function closeChangeRoleModal() {
            document.getElementById('changeRoleModal').classList.remove('active');
        }

        function toggleEmployeeFields() {
            const newRole = document.getElementById('newRole').value;
            const employeeFields = document.getElementById('employeeFields');
            const citizenFields = document.getElementById('citizenFields');
            const agencyId = document.getElementById('agencyId');
            const serviceId = document.getElementById('serviceId');
            const cin = document.getElementById('cin');

            // Hide all role-specific fields
            employeeFields.classList.add('hidden');
            citizenFields.classList.add('hidden');
            agencyId.required = false;
            serviceId.required = false;
            cin.required = false;

            // Show relevant fields
            if (newRole === 'employee') {
                employeeFields.classList.remove('hidden');
                agencyId.required = true;
                serviceId.required = true;
            } else if (newRole === 'citizen') {
                citizenFields.classList.remove('hidden');
                cin.required = true;
            }
        }

        // Close modal when clicking outside
        document.getElementById('changeRoleModal').addEventListener('click', function(e) {
            if (e.target === this) {
                closeChangeRoleModal();
            }
        });
    </script>
</body>
</html>
