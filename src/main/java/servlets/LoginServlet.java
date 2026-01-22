package servlets;

import java.io.IOException;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.EmployeeDAO;
import dao.DAOFactory;
import models.Administrator;
import models.Citizen;
import models.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Generic Login Servlet - automatically detects user role based on email
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdministratorDAO administratorDAO;
    private EmployeeDAO employeeDAO;
    private CitizenDAO citizenDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DAOFactory factory = DAOFactory.getInstance();
        administratorDAO = factory.getAdministratorDAO();
        employeeDAO = factory.getEmployeeDAO();
        citizenDAO = factory.getCitizenDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            HttpSession session = request.getSession();
            boolean authenticated = false;

            // Try to authenticate as admin first
            Administrator admin = administratorDAO.authenticate(email, password);
            if (admin != null) {
                authenticated = true;
                session.setAttribute("userEmail", admin.getEmail());
                session.setAttribute("userRole", "admin");
                session.setAttribute("userId", admin.getId());
                session.setAttribute("userName", admin.getFirstName() + " " + admin.getLastName());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
                return;
            }

            // Try to authenticate as employee
            Employee employee = employeeDAO.authenticate(email, password);
            if (employee != null) {
                authenticated = true;
                session.setAttribute("userEmail", employee.getEmail());
                session.setAttribute("userRole", "employee");
                session.setAttribute("userId", employee.getId());
                session.setAttribute("userName", employee.getFirstName() + " " + employee.getLastName());
                session.setAttribute("agencyId", employee.getAgencyId());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
                return;
            }

            // Try to authenticate as citizen
            Citizen citizen = citizenDAO.authenticate(email, password);
            if (citizen != null) {
                authenticated = true;
                session.setAttribute("userEmail", citizen.getEmail());
                session.setAttribute("userRole", "citizen");
                session.setAttribute("userId", citizen.getId());
                session.setAttribute("userName", citizen.getFirstName() + " " + citizen.getLastName());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                response.sendRedirect(request.getContextPath() + "/citizen/index.jsp");
                return;
            }

            // Login failed - no matching credentials found
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
