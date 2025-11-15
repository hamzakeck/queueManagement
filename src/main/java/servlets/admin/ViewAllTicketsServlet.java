package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.DAOFactory;
import dao.TicketDAO;
import models.Ticket;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/ViewAllTicketsServlet")
public class ViewAllTicketsServlet extends HttpServlet {
    private TicketDAO ticketDAO;

    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.ticketDAO = daoFactory.getTicketDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
            return;
        }

        try {
            List<Ticket> tickets = ticketDAO.findAll();
            request.setAttribute("tickets", tickets);
            request.getRequestDispatcher("/admin/view-all-tickets.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading tickets: " + e.getMessage());
            request.getRequestDispatcher("/admin/view-all-tickets.jsp").forward(request, response);
        }
    }
}
