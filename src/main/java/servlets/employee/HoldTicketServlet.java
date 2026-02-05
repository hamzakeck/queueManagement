package servlets.employee;

import java.io.IOException;

import dao.DAOException;
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

@WebServlet("/employee/HoldTicketServlet")
public class HoldTicketServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer employeeId = (Integer) session.getAttribute("userId");

        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();
            // Get the ticket currently in progress for this employee
            Ticket current = ticketDAO.getCurrentTicketForEmployee(employeeId);

            if (current == null) {
                session.setAttribute("errorMessage", "No ticket is currently in progress to put on hold.");
                response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
                return;
            }

            // Compute next position at the end of the queue for same service/agency
            int newPosition = ticketDAO.getNextPosition(current.getAgencyId(), current.getServiceId());

            // Move ticket back to waiting queue
            current.setStatus("WAITING");
            current.setPosition(newPosition);
            current.setCounterId(0); // clear counter assignment
            current.setCalledAt(null); // clear called time

            boolean updated = ticketDAO.update(current);

            if (updated) {
                String message = "{\"action\":\"queueUpdate\",\"ticketNumber\":\"" + current.getTicketNumber() +
                        "\",\"status\":\"WAITING\"}";
                QueueWebSocket.sendUpdateToEveryone(message);

                session.setAttribute("successMessage", "Ticket put on hold: " + current.getTicketNumber());
            } else {
                session.setAttribute("errorMessage", "Failed to put the ticket on hold.");
            }

        } catch (DAOException e) {
            throw new ServletException("Error putting ticket on hold: " + e.getMessage(), e);
        }

        response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
    }
}
