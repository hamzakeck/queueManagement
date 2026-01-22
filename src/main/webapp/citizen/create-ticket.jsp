<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Agency" %>
<%@ page import="models.Service" %>
<%@ page import="dao.AgencyDAO" %>
<%@ page import="dao.ServiceDAO" %>
<%@ page import="dao.DAOFactory" %>
<%
    // Check if user is logged in and is citizen
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer userId = (Integer) session.getAttribute("userId");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Get agencies and services from database
    AgencyDAO agencyDAO = DAOFactory.getInstance().getAgencyDAO();
    ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
    
    List<Agency> agencies = agencyDAO.findAll();
    List<Service> services = serviceDAO.findAllActive();
    
    // Get error and success messages from session
    String errorMessage = (String) session.getAttribute("errorMessage");
    String successMessage = (String) session.getAttribute("successMessage");
    
    // Clear messages after retrieval
    if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    }
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Create New Ticket - Queue Management</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        background-color: #f5f5f5;
        color: #333;
    }

    header {
        background: #fff;
        border-bottom: 1px solid #e9ecef;
        padding: 1rem 0;
    }

    .header-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 20px;
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

    .container {
        max-width: 700px;
        margin: 60px auto;
        padding: 0 20px;
    }

    .form-card {
        background: white;
        padding: 50px;
        border-radius: 8px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .form-card h2 {
        color: #333;
        margin-bottom: 8px;
        font-size: 1.75rem;
    }

    .form-card p {
        color: #666;
        margin-bottom: 35px;
        font-size: 1rem;
    }

    .form-group {
        margin-bottom: 30px;
    }

    label {
        display: block;
        margin-bottom: 10px;
        color: #333;
        font-weight: 500;
        font-size: 0.95rem;
    }

    select, input {
        width: 100%;
        padding: 14px;
        border: 1px solid #ced4da;
        border-radius: 4px;
        font-size: 1rem;
        transition: border-color 0.2s;
    }

    select:focus, input:focus {
        outline: none;
        border-color: #80bdff;
        box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
    }

    .submit-btn {
        width: 100%;
        background: #007bff;
        color: white;
        padding: 16px;
        border: none;
        border-radius: 6px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: background 0.2s;
        margin-top: 10px;
    }

    .submit-btn:hover {
        background: #0056b3;
    }

    .alert {
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        font-size: 14px;
    }

    .alert-error {
        background-color: #fee;
        color: #c33;
        border: 1px solid #fcc;
    }

    .alert-success {
        background-color: #efe;
        color: #3c3;
        border: 1px solid #cfc;
    }

    .info-box {
        background-color: #f8f9fa;
        border-left: 3px solid #007bff;
        padding: 20px;
        margin-bottom: 35px;
        border-radius: 4px;
    }

    .info-box h4 {
        color: #007bff;
        margin-bottom: 10px;
        font-size: 1rem;
    }

    .info-box p {
        color: #555;
        font-size: 0.9rem;
        margin: 0;
        line-height: 1.8;
    }
</style>
</head>
<body>
    <header>
        <div class="header-container">
            <div class="logo">Create New Ticket</div>
            <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="nav-link">← Back to Dashboard</a>
        </div>
    </header>

    <div class="container">
        <div class="form-card">
            <h2>Request a Service Ticket</h2>
            <p>Select an agency and service to get your ticket number</p>

            <% if (errorMessage != null) { %>
                <div class="alert alert-error">
                    <%= errorMessage %>
                </div>
            <% } %>

            <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    <%= successMessage %>
                </div>
            <% } %>

            <div class="info-box">
                <h4>How it works</h4>
                <p>1. Choose the agency location<br>
                   2. Select the service you need<br>
                   3. Click "Get Ticket" to receive your queue number</p>
            </div>

            <form action="<%= request.getContextPath() %>/citizen/CreateTicketServlet" method="POST">
                <div class="form-group">
                    <label for="agencyId">Select Agency Location *</label>
                    <select id="agencyId" name="agencyId" required>
                        <option value="">-- Choose an agency --</option>
                        <% for (Agency agency : agencies) { %>
                            <option value="<%= agency.getId() %>">
                                <%= agency.getName() %> - <%= agency.getCity() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="serviceId">Select Service *</label>
                    <select id="serviceId" name="serviceId" required>
                        <option value="">-- Choose a service --</option>
                        <% for (Service service : services) { %>
                            <option value="<%= service.getId() %>">
                                <%= service.getName() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <button type="submit" class="submit-btn">Get My Ticket</button>
            </form>
        </div>
    </div>
</body>
</html>
