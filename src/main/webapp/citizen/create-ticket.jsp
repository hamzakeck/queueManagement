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
    
    String error = request.getParameter("error");
    String success = request.getParameter("success");
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
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f5f7fa;
    }

    .navbar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 20px 40px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .navbar h1 {
        font-size: 24px;
    }

    .back-btn {
        background-color: rgba(255, 255, 255, 0.2);
        color: white;
        border: 2px solid white;
        padding: 8px 20px;
        border-radius: 5px;
        cursor: pointer;
        text-decoration: none;
        transition: all 0.3s;
        font-size: 14px;
        font-weight: 600;
        display: inline-block;
    }

    .back-btn:hover {
        background-color: white;
        color: #667eea;
    }

    .container {
        max-width: 600px;
        margin: 40px auto;
        padding: 0 20px;
    }

    .form-card {
        background: white;
        padding: 40px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .form-card h2 {
        color: #333;
        margin-bottom: 10px;
    }

    .form-card p {
        color: #666;
        margin-bottom: 30px;
    }

    .form-group {
        margin-bottom: 25px;
    }

    label {
        display: block;
        margin-bottom: 8px;
        color: #333;
        font-weight: 600;
        font-size: 14px;
    }

    select, input {
        width: 100%;
        padding: 12px;
        border: 2px solid #e1e8ed;
        border-radius: 5px;
        font-size: 14px;
        transition: border-color 0.3s;
    }

    select:focus, input:focus {
        outline: none;
        border-color: #667eea;
    }

    .submit-btn {
        width: 100%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 15px;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }

    .submit-btn:hover {
        opacity: 0.9;
        transform: translateY(-2px);
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
        background-color: #f0f4ff;
        border-left: 4px solid #667eea;
        padding: 15px;
        margin-bottom: 20px;
        border-radius: 5px;
    }

    .info-box h4 {
        color: #667eea;
        margin-bottom: 5px;
    }

    .info-box p {
        color: #555;
        font-size: 13px;
        margin: 0;
    }
</style>
</head>
<body>
    <div class="navbar">
        <h1>Create New Ticket</h1>
        <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="back-btn">← Back to Dashboard</a>
    </div>

    <div class="container">
        <div class="form-card">
            <h2>Request a Service Ticket</h2>
            <p>Select an agency and service to get your ticket number</p>

            <% if (error != null) { %>
                <div class="alert alert-error">
                    <%= error %>
                </div>
            <% } %>

            <% if (success != null) { %>
                <div class="alert alert-success">
                    <%= success %>
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
                                <% if (service.getEstimatedTime() > 0) { %>
                                    (≈ <%= service.getEstimatedTime() %> min)
                                <% } %>
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
