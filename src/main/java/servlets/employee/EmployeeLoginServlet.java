package servlets.employee;

import java.io.IOException;

import dao.EmployeeDAO;
import dao.DAOFactory;
import models.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Employee Login Servlet - handles authentication for employees
 */
@WebServlet("/employee/EmployeeLoginServlet")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        employeeDAO = DAOFactory.getInstance().getEmployeeDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("EmployeeLoginServlet - Attempting login for email: " + email);

        try {
            // Use DAO layer for authentication
            Employee employeeRecord = employeeDAO.findByEmail(email);
            if (employeeRecord == null) {
                System.out.println("EmployeeLoginServlet - Email not found: " + email);
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=Email not found&role=employee");
                return;
            }
            Employee employee = employeeDAO.authenticate(email, password);
            if (employee != null) {
                // Login successful
                System.out.println("EmployeeLoginServlet - Login successful for: " + email);
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", employee.getEmail());
                session.setAttribute("userRole", "employee");
                session.setAttribute("userId", employee.getId());
                session.setAttribute("userName", employee.getFirstName() + " " + employee.getLastName());
                session.setAttribute("agencyId", employee.getAgencyId());
                // Backward compatibility for any pages/logic still expecting employeeId/employeeEmail
                session.setAttribute("employeeId", employee.getId());
                session.setAttribute("employeeEmail", employee.getEmail());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
            } else {
                // Password mismatch
                System.out.println("EmployeeLoginServlet - Password mismatch for: " + email);
                response.sendRedirect(
                        request.getContextPath() + "/login.jsp?error=Incorrect password&role=employee");
            }
        } catch (Exception e) {
            System.out.println("EmployeeLoginServlet - Exception during login: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Login failed&role=employee");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
