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
}
