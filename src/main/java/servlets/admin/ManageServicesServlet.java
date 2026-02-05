package servlets.admin;

import java.io.IOException;
import java.util.List;

import dao.DAOFactory;
import dao.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Service;

@WebServlet("/admin/ManageServicesServlet")
public class ManageServicesServlet extends HttpServlet {
    private ServiceDAO serviceDAO;

    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.serviceDAO = daoFactory.getServiceDAO();
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
            List<Service> services = serviceDAO.findAll();
            request.setAttribute("services", services);
            request.getRequestDispatcher("/admin/manage-services.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error loading services: " + e.getMessage(), e);
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
                String description = request.getParameter("description");
                int estimatedTime = Integer.parseInt(request.getParameter("estimatedTime"));

                Service service = new Service();
                service.setName(name);
                service.setDescription(description);
                service.setEstimatedTime(estimatedTime);
                service.setActive(true);

                serviceDAO.save(service);
                response.sendRedirect(request.getContextPath() + "/admin/ManageServicesServlet?success=added");

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                int estimatedTime = Integer.parseInt(request.getParameter("estimatedTime"));
                boolean active = "on".equals(request.getParameter("active"));

                Service service = serviceDAO.findById(id);
                if (service != null) {
                    service.setName(name);
                    service.setDescription(description);
                    service.setEstimatedTime(estimatedTime);
                    service.setActive(active);
                    serviceDAO.update(service);
                    response.sendRedirect(request.getContextPath() + "/admin/ManageServicesServlet?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/ManageServicesServlet?error=notfound");
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                serviceDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/ManageServicesServlet?success=deleted");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/ManageServicesServlet?error=" + e.getMessage());
        }
    }
}
