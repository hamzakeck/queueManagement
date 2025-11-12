<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Ticket" %>
<%@ page import="models.Agency" %>
<%@ page import="models.Service" %>
<%@ page import="dao.TicketDAO" %>
<%@ page import="dao.AgencyDAO" %>
<%@ page import="dao.ServiceDAO" %>
<%@ page import="dao.DAOFactory" %>
<%
    // Check if user is logged in and is citizen
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer citizenId = (Integer) session.getAttribute("userId");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Get my active tickets
    TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();
    AgencyDAO agencyDAO = DAOFactory.getInstance().getAgencyDAO();
    ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
    
    List<Ticket> myTickets = ticketDAO.findByCitizenId(citizenId);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Track My Tickets - Queue Management</title>
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
        max-width: 1000px;
        margin: 40px auto;
        padding: 0 20px;
    }

    .page-header {
        margin-bottom: 30px;
    }

    .page-header h2 {
        color: #333;
        margin-bottom: 10px;
    }

    .page-header p {
        color: #666;
    }

    .tickets-grid {
        display: grid;
        gap: 20px;
    }

    .ticket-card {
        background: white;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        overflow: hidden;
        transition: transform 0.3s, box-shadow 0.3s;
    }

    .ticket-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 5px 20px rgba(102, 126, 234, 0.2);
    }

    .ticket-header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .ticket-number {
        font-size: 32px;
        font-weight: bold;
        font-family: 'Courier New', monospace;
    }

    .ticket-status {
        padding: 8px 16px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
    }

    .status-waiting {
        background-color: #ff9800;
        color: white;
    }

    .status-called {
        background-color: #2196F3;
        color: white;
    }

    .status-in_progress {
        background-color: #4CAF50;
        color: white;
    }

    .status-completed {
        background-color: #9E9E9E;
        color: white;
    }

    .status-cancelled {
        background-color: #f44336;
        color: white;
    }

    .ticket-body {
        padding: 25px;
    }

    .ticket-info {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
        margin-bottom: 20px;
    }

    .info-item {
        display: flex;
        flex-direction: column;
    }

    .info-label {
        font-size: 12px;
        color: #666;
        text-transform: uppercase;
        margin-bottom: 5px;
    }

    .info-value {
        font-size: 16px;
        color: #333;
        font-weight: 600;
    }

    .position-indicator {
        background: #f0f4ff;
        border-left: 4px solid #667eea;
        padding: 20px;
        border-radius: 5px;
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 15px;
        text-align: center;
    }

    .position-item h4 {
        font-size: 28px;
        color: #667eea;
        margin-bottom: 5px;
    }

    .position-item p {
        font-size: 12px;
        color: #666;
    }

    .empty-state {
        background: white;
        padding: 60px 40px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        text-align: center;
    }

    .empty-icon {
        font-size: 64px;
        margin-bottom: 20px;
        opacity: 0.5;
    }

    .empty-state h3 {
        color: #333;
        margin-bottom: 10px;
    }

    .empty-state p {
        color: #666;
        margin-bottom: 30px;
    }

    .create-ticket-btn {
        display: inline-block;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 15px 40px;
        border-radius: 8px;
        text-decoration: none;
        font-weight: 600;
        transition: all 0.3s;
    }

    .create-ticket-btn:hover {
        opacity: 0.9;
        transform: translateY(-2px);
    }

    .refresh-btn {
        position: fixed;
        bottom: 30px;
        right: 30px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        padding: 15px 30px;
        border-radius: 50px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        transition: all 0.3s;
    }

    .refresh-btn:hover {
        transform: scale(1.05);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
    }
</style>
</head>
<body>
    <div class="navbar">
        <h1>📍 Track My Tickets</h1>
        <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="back-btn">← Back to Dashboard</a>
    </div>

    <div class="container">
        <div class="page-header">
            <h2>My Active Tickets</h2>
            <p>Real-time status of your queue tickets</p>
        </div>

        <% if (myTickets == null || myTickets.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-icon">🎫</div>
                <h3>No Active Tickets</h3>
                <p>You haven't created any tickets yet. Create one now to get started!</p>
                <a href="<%= request.getContextPath() %>/citizen/create-ticket.jsp" class="create-ticket-btn">
                    🎫 Create New Ticket
                </a>
            </div>
        <% } else { %>
            <div class="tickets-grid">
                <% for (Ticket ticket : myTickets) { 
                    Agency agency = agencyDAO.findById(ticket.getAgencyId());
                    Service service = serviceDAO.findById(ticket.getServiceId());
                    
                    // Get current position (count WAITING tickets before this one)
                    int currentPosition = ticketDAO.getQueuePosition(ticket.getId());
                    int totalWaiting = ticketDAO.countByAgencyAndStatus(ticket.getAgencyId(), "WAITING");
                    
                    // Calculate estimated wait time
                    int estimatedWait = 0;
                    if (service.getEstimatedTime() != null && currentPosition > 0) {
                        estimatedWait = currentPosition * service.getEstimatedTime();
                    }
                %>
                <div class="ticket-card">
                    <div class="ticket-header">
                        <div class="ticket-number"><%= ticket.getTicketNumber() %></div>
                        <div class="ticket-status status-<%= ticket.getStatus().toLowerCase() %>">
                            <%= ticket.getStatus() %>
                        </div>
                    </div>
                    
                    <div class="ticket-body">
                        <div class="ticket-info">
                            <div class="info-item">
                                <span class="info-label">Ticket ID</span>
                                <span class="info-value">#<%= ticket.getId() %></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Created</span>
                                <span class="info-value"><%= ticket.getCreatedAt() != null ? ticket.getCreatedAt().toString().substring(0, 16).replace("T", " ") : "N/A" %></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Agency</span>
                                <span class="info-value"><%= agency != null ? agency.getName() : "N/A" %></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Service</span>
                                <span class="info-value"><%= service != null ? service.getName() : "N/A" %></span>
                            </div>
                        </div>

                        <% if ("WAITING".equals(ticket.getStatus())) { %>
                        <div class="position-indicator">
                            <div class="position-item">
                                <h4><%= currentPosition %></h4>
                                <p>Position</p>
                            </div>
                            <div class="position-item">
                                <h4><%= totalWaiting %></h4>
                                <p>In Queue</p>
                            </div>
                            <div class="position-item">
                                <h4><%= estimatedWait %> min</h4>
                                <p>Est. Wait</p>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
        <% } %>
    </div>

    <button class="refresh-btn" onclick="location.reload()">🔄 Refresh</button>

    <script>
        // Auto-refresh every 30 seconds
        setTimeout(function() {
            location.reload();
        }, 30000);
    </script>
</body>
</html>
