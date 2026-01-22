package servlets.employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.TicketDAO;
import dao.DAOFactory;
import dao.DAOException;
import models.Ticket;
import websocket.QueueWebSocket;

import java.io.IOException;
import java.util.List;

// Simple servlet to call next ticket
@WebServlet("/employee/CallNextTicketServlet")
public class CallNextTicketServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer employeeId = (Integer) session.getAttribute("userId");

        // Check if employee is logged in
        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Get the next ticket
            TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();
            Ticket nextTicket = ticketDAO.callNextTicket(employeeId);

            if (nextTicket != null) {
                // Tell everyone via WebSocket (with action to trigger recalculation)
                String message = "{\"action\":\"queueUpdate\",\"ticketNumber\":\"" + nextTicket.getTicketNumber() +
                        "\",\"status\":\"IN_PROGRESS\"}";
                QueueWebSocket.sendUpdateToEveryone(message);
                
                // Broadcast updated wait times for all waiting tickets in the same service
                broadcastWaitTimeUpdates(nextTicket.getServiceId(), nextTicket.getAgencyId(), ticketDAO);

                session.setAttribute("successMessage", "Called ticket: " + nextTicket.getTicketNumber());
            } else {
                session.setAttribute("errorMessage", "No tickets in queue");
            }

        } catch (DAOException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
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
            System.err.println("Error broadcasting wait time updates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
