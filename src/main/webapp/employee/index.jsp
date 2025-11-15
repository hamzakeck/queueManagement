<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Ticket" %>
<%@ page import="dao.TicketDAO" %>
<%@ page import="dao.DAOFactory" %>
<%
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer employeeId = (Integer) session.getAttribute("userId");
    
    if (userEmail == null || !"employee".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();
    Ticket currentTicket = ticketDAO.getCurrentTicketForEmployee(employeeId);
    List<Ticket> waitingTickets = ticketDAO.getTicketsByEmployeeAndStatus(employeeId, "WAITING");
    
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Employee Counter</title>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        background: #f8f9fa;
        color: #212529;
        line-height: 1.6;
    }

    .header {
        background: #fff;
        border-bottom: 1px solid #e9ecef;
        padding: 1rem 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .header h1 {
        font-size: 1.25rem;
        font-weight: 600;
    }

    .user-section {
        display: flex;
        align-items: center;
        gap: 1.5rem;
    }

    .user-info {
        text-align: right;
        font-size: 0.875rem;
        color: #6c757d;
    }

    .logout-btn {
        background: transparent;
        border: 1px solid #dee2e6;
        padding: 0.5rem 1rem;
        border-radius: 0.25rem;
        cursor: pointer;
        font-size: 0.875rem;
        color: #495057;
        transition: all 0.2s;
    }

    .logout-btn:hover {
        background: #f8f9fa;
        border-color: #adb5bd;
    }

    .container {
        max-width: 1000px;
        margin: 2rem auto;
        padding: 0 1.5rem;
    }

    .alert {
        padding: 1rem;
        border-radius: 0.375rem;
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

    .current-section {
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        padding: 2rem;
        margin-bottom: 2rem;
    }

    .current-section h2 {
        font-size: 1.25rem;
        font-weight: 600;
        margin-bottom: 1.5rem;
    }

    .no-ticket {
        text-align: center;
        padding: 2rem;
        color: #6c757d;
    }

    .ticket-display {
        text-align: center;
        padding: 2rem;
        background: #f8f9fa;
        border-radius: 0.375rem;
        margin-bottom: 1.5rem;
    }

    .ticket-number-large {
        font-size: 3rem;
        font-weight: 700;
        font-family: 'Courier New', monospace;
        color: #212529;
        margin-bottom: 0.5rem;
    }

    .ticket-service {
        font-size: 1rem;
        color: #6c757d;
        margin-bottom: 1.5rem;
    }

    .action-buttons {
        display: flex;
        gap: 1rem;
        justify-content: center;
    }

    .btn {
        padding: 0.75rem 1.5rem;
        border: none;
        border-radius: 0.25rem;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
    }

    .btn-primary {
        background: #212529;
        color: #fff;
    }

    .btn-primary:hover {
        background: #000;
    }

    .btn-success {
        background: #198754;
        color: #fff;
    }

    .btn-success:hover {
        background: #146c43;
    }

    .queue-section {
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        padding: 2rem;
    }

    .queue-section h2 {
        font-size: 1.25rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
    }

    .queue-count {
        font-size: 0.875rem;
        color: #6c757d;
        margin-bottom: 1.5rem;
    }

    .queue-list {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .queue-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        background: #f8f9fa;
        border-radius: 0.375rem;
        border: 1px solid #e9ecef;
    }

    .queue-item-number {
        font-size: 1.125rem;
        font-weight: 600;
        font-family: 'Courier New', monospace;
    }

    .queue-item-info {
        font-size: 0.875rem;
        color: #6c757d;
    }

    @media (max-width: 768px) {
        .action-buttons {
            flex-direction: column;
        }

        .btn {
            width: 100%;
        }
    }
</style>
</head>
<body>
    <div class="header">
        <h1>Employee Counter</h1>
        <div class="user-section">
            <div class="user-info">
                <div><%= userEmail %></div>
                <div style="font-weight: 500; color: #212529;">Employee</div>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <% if (successMessage != null) { %>
            <div class="alert alert-success"><%= successMessage %></div>
        <% } %>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <% } %>

        <div class="current-section">
            <h2>Currently Serving</h2>
            
            <% if (currentTicket == null) { %>
                <div class="no-ticket">
                    <p style="margin-bottom: 1.5rem;">No ticket is currently being served</p>
                    <form action="<%= request.getContextPath() %>/employee/CallNextTicketServlet" method="POST">
                        <button type="submit" class="btn btn-primary">Call Next Ticket</button>
                    </form>
                </div>
            <% } else { %>
                <div class="ticket-display">
                    <div class="ticket-number-large"><%= currentTicket.getTicketNumber() %></div>
                    <div class="ticket-service">Service #<%= currentTicket.getServiceId() %></div>
                </div>
                
                <div class="action-buttons">
                    <form action="<%= request.getContextPath() %>/employee/CompleteTicketServlet" method="POST">
                        <input type="hidden" name="ticketId" value="<%= currentTicket.getId() %>">
                        <button type="submit" class="btn btn-success">Complete Service</button>
                    </form>
                </div>
            <% } %>
        </div>

        <div class="queue-section">
            <h2>Waiting Queue</h2>
            <div class="queue-count"><%= waitingTickets.size() %> ticket(s) waiting</div>
            
            <% if (waitingTickets.isEmpty()) { %>
                <div style="text-align: center; padding: 2rem; color: #6c757d;">
                    <p>No tickets in queue</p>
                </div>
            <% } else { %>
                <div class="queue-list">
                    <% for (Ticket ticket : waitingTickets) { %>
                        <div class="queue-item">
                            <div>
                                <div class="queue-item-number"><%= ticket.getTicketNumber() %></div>
                                <div class="queue-item-info">Position: <%= ticket.getPosition() %></div>
                            </div>
                            <div class="queue-item-info">
                                Created: <%= ticket.getCreatedAt() != null ? ticket.getCreatedAt().toString().substring(11, 16) : "N/A" %>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } %>
        </div>
    </div>

    <script>
        // Auto-refresh every 10 seconds to keep queue updated
        setTimeout(function() {
            location.reload();
        }, 10000);
    </script>
</body>
</html>
