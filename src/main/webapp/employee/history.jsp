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
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket History - Employee</title>
<style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif; background: #f8f9fa; }
    .header { background: #fff; border-bottom: 1px solid #e9ecef; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
    .header h1 { font-size: 1.5rem; font-weight: 600; color: #212529; }
    .user-section { display: flex; align-items: center; gap: 1rem; }
    .user-info { display: flex; flex-direction: column; align-items: flex-end; gap: 0.25rem; }
    .user-info .email { font-size: 0.875rem; color: #6c757d; }
    .user-info .role { font-size: 0.75rem; font-weight: 600; color: #fff; background: #28a745; padding: 0.25rem 0.5rem; border-radius: 0.25rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .logout-btn { padding: 0.5rem 1rem; background: #28a745; color: white; border: none; border-radius: 0.375rem; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.2s; }
    .logout-btn:hover { background: #218838; }
    .back-btn { padding: 0.5rem 1rem; background: #6c757d; color: white; border: none; border-radius: 0.375rem; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.2s; text-decoration: none; display: inline-block; }
    .back-btn:hover { background: #545b62; }
    .container { max-width: 1200px; margin: 0 auto; padding: 2rem; }
    .page-header { margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { font-size: 1.75rem; font-weight: 600; color: #212529; }
    .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem; }
    .stat-card { background: #fff; border: 1px solid #e9ecef; border-radius: 0.5rem; padding: 1.5rem; }
    .stat-card h3 { font-size: 0.875rem; font-weight: 500; color: #6c757d; margin-bottom: 0.5rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .stat-card .number { font-size: 2rem; font-weight: 700; color: #212529; }
    .history-table { background: #fff; border: 1px solid #e9ecef; border-radius: 0.5rem; overflow: hidden; }
    table { width: 100%; border-collapse: collapse; }
    thead { background: #f8f9fa; }
    th { padding: 0.75rem 1rem; text-align: left; font-size: 0.75rem; font-weight: 600; color: #6c757d; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid #e9ecef; }
    td { padding: 0.75rem 1rem; font-size: 0.875rem; color: #212529; border-bottom: 1px solid #f8f9fa; }
    tr:last-child td { border-bottom: none; }
    tbody tr:hover { background: #f8f9fa; }
    .ticket-number { font-family: 'Courier New', monospace; font-weight: 600; }
    .status-badge { display: inline-block; padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
    .status-completed { background: #d1e7dd; color: #0f5132; }
    .status-cancelled { background: #f8d7da; color: #842029; }
    .empty-state { text-align: center; padding: 3rem 1rem; color: #6c757d; }
    .empty-state p { margin-bottom: 1rem; }
    @media (max-width: 768px) { .header { flex-direction: column; align-items: flex-start; gap: 1rem; } .user-section { width: 100%; justify-content: space-between; } .page-header { flex-direction: column; align-items: flex-start; gap: 1rem; } .stats { grid-template-columns: 1fr; } table { font-size: 0.75rem; } th, td { padding: 0.5rem; } }
</style>
</head>
<body>
    <div class="header">
        <h1>Ticket History</h1>
        <div class="user-section">
            <div class="user-info">
                <div class="email"><%= userEmail %></div>
                <div class="role">Employee</div>
            </div>
            <form action="<%= request.getContextPath() %>/LogoutServlet" method="POST" style="margin: 0;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>

    <div class="container">
        <div class="page-header">
            <h2>Your Completed Tickets</h2>
            <a href="<%= request.getContextPath() %>/employee/index.jsp" class="back-btn">← Back to Counter</a>
        </div>

        <div class="stats">
            <div class="stat-card">
                <h3>Total Tickets</h3>
                <div class="number"><%= history.size() %></div>
            </div>
            <div class="stat-card">
                <h3>Completed</h3>
                <div class="number"><%= completed.size() %></div>
            </div>
            <div class="stat-card">
                <h3>Cancelled</h3>
                <div class="number"><%= cancelled.size() %></div>
            </div>
        </div>

        <% if (history.isEmpty()) { %>
            <div class="history-table">
                <div class="empty-state">
                    <p>No tickets served yet</p>
                    <a href="<%= request.getContextPath() %>/employee/index.jsp" class="back-btn">Go to Counter</a>
                </div>
            </div>
        <% } else { %>
            <div class="history-table">
                <table>
                    <thead>
                        <tr>
                            <th>Ticket</th>
                            <th>Status</th>
                            <th>Counter</th>
                            <th>Created</th>
                            <th>Called</th>
                            <th>Completed</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Ticket t : history) { %>
                            <tr>
                                <td class="ticket-number"><%= t.getTicketNumber() %></td>
                                <td>
                                    <% if ("COMPLETED".equals(t.getStatus())) { %>
                                        <span class="status-badge status-completed">Completed</span>
                                    <% } else if ("CANCELLED".equals(t.getStatus())) { %>
                                        <span class="status-badge status-cancelled">Cancelled</span>
                                    <% } else { %>
                                        <span class="status-badge"><%= t.getStatus() %></span>
                                    <% } %>
                                </td>
                                <td><%= t.getCounterId() > 0 ? t.getCounterId() : "-" %></td>
                                <td><%= t.getCreatedAt() != null ? t.getCreatedAt().toString().replace('T',' ').substring(0, 16) : "-" %></td>
                                <td><%= t.getCalledAt() != null ? t.getCalledAt().toString().replace('T',' ').substring(0, 16) : "-" %></td>
                                <td><%= t.getCompletedAt() != null ? t.getCompletedAt().toString().replace('T',' ').substring(0, 16) : "-" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>
</body>
</html>
