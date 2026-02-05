package servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;

import dao.AdministratorDAO;
import dao.DAOFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Administrator;

/**
 * Test servlet to check admin authentication
 */
@WebServlet("/admin/TestAdminServlet")
public class TestAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Admin Authentication Test</h2>");

        try {
            AdministratorDAO adminDAO = DAOFactory.getInstance().getAdministratorDAO();

            // Test finding by email
            out.println("<h3>Test 1: Find admin by email</h3>");
            Administrator admin = adminDAO.findByEmail("admin@queue.com");
            if (admin != null) {
                out.println("<p style='color:green'>✓ Admin found: " + admin.getEmail() + "</p>");
                out.println("<p>Name: " + admin.getFirstName() + " " + admin.getLastName() + "</p>");
                out.println("<p>ID: " + admin.getId() + "</p>");
            } else {
                out.println("<p style='color:red'>✗ Admin not found in database</p>");
            }

            // Test authentication
            out.println("<h3>Test 2: Authenticate admin</h3>");
            Administrator authAdmin = adminDAO.authenticate("admin@queue.com", "admin123");
            if (authAdmin != null) {
                out.println("<p style='color:green'>✓ Authentication successful</p>");
                out.println("<p>Admin: " + authAdmin.getFirstName() + " " + authAdmin.getLastName() + "</p>");
            } else {
                out.println("<p style='color:red'>✗ Authentication failed</p>");
            }

            // List all admins
            out.println("<h3>Test 3: List all administrators</h3>");
            out.println("<p>Total admins: " + adminDAO.findAll().size() + "</p>");
            for (Administrator a : adminDAO.findAll()) {
                out.println("<p>- " + a.getEmail() + " (ID: " + a.getId() + ")</p>");
            }

        } catch (Exception e) {
            out.println("<h3 style='color:red'>Error:</h3>");
            out.println("<pre>" + e.getClass().getName() + ": " + e.getMessage() + "</pre>");
        }

        out.println("</body></html>");
    }
}
