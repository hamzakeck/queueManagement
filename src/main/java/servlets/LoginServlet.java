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
import jakarta.servlet.http.HttpSession;

/**
 * Generic Login Servlet - kept for backward compatibility
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            Connection conn = DatabaseFactory.getInstance().getConnection();
            String query = "";
            String tableName = "";

            // Determine which table to query based on role
            if ("admin".equals(role)) {
                tableName = "administrators";
            } else if ("employee".equals(role)) {
                tableName = "employees";
            } else if ("citizen".equals(role)) {
                tableName = "citizens";
            }

            query = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Login successful - create session
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", email);
                session.setAttribute("userRole", role);
                session.setAttribute("userId", rs.getInt("id"));
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                // Redirect to appropriate dashboard
                if ("admin".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
                } else if ("employee".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/employee/index.jsp");
                } else if ("citizen".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/citizen/index.jsp");
                }
            } else {
                // Login failed
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=Invalid email or password");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Login failed");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
