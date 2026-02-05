package servlets.citizen;

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

    @Override
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
                session.setAttribute("errorMessage", "You already have an active ticket. Please complete or cancel your current ticket before creating a new one.");
                response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
                return;
            }

            // Get form parameters
            String agencyIdStr = request.getParameter("agencyId");
            String serviceIdStr = request.getParameter("serviceId");

            // Validate parameters
            if (agencyIdStr == null || agencyIdStr.trim().isEmpty() ||
                    serviceIdStr == null || serviceIdStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Please select both agency and service");
                response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
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
            
            // Broadcast new ticket creation to employees
            broadcastTicketCreated(ticketNumber, agencyId, serviceId);
            
            // Broadcast updated wait times for all waiting tickets in this service
            broadcastWaitTimeUpdates(serviceId, agencyId, ticketDAO);

            // Store ticket info in session for confirmation page
            session.setAttribute("newTicketId", ticketId);
            session.setAttribute("newTicketNumber", ticketNumber);
            session.setAttribute("newTicketPosition", position);
            session.setAttribute("newTicketAgencyId", agencyId);
            session.setAttribute("newTicketServiceId", serviceId);

            // Redirect to confirmation page
            response.sendRedirect(request.getContextPath() + "/citizen/ticket-confirmation.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid agency or service selection");
            response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
        } catch (DAOException e) {
            session.setAttribute("errorMessage", "Failed to create ticket. Please try again.");
            response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "An error occurred while creating your ticket");
            response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the form page
        response.sendRedirect(request.getContextPath() + "/citizen/create-ticket.jsp");
    }
    
    /**
     * Broadcast new ticket creation to all connected clients (employees)
     */
    private void broadcastTicketCreated(String ticketNumber, int agencyId, int serviceId) {
        try {
            String message = "{\"action\":\"newTicket\"," +
                    "\"ticketNumber\":\"" + ticketNumber + "\"," +
                    "\"agencyId\":" + agencyId + "," +
                    "\"serviceId\":" + serviceId + "," +
                    "\"status\":\"WAITING\"}";
            QueueWebSocket.sendUpdateToEveryone(message);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            java.util.logging.Logger.getLogger(CreateTicketServlet.class.getName())
                    .log(java.util.logging.Level.WARNING, "Error broadcasting ticket creation", e);
        }
    }
    
    /**
     * Broadcast updated wait times for all waiting tickets in a service
     */
    private void broadcastWaitTimeUpdates(int serviceId, int agencyId, TicketDAO ticketDAO) {
        try {
            // Get all waiting tickets for this service and agency
            java.util.List<Ticket> waitingTickets = ticketDAO.getWaitingQueue(agencyId, serviceId);
            
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
                    .append("\"position\":").append(position).append(",")
                    .append("\"estimatedWaitMinutes\":").append(estimatedMinutes)
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
