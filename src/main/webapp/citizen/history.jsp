<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="dao.*" %>
<%@ page import="models.*" %>
<%
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer citizenId = (Integer) session.getAttribute("userId");
    
    if (userEmail == null || !"citizen".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    DAOFactory factory = DAOFactory.getInstance();
    TicketDAO ticketDAO = factory.getTicketDAO();

    // Get all tickets for this citizen
    List<Ticket> allTickets = Collections.emptyList();
    try {
        allTickets = ticketDAO.findByCitizenId(citizenId);
    } catch (DAOException e) {
        e.printStackTrace();
    }

    // Filter completed and cancelled tickets
    List<Ticket> history = new ArrayList<>();
    for (Ticket t : allTickets) {
        String status = t.getStatus();
        if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) {
            history.add(t);
        }
    }

    // Sort by completed_at desc, fallback to created_at
    history.sort((a, b) -> {
        java.time.LocalDateTime da = a.getCompletedAt() != null ? a.getCompletedAt() : a.getCreatedAt();
        java.time.LocalDateTime db = b.getCompletedAt() != null ? b.getCompletedAt() : b.getCreatedAt();
        if (da == null && db == null) return 0;
        if (da == null) return 1;
        if (db == null) return -1;
        return db.compareTo(da);
    });
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket History</title>
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
        color: #212529;
    }

    .user-section {
        display: flex;
        align-items: center;
        gap: 1.5rem;
    }

    .user-info {
        text-align: right;
    }

    .user-info .email {
        font-size: 0.875rem;
        color: #6c757d;
    }

    .user-info .role {
        font-size: 0.75rem;
        font-weight: 500;
        color: #fff;
        background: #28a745;
        padding: 0.125rem 0.5rem;
        border-radius: 0.25rem;
        display: inline-block;
        margin-top: 0.25rem;
    }

    .back-btn {
        background: transparent;
        border: 1px solid #dee2e6;
        padding: 0.5rem 1rem;
        border-radius: 0.375rem;
        cursor: pointer;
        font-size: 0.875rem;
        color: #495057;
        text-decoration: none;
        transition: all 0.2s;
    }

    .back-btn:hover {
        background: #f8f9fa;
        border-color: #adb5bd;
    }

    .container {
        max-width: 1200px;
        margin: 2rem auto;
        padding: 0 1rem;
    }

    .page-header {
        margin-bottom: 1.5rem;
    }

    .page-header h2 {
        font-size: 1.5rem;
        font-weight: 600;
        color: #212529;
        margin-bottom: 0.5rem;
    }

    .page-header p {
        color: #6c757d;
        font-size: 0.875rem;
    }

    .history-table {
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 0.5rem;
        overflow: hidden;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }

    table {
        width: 100%;
        border-collapse: collapse;
    }

    thead {
        background: #f8f9fa;
    }

    th {
        padding: 1rem;
        text-align: left;
        font-weight: 600;
        color: #495057;
        border-bottom: 2px solid #dee2e6;
        font-size: 0.875rem;
    }

    td {
        padding: 1rem;
        border-bottom: 1px solid #e9ecef;
        font-size: 0.875rem;
    }

    tbody tr:hover {
        background: #f8f9fa;
    }

    tbody tr:last-child td {
        border-bottom: none;
    }

    .ticket-number {
        font-family: 'Courier New', monospace;
        font-weight: 600;
        font-size: 1rem;
        color: #212529;
    }

    .badge {
        display: inline-block;
        padding: 0.25rem 0.5rem;
        border-radius: 0.25rem;
        font-size: 0.75rem;
        font-weight: 500;
        text-transform: uppercase;
    }

    .badge-completed {
        background: #d4edda;
        color: #155724;
    }

    .badge-cancelled {
        background: #f8d7da;
        color: #721c24;
    }

    .empty-state {
        text-align: center;
        padding: 3rem 1rem;
        color: #6c757d;
    }

    .empty-state p {
        margin-bottom: 1rem;
    }

    .empty-state a {
        color: #007bff;
        text-decoration: none;
        font-weight: 500;
    }

    .empty-state a:hover {
        text-decoration: underline;
    }

    @media (max-width: 768px) {
        .header {
            flex-direction: column;
            align-items: flex-start;
            gap: 1rem;
        }

        .user-section {
            width: 100%;
            justify-content: space-between;
        }

        table {
            font-size: 0.75rem;
        }

        th, td {
            padding: 0.5rem;
        }

        .ticket-number {
            font-size: 0.875rem;
        }
    }
</style>
</head>
<body>
    <div class="header">
        <h1>Ticket History</h1>
        <div class="user-section">
            <div class="user-info">
                <div class="email"><%=userEmail%></div>
                <div class="role">Citizen</div>
            </div>
            <a href="<%= request.getContextPath() %>/citizen/index.jsp" class="back-btn">Back to Dashboard</a>
        </div>
    </div>

    <div class="container">
        <div class="page-header">
            <h2>Your Ticket History</h2>
            <p>View your completed and cancelled tickets</p>
        </div>

        <% if (history.isEmpty()) { %>
            <div class="history-table">
                <div class="empty-state">
                    <p>No ticket history yet.</p>
                    <a href="<%= request.getContextPath() %>/citizen/create-ticket.jsp">Create your first ticket</a>
                </div>
            </div>
        <% } else { %>
            <div class="history-table">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Ticket Number</th>
                            <th>Status</th>
                            <th>Agency</th>
                            <th>Service</th>
                            <th>Counter</th>
                            <th>Created</th>
                            <th>Completed</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% int i = 1; 
                           for (Ticket t : history) { 
                               String formattedCreated = t.getCreatedAt() != null 
                                   ? t.getCreatedAt().toString().replace('T', ' ').substring(0, 16) 
                                   : "-";
                               String formattedCompleted = t.getCompletedAt() != null 
                                   ? t.getCompletedAt().toString().replace('T', ' ').substring(0, 16) 
                                   : "-";
                        %>
                            <tr>
                                <td><%= i++ %></td>
                                <td class="ticket-number"><%= t.getTicketNumber() %></td>
                                <td>
                                    <% if ("COMPLETED".equals(t.getStatus())) { %>
                                        <span class="badge badge-completed">Completed</span>
                                    <% } else if ("CANCELLED".equals(t.getStatus())) { %>
                                        <span class="badge badge-cancelled">Cancelled</span>
                                    <% } %>
                                </td>
                                <td><%= t.getAgencyId() %></td>
                                <td><%= t.getServiceId() %></td>
                                <td><%= t.getCounterId() > 0 ? String.valueOf(t.getCounterId()) : "-" %></td>
                                <td><%= formattedCreated %></td>
                                <td><%= formattedCompleted %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>
</body>
</html>
