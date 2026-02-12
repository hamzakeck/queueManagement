package servlets.admin;

import java.io.IOException;
import java.util.List;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.DAOException;
import dao.DAOFactory;
import dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Administrator;
import models.Citizen;
import models.Employee;

/**
 * Servlet to manage all users (view and change roles)
 */
@WebServlet("/admin/ManageUsersServlet")
public class ManageUsersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_CITIZEN = "citizen";
    private static final String ROLE_EMPLOYEE = "employee";
    private static final String REDIRECT_BASE = "/admin/ManageUsersServlet";
    private static final String REDIRECT_USER_NOT_FOUND = "/admin/ManageUsersServlet?error=User not found";
    private transient AdministratorDAO administratorDAO;
    private transient EmployeeDAO employeeDAO;
    private transient CitizenDAO citizenDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DAOFactory factory = DAOFactory.getInstance();
        administratorDAO = factory.getAdministratorDAO();
        employeeDAO = factory.getEmployeeDAO();
        citizenDAO = factory.getCitizenDAO();
    }

    /**
     * GET: Load all users and display manage-users.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !ROLE_ADMIN.equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
            return;
        }

        try {
            // Load all users from all three tables
            List<Citizen> citizens = citizenDAO.findAll();
            List<Employee> employees = employeeDAO.findAll();
            List<Administrator> admins = administratorDAO.findAll();

            request.setAttribute("citizens", citizens);
            request.setAttribute("employees", employees);
            request.setAttribute("admins", admins);

            request.getRequestDispatcher("/admin/manage-users.jsp").forward(request, response);
        } catch (DAOException e) {
            throw new ServletException("Failed to load users: " + e.getMessage(), e);
        }
    }

    /**
     * POST: Handle role changes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !ROLE_ADMIN.equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
            return;
        }

        String action = request.getParameter("action");

        if ("changeRole".equals(action)) {
            handleChangeRole(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + REDIRECT_BASE + "?error=Invalid action");
        }
    }

    /**
     * Handle changing a user's role
     */
    private void handleChangeRole(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String currentRole = request.getParameter("currentRole");
            String newRole = request.getParameter("newRole");

            // Fetch user data from current role table
            String[] userData = fetchUserData(userId, currentRole);
            if (userData.length == 0) {
                response.sendRedirect(request.getContextPath() + REDIRECT_USER_NOT_FOUND);
                return;
            }

            // If same role, no change needed
            if (currentRole.equals(newRole)) {
                response.sendRedirect(request.getContextPath() + REDIRECT_BASE + "?success=No changes made");
                return;
            }

            // Create new user in target role table
            String createError = createUserInRole(request, newRole, userData);
            if (createError != null) {
                response.sendRedirect(request.getContextPath() + REDIRECT_BASE + "?error=" + createError);
                return;
            }

            // Delete user from old role table
            deleteUserFromRole(userId, currentRole);

            response.sendRedirect(
                    request.getContextPath() + REDIRECT_BASE + "?success=User role changed successfully");

        } catch (DAOException e) {
            response.sendRedirect(request.getContextPath() + REDIRECT_BASE + "?error=Failed to change role: "
                    + e.getMessage());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + REDIRECT_BASE + "?error=Invalid numeric value");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + REDIRECT_BASE + "?error=An error occurred");
        }
    }

    /**
     * Fetch user data (firstName, lastName, email, password) from the role-specific
     * table.
     * Returns empty array if user not found or role is unknown.
     */
    private String[] fetchUserData(int userId, String role) throws DAOException {
        if (ROLE_CITIZEN.equals(role)) {
            Citizen citizen = citizenDAO.findById(userId);
            return citizen == null ? new String[0]
                    : new String[] { citizen.getFirstName(), citizen.getLastName(), citizen.getEmail(),
                            citizen.getPassword() };
        } else if (ROLE_EMPLOYEE.equals(role)) {
            Employee employee = employeeDAO.findById(userId);
            return employee == null ? new String[0]
                    : new String[] { employee.getFirstName(), employee.getLastName(), employee.getEmail(),
                            employee.getPassword() };
        } else if (ROLE_ADMIN.equals(role)) {
            Administrator admin = administratorDAO.findById(userId);
            return admin == null ? new String[0]
                    : new String[] { admin.getFirstName(), admin.getLastName(), admin.getEmail(),
                            admin.getPassword() };
        }
        // Unknown role
        return new String[0];
    }

    /**
     * Create a user in the target role table. Returns an error message if
     * validation fails, null on success.
     */
    private String createUserInRole(HttpServletRequest request, String newRole, String[] userData)
            throws DAOException {
        String firstName = userData[0];
        String lastName = userData[1];
        String email = userData[2];
        String password = userData[3];

        if (ROLE_CITIZEN.equals(newRole)) {
            return createCitizen(request, firstName, lastName, email, password);
        } else if (ROLE_EMPLOYEE.equals(newRole)) {
            return createEmployee(request, firstName, lastName, email, password);
        } else if (ROLE_ADMIN.equals(newRole)) {
            Administrator newAdmin = new Administrator();
            newAdmin.setFirstName(firstName);
            newAdmin.setLastName(lastName);
            newAdmin.setEmail(email);
            newAdmin.setPassword(password);
            administratorDAO.create(newAdmin);
        }
        return null;
    }

    private String createCitizen(HttpServletRequest request, String firstName, String lastName,
            String email, String password) throws DAOException {
        String cin = request.getParameter("cin");
        if (cin == null || cin.trim().isEmpty()) {
            return "CIN is required for citizens";
        }
        Citizen newCitizen = new Citizen();
        newCitizen.setFirstName(firstName);
        newCitizen.setLastName(lastName);
        newCitizen.setEmail(email);
        newCitizen.setPassword(password);
        newCitizen.setCin(cin);
        citizenDAO.create(newCitizen);
        return null;
    }

    private String createEmployee(HttpServletRequest request, String firstName, String lastName,
            String email, String password) throws DAOException {
        String agencyIdStr = request.getParameter("agencyId");
        String serviceIdStr = request.getParameter("serviceId");
        String counterIdStr = request.getParameter("counterId");

        if (agencyIdStr == null || agencyIdStr.trim().isEmpty()) {
            return "Agency ID is required for employees";
        }
        if (serviceIdStr == null || serviceIdStr.trim().isEmpty()) {
            return "Service ID is required for employees";
        }

        int agencyId = Integer.parseInt(agencyIdStr);
        int serviceId = Integer.parseInt(serviceIdStr);
        int counterId = 0;
        if (counterIdStr != null && !counterIdStr.trim().isEmpty()) {
            counterId = Integer.parseInt(counterIdStr);
        }

        Employee newEmployee = new Employee();
        newEmployee.setFirstName(firstName);
        newEmployee.setLastName(lastName);
        newEmployee.setEmail(email);
        newEmployee.setPassword(password);
        newEmployee.setAgencyId(agencyId);
        newEmployee.setServiceId(serviceId);
        newEmployee.setCounterId(counterId);
        employeeDAO.create(newEmployee);
        return null;
    }

    /**
     * Delete a user from their current role table.
     */
    private void deleteUserFromRole(int userId, String role) throws DAOException {
        if (ROLE_CITIZEN.equals(role)) {
            citizenDAO.delete(userId);
        } else if (ROLE_EMPLOYEE.equals(role)) {
            employeeDAO.delete(userId);
        } else if (ROLE_ADMIN.equals(role)) {
            administratorDAO.delete(userId);
        }
    }
}
