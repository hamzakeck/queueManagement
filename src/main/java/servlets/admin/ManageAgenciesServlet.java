package servlets.admin;

import java.io.IOException;
import java.util.List;

import dao.AgencyDAO;
import dao.DAOFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Agency;

@WebServlet("/admin/ManageAgenciesServlet")
public class ManageAgenciesServlet extends HttpServlet {
    private AgencyDAO agencyDAO;

    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.agencyDAO = daoFactory.getAgencyDAO();
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
            List<Agency> agencies = agencyDAO.findAll();
            request.setAttribute("agencies", agencies);
            request.getRequestDispatcher("/admin/manage-agencies.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error loading agencies: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                String name = request.getParameter("name");
                String address = request.getParameter("address");
                String city = request.getParameter("city");

                Agency agency = new Agency();
                agency.setName(name);
                agency.setAddress(address);
                agency.setCity(city);
                agency.setActive(true);

                agencyDAO.save(agency);
                response.sendRedirect(request.getContextPath() + "/admin/ManageAgenciesServlet?success=added");

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                String address = request.getParameter("address");
                String city = request.getParameter("city");
                boolean active = "on".equals(request.getParameter("active"));

                Agency agency = agencyDAO.findById(id);
                if (agency != null) {
                    agency.setName(name);
                    agency.setAddress(address);
                    agency.setCity(city);
                    agency.setActive(active);
                    agencyDAO.update(agency);
                    response.sendRedirect(request.getContextPath() + "/admin/ManageAgenciesServlet?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/ManageAgenciesServlet?error=notfound");
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                agencyDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/ManageAgenciesServlet?success=deleted");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/ManageAgenciesServlet?error=" + e.getMessage());
        }
    }
}
