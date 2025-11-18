package servlets.citizen;

import java.io.IOException;

import dao.CitizenDAO;
import dao.DAOFactory;
import models.Citizen;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Citizen Login Servlet - handles authentication for citizens
 */
@WebServlet("/citizen/CitizenLoginServlet")
public class CitizenLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CitizenDAO citizenDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        citizenDAO = DAOFactory.getInstance().getCitizenDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Use DAO layer for authentication
            Citizen citizen = citizenDAO.authenticate(email, password);

            if (citizen != null) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", citizen.getEmail());
                session.setAttribute("userRole", "citizen");
                session.setAttribute("userId", citizen.getId());
                session.setAttribute("userName", citizen.getFirstName() + " " + citizen.getLastName());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                response.sendRedirect(request.getContextPath() + "/citizen/index.jsp");
            } else {
                // Login failed
                response.sendRedirect(
                        request.getContextPath() + "/login.jsp?error=Invalid email or password&role=citizen");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Login failed&role=citizen");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
