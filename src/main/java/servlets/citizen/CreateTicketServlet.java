package servlets.citizen;

import java.io.IOException;

import dao.TicketDAO;
import dao.DAOFactory;
import dao.DAOException;
import models.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet to handle ticket creation for citizens
 */
@WebServlet("/citizen/CreateTicketServlet")
public class CreateTicketServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TicketDAO ticketDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        ticketDAO = DAOFactory.getInstance().getTicketDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer citizenId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        // Verify user is logged in as citizen
        if (citizenId == null || !"citizen".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Check if user already has an active ticket (WAITING, CALLED, or IN_PROGRESS)
            java.util.List<Ticket> allTickets = ticketDAO.findByCitizenId(citizenId);
            boolean hasActiveTicket = false;
            if (allTickets != null) {
                for (Ticket t : allTickets) {
                    String status = t.getStatus();
                    if ("WAITING".equals(status) || "CALLED".equals(status) || "IN_PROGRESS".equals(status)) {
                        hasActiveTicket = true;
                        break;
                    }
                }
            }

            if (hasActiveTicket) {
                response.sendRedirect(request.getContextPath() +
                        "/citizen/create-ticket.jsp?error=You already have an active ticket. Please complete or cancel your current ticket before creating a new one.");
                return;
            }

            // Get form parameters
            String agencyIdStr = request.getParameter("agencyId");
            String serviceIdStr = request.getParameter("serviceId");

            // Validate parameters
            if (agencyIdStr == null || agencyIdStr.trim().isEmpty() ||
                    serviceIdStr == null || serviceIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() +
                        "/citizen/create-ticket.jsp?error=Please select both agency and service");
                return;
            }

            int agencyId = Integer.parseInt(agencyIdStr);
            int serviceId = Integer.parseInt(serviceIdStr);

            // Create new ticket using DAO
            Ticket ticket = new Ticket();
            ticket.setCitizenId(citizenId);
            ticket.setAgencyId(agencyId);
            ticket.setServiceId(serviceId);
            ticket.setStatus("WAITING");

            // Generate ticket number and get position BEFORE creating
            String ticketNumber = ticketDAO.generateTicketNumber(agencyId, serviceId);
            ticket.setTicketNumber(ticketNumber);

            int position = ticketDAO.getNextPosition(agencyId, serviceId);
            ticket.setPosition(position);

            // Create the ticket in database
            int ticketId = ticketDAO.create(ticket);
            ticket.setId(ticketId);

            // Store ticket info in session for confirmation page
            session.setAttribute("newTicketId", ticketId);
            session.setAttribute("newTicketNumber", ticketNumber);
            session.setAttribute("newTicketPosition", position);
            session.setAttribute("newTicketAgencyId", agencyId);
            session.setAttribute("newTicketServiceId", serviceId);

            // Redirect to confirmation page
            response.sendRedirect(request.getContextPath() + "/citizen/ticket-confirmation.jsp");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/citizen/create-ticket.jsp?error=Invalid agency or service selection");
        } catch (DAOException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/citizen/create-ticket.jsp?error=Failed to create ticket: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/citizen/create-ticket.jsp?error=An error occurred while creating your ticket");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the form page
        response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
    }
}
