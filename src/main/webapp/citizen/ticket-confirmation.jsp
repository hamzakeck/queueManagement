<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="models.Agency" %>
<%@ page import="models.Service" %>
<%@ page import="dao.AgencyDAO" %>
<%@ page import="dao.ServiceDAO" %>
<%@ page import="dao.DAOFactory" %>
<%
    // Check if user is logged in and is citizen
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Get ticket information from session
    Integer ticketId = (Integer) session.getAttribute("newTicketId");
    String ticketNumber = (String) session.getAttribute("newTicketNumber");
    Integer position = (Integer) session.getAttribute("newTicketPosition");
    Integer agencyId = (Integer) session.getAttribute("newTicketAgencyId");
    Integer serviceId = (Integer) session.getAttribute("newTicketServiceId");

    if (ticketId == null || ticketNumber == null) {
        response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
        return;
    }

    // Get agency and service details
    AgencyDAO agencyDAO = DAOFactory.getInstance().getAgencyDAO();
    ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
    
    Agency agency = agencyDAO.findById(agencyId);
    Service service = serviceDAO.findById(serviceId);

    // Calculate estimated wait time (position * service time)
    int estimatedWait = 0;
    if (position != null && service.getEstimatedTime() != null) {
        estimatedWait = position * service.getEstimatedTime();
    }

    // Clear the session attributes after retrieval (one-time view)
    // Comment out if you want to keep them for refresh
    // session.removeAttribute("newTicketId");
    // session.removeAttribute("newTicketNumber");
    // session.removeAttribute("newTicketPosition");
    // session.removeAttribute("newTicketAgencyId");
    // session.removeAttribute("newTicketServiceId");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket Created - Queue Management</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
    }

    .confirmation-container {
        max-width: 600px;
        background: white;
        border-radius: 15px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        overflow: hidden;
    }

    .success-header {
        background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
        color: white;
        padding: 40px;
        text-align: center;
    }

    .success-icon {
        font-size: 64px;
        margin-bottom: 15px;
        animation: scaleIn 0.5s ease-out;
    }

    @keyframes scaleIn {
        from {
            transform: scale(0);
        }
        to {
            transform: scale(1);
        }
    }

    .success-header h1 {
        font-size: 28px;
        margin-bottom: 10px;
    }

    .success-header p {
        font-size: 16px;
        opacity: 0.95;
    }

    .ticket-details {
        padding: 40px;
    }

    .ticket-number {
        text-align: center;
        margin-bottom: 30px;
    }

    .ticket-number-label {
        font-size: 14px;
        color: #666;
        margin-bottom: 10px;
    }

    .ticket-number-value {
        font-size: 72px;
        font-weight: bold;
        color: #667eea;
        font-family: 'Courier New', monospace;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    }

    .info-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-bottom: 30px;
    }

    .info-item {
        background: #f5f7fa;
        padding: 20px;
        border-radius: 10px;
        text-align: center;
    }

    .info-label {
        font-size: 12px;
        color: #666;
        text-transform: uppercase;
        margin-bottom: 8px;
    }

    .info-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
    }

    .info-value.large {
        font-size: 32px;
        color: #667eea;
    }

    .details-section {
        border-top: 2px solid #f0f0f0;
        padding-top: 20px;
        margin-bottom: 30px;
    }

    .details-section h3 {
        color: #333;
        margin-bottom: 15px;
        font-size: 18px;
    }

    .detail-row {
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        border-bottom: 1px solid #f0f0f0;
    }

    .detail-label {
        color: #666;
        font-size: 14px;
    }

    .detail-value {
        color: #333;
        font-weight: 600;
        font-size: 14px;
    }

    .action-buttons {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 15px;
    }

    .btn {
        padding: 15px;
        border-radius: 8px;
        text-decoration: none;
        text-align: center;
        font-weight: 600;
        font-size: 14px;
        transition: all 0.3s;
        cursor: pointer;
        border: none;
    }

    .btn-primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }

    .btn-primary:hover {
        opacity: 0.9;
        transform: translateY(-2px);
    }

    .btn-secondary {
        background: white;
        color: #667eea;
        border: 2px solid #667eea;
    }

    .btn-secondary:hover {
        background: #f5f7fa;
    }

    .note {
        background: #fff8e1;
        border-left: 4px solid #ffc107;
        padding: 15px;
        margin-top: 20px;
        border-radius: 5px;
        font-size: 13px;
        color: #666;
    }

    .note strong {
        color: #333;
    }

    @media print {
        body {
            background: white;
        }
        .action-buttons {
            display: none;
        }
    }
</style>
</head>
<body>
    <div class="confirmation-container">
        <div class="success-header">
            <div class="success-icon">✅</div>
            <h1>Ticket Created Successfully!</h1>
            <p>Your queue ticket has been generated</p>
        </div>

        <div class="ticket-details">
            <div class="ticket-number">
                <div class="ticket-number-label">YOUR TICKET NUMBER</div>
                <div class="ticket-number-value"><%= ticketNumber %></div>
            </div>

            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Position in Queue</div>
                    <div class="info-value large"><%= position != null ? position : "N/A" %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Estimated Wait</div>
                    <div class="info-value large">
                        <%= estimatedWait > 0 ? estimatedWait + " min" : "N/A" %>
                    </div>
                </div>
            </div>

            <div class="details-section">
                <h3>📋 Ticket Details</h3>
                <div class="detail-row">
                    <span class="detail-label">Ticket ID</span>
                    <span class="detail-value">#<%= ticketId %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Agency</span>
                    <span class="detail-value"><%= agency.getName() %> - <%= agency.getCity() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Service</span>
                    <span class="detail-value"><%= service.getName() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Status</span>
                    <span class="detail-value" style="color: #ff9800;">WAITING</span>
                </div>
            </div>

            <div class="note">
                <strong>📌 Important:</strong> Please arrive at the agency before your turn. 
                You can track your ticket status in real-time from the dashboard.
            </div>

            <div class="action-buttons">
                <a href="<%= request.getContextPath() %>/citizen/track-ticket.jsp?ticketId=<%= ticketId %>" class="btn btn-primary">
                    📍 Track Ticket
                </a>
                <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="btn btn-secondary">
                    🏠 Dashboard
                </a>
            </div>
        </div>
    </div>

    <script>
        // Auto-print option (uncomment if needed)
        // window.onload = function() {
        //     setTimeout(function() {
        //         if (confirm('Would you like to print your ticket?')) {
        //             window.print();
        //         }
        //     }, 500);
        // };
    </script>
</body>
</html>
