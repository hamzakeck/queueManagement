<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="dao.*" %>
<%@ page import="models.*" %>
<%
    String userEmail = (String) session.getAttribute("userEmail");
    String userRole = (String) session.getAttribute("userRole");
    Integer employeeId = (Integer) session.getAttribute("userId");
    if (userEmail == null || !"employee".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    DAOFactory factory = DAOFactory.getInstance();
    TicketDAO ticketDAO = factory.getTicketDAO();
    EmployeeDAO employeeDAO = factory.getEmployeeDAO();

    Employee employee = null;
    int counterId = 0;
    try {
        employee = employeeDAO.findById(employeeId);
        if (employee != null) {
            counterId = employee.getCounterId();
        }
    } catch (DAOException e) {
        e.printStackTrace();
    }

    // Get completed and cancelled tickets for employee's service/agency then filter by counter
    List<Ticket> completed = Collections.emptyList();
    List<Ticket> cancelled = Collections.emptyList();
    try {
        completed = ticketDAO.getTicketsByEmployeeAndStatus(employeeId, "COMPLETED");
        cancelled = ticketDAO.getTicketsByEmployeeAndStatus(employeeId, "CANCELLED");
    } catch (DAOException e) {
        e.printStackTrace();
    }

    List<Ticket> history = new ArrayList<>();
    for (Ticket t : completed) {
        if (counterId == 0 || t.getCounterId() == counterId) history.add(t);
    }
    for (Ticket t : cancelled) {
        if (counterId == 0 || t.getCounterId() == counterId) history.add(t);
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
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket History</title>
<style>
    body { font-family: -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Ubuntu,sans-serif; background:#f8f9fa; }
    .header { background:#fff; border-bottom:1px solid #e9ecef; padding:1rem 2rem; display:flex; justify-content:space-between; }
    .container { max-width: 1000px; margin: 2rem auto; background:#fff; border:1px solid #e9ecef; border-radius:8px; padding:1.5rem; }
    table { width:100%; border-collapse: collapse; }
    th, td { padding:0.75rem; border-bottom:1px solid #e9ecef; text-align:left; font-size:0.9rem; }
    th { background:#f8f9fa; font-weight:600; }
    .badge { display:inline-block; padding:0.25rem 0.5rem; border-radius:999px; font-size:0.75rem; }
    .badge-green{ background:#d1e7dd; color:#0f5132; }
    .badge-red{ background:#f8d7da; color:#842029; }
    .back { text-decoration:none; color:#212529; border:1px solid #dee2e6; padding:0.5rem 1rem; border-radius:6px; }
</style>
</head>
<body>
    <div class="header">
        <div><strong>Ticket History</strong></div>
        <div>
            <a class="back" href="<%= request.getContextPath() %>/employee/index.jsp">Back to Counter</a>
        </div>
    </div>

    <div class="container">
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Ticket</th>
                    <th>Status</th>
                    <th>Counter</th>
                    <th>Created</th>
                    <th>Called</th>
                    <th>Completed</th>
                </tr>
            </thead>
            <tbody>
                <% if (history.isEmpty()) { %>
                    <tr><td colspan="7" style="text-align:center; color:#6c757d; padding:1.5rem;">No history yet.</td></tr>
                <% } else { int i=1; for (Ticket t : history) { %>
                    <tr>
                        <td><%= i++ %></td>
                        <td style="font-family:'Courier New', monospace; font-weight:600;"> <%= t.getTicketNumber() %></td>
                        <td>
                            <% if ("COMPLETED".equals(t.getStatus())) { %>
                                <span class="badge badge-green">COMPLETED</span>
                            <% } else if ("CANCELLED".equals(t.getStatus())) { %>
                                <span class="badge badge-red">CANCELLED</span>
                            <% } else { %>
                                <span class="badge"><%= t.getStatus() %></span>
                            <% } %>
                        </td>
                        <td><%= t.getCounterId() > 0 ? t.getCounterId() : "-" %></td>
                        <td><%= t.getCreatedAt() != null ? t.getCreatedAt().toString().replace('T',' ') : "-" %></td>
                        <td><%= t.getCalledAt() != null ? t.getCalledAt().toString().replace('T',' ') : "-" %></td>
                        <td><%= t.getCompletedAt() != null ? t.getCompletedAt().toString().replace('T',' ') : "-" %></td>
                    </tr>
                <% } } %>
            </tbody>
        </table>
    </div>
</body>
</html>
