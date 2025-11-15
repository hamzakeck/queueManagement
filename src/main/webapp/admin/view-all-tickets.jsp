<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="models.Ticket" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Tickets</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css">
</head>
<body>
    <header class="navbar">
        <div class="container">
            <div class="nav-brand">All Tickets</div>
            <nav>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="btn btn-secondary">Back to Dashboard</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="dashboard-header">
            <h1>All System Tickets</h1>
        </div>

        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <div class="alert alert-danger">Error: <%= error %></div>
        <% } %>

        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ticket #</th>
                    <th>Citizen ID</th>
                    <th>Service ID</th>
                    <th>Agency ID</th>
                    <th>Status</th>
                    <th>Counter</th>
                    <th>Created</th>
                </tr>
            </thead>
            <tbody>
                <% List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
                   if (tickets != null && !tickets.isEmpty()) {
                       for (Ticket ticket : tickets) { %>
                <tr>
                    <td><%=ticket.getId()%></td>
                    <td><strong><%=ticket.getTicketNumber()%></strong></td>
                    <td><%=ticket.getCitizenId()%></td>
                    <td><%=ticket.getServiceId()%></td>
                    <td><%=ticket.getAgencyId()%></td>
                    <td>
                        <span class="badge badge-<%=ticket.getStatus().toLowerCase()%>">
                            <%=ticket.getStatus()%>
                        </span>
                    </td>
                    <td><%=ticket.getCounterId() > 0 ? ticket.getCounterId() : "-"%></td>
                    <td><%=ticket.getCreatedAt() != null ? ticket.getCreatedAt() : "-"%></td>
                </tr>
                <% } } else { %>
                <tr><td colspan="8">No tickets found</td></tr>
                <% } %>
            </tbody>
        </table>
    </main>

    <style>
        .badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.85em;
            font-weight: 500;
        }
        .badge-waiting { background: #fff3cd; color: #856404; }
        .badge-called { background: #d1ecf1; color: #0c5460; }
        .badge-in_progress { background: #cce5ff; color: #004085; }
        .badge-completed { background: #d4edda; color: #155724; }
        .badge-cancelled { background: #f8d7da; color: #721c24; }
    </style>
</body>
</html>
