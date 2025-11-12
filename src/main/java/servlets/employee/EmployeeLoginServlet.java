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
@WebServlet("/employee/LoginServlet")
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

        try {
            // Use DAO layer for authentication
            Employee employee = employeeDAO.authenticate(email, password);

            if (employee != null) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", employee.getEmail());
                session.setAttribute("userRole", "employee");
                session.setAttribute("userId", employee.getId());
                session.setAttribute("userName", employee.getFirstName() + " " + employee.getLastName());
                session.setAttribute("agencyId", employee.getAgencyId());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
            } else {
                // Login failed
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=Invalid email or password&role=employee");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Login failed&role=employee");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
