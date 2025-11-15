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

                    session.setAttribute("successMessage", "Ticket completed: " + ticket.getTicketNumber());
                } else {
                    session.setAttribute("errorMessage", "Could not complete ticket");
                }
            } else {
                session.setAttribute("errorMessage", "Ticket not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
    }
}
