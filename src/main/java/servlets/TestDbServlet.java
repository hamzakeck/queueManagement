package servlets;

import dao.factory.DatabaseFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Simple servlet to test database connectivity and show sample data.
 */
public class TestDbServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><head><title>DB Test</title></head><body>");
            out.println("<h2>Database Connection Test</h2>");
            try (Connection conn = DatabaseFactory.getInstance().getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    out.println("<p style='color:green;'>Connected to database successfully.</p>");

                    // Sample query: count agencies and list first few
                    try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM agencies");
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            out.println("<p>Agencies count: " + rs.getInt("cnt") + "</p>");
                        }
                    }

                    out.println("<h3>First agencies (up to 10)</h3>");
                    out.println("<ul>");
                    try (PreparedStatement ps2 = conn.prepareStatement("SELECT id, name, city FROM agencies ORDER BY id LIMIT 10");
                         ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            int id = rs2.getInt("id");
                            String name = rs2.getString("name");
                            String city = rs2.getString("city");
                            out.println("<li>" + id + " - " + escapeHtml(name) + " (" + escapeHtml(city) + ")</li>");
                        }
                    }
                    out.println("</ul>");
                } else {
                    out.println("<p style='color:red;'>Failed to obtain a database connection (null or closed).</p>");
                }
            } catch (SQLException e) {
                out.println("<p style='color:red;'>SQL error: " + escapeHtml(e.getMessage()) + "</p>");
            }

            out.println("<p><a href='" + req.getContextPath() + "'>Back</a></p>");
            out.println("</body></html>");
        }
    }

    // Minimal HTML escape to avoid accidental markup injection in the admin output
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
