<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="models.Ticket" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Tickets</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: #f5f5f5;
            color: #333;
        }
        header {
            background: #fff;
            border-bottom: 1px solid #e9ecef;
            padding: 1rem 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
        .header-content {
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
        main {
            padding: 2rem 0;
        }
        .dashboard-header {
            margin-bottom: 2rem;
        }
        h1 {
            font-size: 2rem;
            font-weight: 600;
        }
        .alert {
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1.5rem;
        }
        .alert-danger {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .data-table {
            width: 100%;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .data-table thead {
            background: #f8f9fa;
        }
        .data-table th,
        .data-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }
        .data-table th {
            font-weight: 600;
            color: #495057;
        }
        .data-table tbody tr:hover {
            background: #f8f9fa;
        }
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
</head>
<body>
    <header>
        <div class="container">
            <div class="header-content">
                <div class="logo">All Tickets</div>
                <a href="<%=request.getContextPath()%>/admin/index.jsp" class="nav-link">← Back to Dashboard</a>
            </div>
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
                    <th>Citizen</th>
                    <th>Service</th>
                    <th>Agency</th>
                    <th>Status</th>
                    <th>Counter</th>
                    <th>Created</th>
                </tr>
            </thead>
            <tbody>
                <% 
                   List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
                   Map<Integer, String> serviceNames = (Map<Integer, String>) request.getAttribute("serviceNames");
                   Map<Integer, String> agencyNames = (Map<Integer, String>) request.getAttribute("agencyNames");
                   Map<Integer, String> citizenNames = (Map<Integer, String>) request.getAttribute("citizenNames");
                   
                   if (tickets != null && !tickets.isEmpty()) {
                       for (Ticket ticket : tickets) { 
                           String serviceName = serviceNames.get(ticket.getServiceId());
                           String agencyName = agencyNames.get(ticket.getAgencyId());
                           String citizenName = citizenNames.get(ticket.getCitizenId());
                %>
                <tr>
                    <td><%=ticket.getId()%></td>
                    <td><strong><%=ticket.getTicketNumber()%></strong></td>
                    <td><%=citizenName != null ? citizenName : "ID: " + ticket.getCitizenId()%></td>
                    <td><%=serviceName != null ? serviceName : "ID: " + ticket.getServiceId()%></td>
                    <td><%=agencyName != null ? agencyName : "ID: " + ticket.getAgencyId()%></td>
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
</body>
</html>
