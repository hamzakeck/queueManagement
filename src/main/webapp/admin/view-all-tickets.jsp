<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="models.Ticket" %>
<%
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    
    if (userEmail == null || !"admin".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>All Tickets</title>
<style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8f9fa; color: #212529; line-height: 1.6; }
    .header { background: #fff; border-bottom: 1px solid #e9ecef; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
    .header h1 { font-size: 1.25rem; font-weight: 600; color: #212529; }
    .user-section { display: flex; align-items: center; gap: 1.5rem; }
    .user-info { text-align: right; }
    .user-info .email { font-size: 0.875rem; color: #6c757d; }
    .user-info .role { font-size: 0.75rem; font-weight: 500; color: #fff; background: #dc3545; padding: 0.125rem 0.5rem; border-radius: 0.25rem; display: inline-block; margin-top: 0.25rem; }
    .logout-btn { background: #dc3545; color: white; border: none; padding: 0.5rem 1rem; border-radius: 0.375rem; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: background 0.2s; }
    .logout-btn:hover { background: #c82333; }
    .container { max-width: 1400px; margin: 2rem auto; padding: 0 1rem; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
    .page-header h2 { font-size: 1.5rem; font-weight: 600; color: #212529; }
    .alert { padding: 1rem; border-radius: 0.375rem; margin-bottom: 1rem; }
    .alert-danger { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; }
    .data-table { width: 100%; background: #fff; border-radius: 0.5rem; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .data-table thead { background: #f8f9fa; }
    .data-table th { padding: 1rem; text-align: left; font-weight: 600; color: #495057; border-bottom: 2px solid #dee2e6; font-size: 0.875rem; }
    .data-table td { padding: 1rem; border-bottom: 1px solid #dee2e6; font-size: 0.875rem; }
    .data-table tbody tr:hover { background: #f8f9fa; }
    .badge { padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; font-weight: 500; text-transform: uppercase; }
    .badge-waiting { background: #fff3cd; color: #856404; }
    .badge-in_progress { background: #cfe2ff; color: #084298; }
    .badge-completed { background: #d4edda; color: #155724; }
    .badge-cancelled { background: #f8d7da; color: #721c24; }
</style>
</head>
<body>
    <div class="header">
        <h1>All Tickets</h1>
        <div class="user-section">
            <div class="user-info">
                <div class="email"><%=userEmail%></div>
                <div class="role">Admin</div>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST" style="margin: 0;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <div class="page-header">
            <h2>All System Tickets</h2>
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
                <tr><td colspan="8" style="text-align: center; color: #6c757d; padding: 2rem;">No tickets found</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>
