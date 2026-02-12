package servlets;

import java.io.IOException;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.DAOFactory;
import dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Administrator;
import models.Citizen;
import models.Employee;

/**
 * Generic Login Servlet - automatically detects user role based on email
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_USER_EMAIL = "userEmail";
    private static final String ATTR_USER_ROLE = "userRole";
    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_USER_NAME = "userName";
    private transient AdministratorDAO administratorDAO;
    private transient EmployeeDAO employeeDAO;
    private transient CitizenDAO citizenDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DAOFactory factory = DAOFactory.getInstance();
        administratorDAO = factory.getAdministratorDAO();
        employeeDAO = factory.getEmployeeDAO();
        citizenDAO = factory.getCitizenDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            HttpSession session = request.getSession();

            // Try to authenticate as admin first
            Administrator admin = administratorDAO.authenticate(email, password);
            if (admin != null) {
                session.setAttribute(ATTR_USER_EMAIL, admin.getEmail());
                session.setAttribute(ATTR_USER_ROLE, "admin");
                session.setAttribute(ATTR_USER_ID, admin.getId());
                session.setAttribute(ATTR_USER_NAME, admin.getFirstName() + " " + admin.getLastName());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
                return;
            }

            // Try to authenticate as employee
            Employee employee = employeeDAO.authenticate(email, password);
            if (employee != null) {
                session.setAttribute(ATTR_USER_EMAIL, employee.getEmail());
                session.setAttribute(ATTR_USER_ROLE, "employee");
                session.setAttribute(ATTR_USER_ID, employee.getId());
                session.setAttribute(ATTR_USER_NAME, employee.getFirstName() + " " + employee.getLastName());
                session.setAttribute("agencyId", employee.getAgencyId());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
                return;
            }

            // Try to authenticate as citizen
            Citizen citizen = citizenDAO.authenticate(email, password);
            if (citizen != null) {
                session.setAttribute(ATTR_USER_EMAIL, citizen.getEmail());
                session.setAttribute(ATTR_USER_ROLE, "citizen");
                session.setAttribute(ATTR_USER_ID, citizen.getId());
                session.setAttribute(ATTR_USER_NAME, citizen.getFirstName() + " " + citizen.getLastName());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                response.sendRedirect(request.getContextPath() + "/citizen/index.jsp");
                return;
            }

            // Login failed - no matching credentials found
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");

        } catch (Exception e) {
            throw new ServletException("Login failed due to an unexpected error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
