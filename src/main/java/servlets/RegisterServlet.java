package servlets;

import java.io.IOException;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.EmployeeDAO;
import dao.DAOFactory;
import dao.DAOException;
import models.Administrator;
import models.Citizen;
import models.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Register Servlet - handles registration for all three roles
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
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
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String cin = request.getParameter("cin");

        try {
            // Check if email already exists across all user types
            if (administratorDAO.findByEmail(email) != null ||
                    employeeDAO.findByEmail(email) != null ||
                    citizenDAO.findByEmail(email) != null) {
                response.sendRedirect(request.getContextPath() + "/register.jsp?error=Email already exists");
                return;
            }

            // Validate CIN
            if (cin == null || cin.trim().isEmpty()) {
                response.sendRedirect(
                        request.getContextPath() + "/register.jsp?error=CIN is required");
                return;
            }

            // Register as citizen by default
            Citizen citizen = new Citizen();
            citizen.setFirstName(firstName);
            citizen.setLastName(lastName);
            citizen.setEmail(email);
            citizen.setPassword(password);
            citizen.setCin(cin);
            citizenDAO.create(citizen);

            // Registration successful
            response.sendRedirect(request.getContextPath() + "/login.jsp?success=Registration successful");

        } catch (DAOException e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/register.jsp?error=Registration failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register.jsp?error=Invalid agency ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register.jsp?error=Registration failed");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/register.jsp");
    }
}
