package servlets.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AgencyDAO;
import dao.CitizenDAO;
import dao.DAOFactory;
import dao.ServiceDAO;
import dao.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Agency;
import models.Citizen;
import models.Service;
import models.Ticket;

@WebServlet("/admin/ViewAllTicketsServlet")
public class ViewAllTicketsServlet extends HttpServlet {
    private TicketDAO ticketDAO;
    private ServiceDAO serviceDAO;
    private AgencyDAO agencyDAO;
    private CitizenDAO citizenDAO;

    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.ticketDAO = daoFactory.getTicketDAO();
        this.serviceDAO = daoFactory.getServiceDAO();
        this.agencyDAO = daoFactory.getAgencyDAO();
        this.citizenDAO = daoFactory.getCitizenDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<Ticket> tickets = ticketDAO.findAll();

            // Load all services, agencies, and citizens into maps
            Map<Integer, String> serviceNames = new HashMap<>();
            Map<Integer, String> agencyNames = new HashMap<>();
            Map<Integer, String> citizenNames = new HashMap<>();

            for (Service service : serviceDAO.findAll()) {
                serviceNames.put(service.getId(), service.getName());
            }

            for (Agency agency : agencyDAO.findAll()) {
                agencyNames.put(agency.getId(), agency.getName());
            }

            for (Citizen citizen : citizenDAO.findAll()) {
                citizenNames.put(citizen.getId(), citizen.getFirstName() + " " + citizen.getLastName());
            }

            request.setAttribute("tickets", tickets);
            request.setAttribute("serviceNames", serviceNames);
            request.setAttribute("agencyNames", agencyNames);
            request.setAttribute("citizenNames", citizenNames);
            request.getRequestDispatcher("/admin/view-all-tickets.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error loading tickets: " + e.getMessage(), e);
        }
    }
}
