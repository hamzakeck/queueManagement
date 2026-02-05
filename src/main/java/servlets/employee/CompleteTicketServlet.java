package servlets.employee;

import java.io.IOException;
import java.util.List;

import dao.DAOFactory;
import dao.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Ticket;
import websocket.QueueWebSocket;

// Simple servlet to complete a ticket
@WebServlet("/employee/CompleteTicketServlet")
public class CompleteTicketServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String ticketIdStr = request.getParameter("ticketId");

        // Check if we got a ticket ID
        if (ticketIdStr == null || ticketIdStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "No ticket ID");
            response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
            return;
        }

        try {
            int ticketId = Integer.parseInt(ticketIdStr);
            TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();

            // Get ticket info
            Ticket ticket = ticketDAO.findById(ticketId);

            if (ticket != null) {
                // Mark as completed
                boolean success = ticketDAO.completeTicket(ticketId);

                if (success) {
                    // Tell everyone via WebSocket (with action to trigger recalculation)
                    String message = "{\"action\":\"queueUpdate\",\"ticketNumber\":\"" + ticket.getTicketNumber() +
                            "\",\"status\":\"COMPLETED\"}";
                    QueueWebSocket.sendUpdateToEveryone(message);
                    
                    // Broadcast updated wait times for all waiting tickets in the same service
                    broadcastWaitTimeUpdates(ticket.getServiceId(), ticket.getAgencyId(), ticketDAO);

                    session.setAttribute("successMessage", "Ticket completed: " + ticket.getTicketNumber());
                } else {
                    session.setAttribute("errorMessage", "Could not complete ticket");
                }
            } else {
                session.setAttribute("errorMessage", "Ticket not found");
            }

        } catch (Exception e) {
            throw new ServletException("Error completing ticket: " + e.getMessage(), e);
        }

        response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
    }
    
    /**
     * Broadcast updated wait times for all waiting tickets in a service
     */
    private void broadcastWaitTimeUpdates(int serviceId, int agencyId, TicketDAO ticketDAO) {
        try {
            // Get all waiting tickets for this service and agency
            List<Ticket> waitingTickets = ticketDAO.getWaitingQueue(agencyId, serviceId);
            
            if (waitingTickets == null || waitingTickets.isEmpty()) {
                return;
            }
            
            // Build JSON with updated wait time data
            StringBuilder json = new StringBuilder("{\"action\":\"waitTimeUpdate\",\"tickets\":[");
            
            for (int i = 0; i < waitingTickets.size(); i++) {
                Ticket t = waitingTickets.get(i);
                int position = ticketDAO.getPositionInQueue(t.getId());
                double avgTime = ticketDAO.getAverageServiceTime(t.getServiceId(), t.getAgencyId());
                int estimatedMinutes = (int) Math.max(0, Math.ceil(position * avgTime));
                
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"ticketNumber\":\"").append(t.getTicketNumber()).append("\",")
                    .append("\"position\":" ).append(position).append(",")
                    .append("\"estimatedWaitMinutes\":" ).append(estimatedMinutes)
                    .append("}");
            }
            json.append("]}" );
            
            // Broadcast to all connected WebSocket clients
            QueueWebSocket.sendUpdateToEveryone(json.toString());
            
        } catch (Exception e) {
            // Silently ignore broadcast errors to not disrupt the main flow
        }
    }
}
