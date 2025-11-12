package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.factory.DatabaseFactory;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String role = request.getParameter("role");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Connection conn = DatabaseFactory.getInstance().getConnection();

            // Check if email already exists
            String checkQuery = "SELECT email FROM administrators WHERE email = ? " +
                    "UNION SELECT email FROM employees WHERE email = ? " +
                    "UNION SELECT email FROM citizens WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            checkStmt.setString(2, email);
            checkStmt.setString(3, email);
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                response.sendRedirect(request.getContextPath() + "/register.jsp?error=Email already exists");
                rs.close();
                checkStmt.close();
                conn.close();
                return;
            }
            rs.close();
            checkStmt.close();

            String insertQuery = "";
            PreparedStatement insertStmt = null;

            if ("admin".equals(role)) {
                insertQuery = "INSERT INTO administrators (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password);
            } else if ("employee".equals(role)) {
                String agencyId = request.getParameter("agencyId");
                insertQuery = "INSERT INTO employees (first_name, last_name, email, password, agency_id) VALUES (?, ?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password);
                insertStmt.setInt(5, Integer.parseInt(agencyId));
            } else if ("citizen".equals(role)) {
                String cin = request.getParameter("cin");
                insertQuery = "INSERT INTO citizens (first_name, last_name, email, password, cin) VALUES (?, ?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password);
                insertStmt.setString(5, cin);
            }

            if (insertStmt != null) {
                insertStmt.executeUpdate();
                insertStmt.close();
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=Registration successful");
            }

            conn.close();
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
