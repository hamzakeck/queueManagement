package servlets.citizen;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.TicketDAO;
import dao.DAOFactory;
import dao.DAOException;
import models.Ticket;

import java.io.IOException;
import java.io.PrintWriter;

// Servlet to calculate and return estimated wait time
@WebServlet("/citizen/GetWaitTimeServlet")
public class GetWaitTimeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ticketNumber = request.getParameter("ticketNumber");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            TicketDAO ticketDAO = DAOFactory.getInstance().getTicketDAO();
            Ticket ticket = ticketDAO.findByTicketNumber(ticketNumber);

            if (ticket == null) {
                out.print("{\"error\":\"Ticket not found\"}");
                return;
            }

            // Get position in queue
            int position = ticketDAO.getPositionInQueue(ticket.getId());

            // Get average service time
            double avgTime = ticketDAO.getAverageServiceTime(ticket.getServiceId(), ticket.getAgencyId());

            // Calculate estimated wait time (never negative)
            int estimatedMinutes = (int) Math.max(0, Math.ceil(position * avgTime));

            // Return JSON response
            String json = "{" +
                    "\"ticketNumber\":\"" + ticket.getTicketNumber() + "\"," +
                    "\"status\":\"" + ticket.getStatus() + "\"," +
                    "\"position\":" + position + "," +
                    "\"avgServiceTime\":" + String.format("%.1f", avgTime) + "," +
                    "\"estimatedWaitMinutes\":" + estimatedMinutes +
                    "}";

            out.print(json);

        } catch (DAOException e) {
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
