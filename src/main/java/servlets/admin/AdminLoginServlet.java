package servlets.admin;

import java.io.IOException;

import dao.AdministratorDAO;
import dao.DAOFactory;
import models.Administrator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Admin Login Servlet - handles authentication for administrators
 */
@WebServlet("/admin/LoginServlet")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdministratorDAO administratorDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        administratorDAO = DAOFactory.getInstance().getAdministratorDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Use DAO layer for authentication
            Administrator admin = administratorDAO.authenticate(email, password);

            if (admin != null) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", admin.getEmail());
                session.setAttribute("userRole", "admin");
                session.setAttribute("userId", admin.getId());
                session.setAttribute("userName", admin.getFirstName() + " " + admin.getLastName());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
            } else {
                // Login failed
                response.sendRedirect(
                        request.getContextPath() + "/login.jsp?error=Invalid email or password&role=admin");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Login failed&role=admin");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
