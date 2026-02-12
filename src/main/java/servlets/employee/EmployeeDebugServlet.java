package servlets.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dao.DAOException;
import dao.DAOFactory;
import dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Employee;

/**
 * Temporary diagnostic servlet to inspect employees in DB.
 * Remove in production.
 */
@WebServlet("/employee/EmployeeDebugServlet")
public class EmployeeDebugServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TD_SEPARATOR = "</td><td>";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Employee Debug</title></head><body>");
        out.println("<h2>Employee Table Dump</h2>");
        try {
            EmployeeDAO dao = DAOFactory.getInstance().getEmployeeDAO();
            List<Employee> all = dao.findAll();
            out.println("<p>Total employees: " + all.size() + "</p>");
            out.println("<table border='1' cellpadding='6' style='border-collapse:collapse;font-family:monospace'>");
            out.println("<tr><th>ID</th><th>Email</th><th>Name</th><th>Agency</th><th>Service</th><th>Counter</th></tr>");
            for (Employee e : all) {
                out.println("<tr><td>" + e.getId() + TD_SEPARATOR + e.getEmail() + TD_SEPARATOR + e.getFirstName() + " " + e.getLastName() + TD_SEPARATOR + e.getAgencyId() + TD_SEPARATOR + e.getServiceId() + TD_SEPARATOR + e.getCounterId() + "</td></tr>");
            }
            out.println("</table>");

            // If email parameter supplied, show auth attempt details
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if (email != null && password != null) {
                out.println("<h3>Authentication Test</h3>");
                Employee byEmail = dao.findByEmail(email);
                if (byEmail == null) {
                    out.println("<p style='color:red'>Email not found: " + email + "</p>");
                } else {
                    out.println("<p>Found employee ID " + byEmail.getId() + " service=" + byEmail.getServiceId() + " agency=" + byEmail.getAgencyId() + "</p>");
                    Employee auth = dao.authenticate(email, password);
                    if (auth != null) {
                        out.println("<p style='color:green'>Authentication SUCCESS.</p>");
                    } else {
                        out.println("<p style='color:orange'>Password mismatch.</p>");
                    }
                }
            }
        } catch (DAOException ex) {
            out.println("<p style='color:red'>DAOException: " + ex.getMessage() + "</p>");
        }
        out.println("<hr><form method='get'>Test auth: <input name='email' placeholder='email'> <input name='password' placeholder='password'> <button type='submit'>Test</button></form>");
        out.println("</body></html>");
    }
}
