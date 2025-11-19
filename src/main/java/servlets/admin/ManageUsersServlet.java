package servlets.admin;

import java.io.IOException;
import java.util.List;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.EmployeeDAO;
import dao.AgencyDAO;
import dao.ServiceDAO;
import dao.DAOFactory;
import dao.DAOException;
import models.Administrator;
import models.Citizen;
import models.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet to manage all users (view and change roles)
 */
@WebServlet("/admin/ManageUsersServlet")
public class ManageUsersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdministratorDAO administratorDAO;
    private EmployeeDAO employeeDAO;
    private CitizenDAO citizenDAO;
    private AgencyDAO agencyDAO;
    private ServiceDAO serviceDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DAOFactory factory = DAOFactory.getInstance();
        administratorDAO = factory.getAdministratorDAO();
        employeeDAO = factory.getEmployeeDAO();
        citizenDAO = factory.getCitizenDAO();
        agencyDAO = factory.getAgencyDAO();
        serviceDAO = factory.getServiceDAO();
    }

    /**
     * GET: Load all users and display manage-users.jsp
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
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
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/manage-users.jsp?error=Failed to load users");
        }
    }

    /**
     * POST: Handle role changes
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
            return;
        }

        String action = request.getParameter("action");

        if ("changeRole".equals(action)) {
            handleChangeRole(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=Invalid action");
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
            String firstName = null;
            String lastName = null;
            String email = null;
            String password = null;

            if ("citizen".equals(currentRole)) {
                Citizen citizen = citizenDAO.findById(userId);
                if (citizen == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=User not found");
                    return;
                }
                firstName = citizen.getFirstName();
                lastName = citizen.getLastName();
                email = citizen.getEmail();
                password = citizen.getPassword();
            } else if ("employee".equals(currentRole)) {
                Employee employee = employeeDAO.findById(userId);
                if (employee == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=User not found");
                    return;
                }
                firstName = employee.getFirstName();
                lastName = employee.getLastName();
                email = employee.getEmail();
                password = employee.getPassword();
            } else if ("admin".equals(currentRole)) {
                Administrator admin = administratorDAO.findById(userId);
                if (admin == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=User not found");
                    return;
                }
                firstName = admin.getFirstName();
                lastName = admin.getLastName();
                email = admin.getEmail();
                password = admin.getPassword();
            }

            // If same role, no change needed
            if (currentRole.equals(newRole)) {
                response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?success=No changes made");
                return;
            }

            // Create new user in target role table
            if ("citizen".equals(newRole)) {
                String cin = request.getParameter("cin");
                if (cin == null || cin.trim().isEmpty()) {
                    response.sendRedirect(
                            request.getContextPath() + "/admin/ManageUsersServlet?error=CIN is required for citizens");
                    return;
                }

                Citizen newCitizen = new Citizen();
                newCitizen.setFirstName(firstName);
                newCitizen.setLastName(lastName);
                newCitizen.setEmail(email);
                newCitizen.setPassword(password);
                newCitizen.setCin(cin);
                citizenDAO.create(newCitizen);

            } else if ("employee".equals(newRole)) {
                String agencyIdStr = request.getParameter("agencyId");
                String serviceIdStr = request.getParameter("serviceId");
                String counterIdStr = request.getParameter("counterId");

                if (agencyIdStr == null || agencyIdStr.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath()
                            + "/admin/ManageUsersServlet?error=Agency ID is required for employees");
                    return;
                }
                if (serviceIdStr == null || serviceIdStr.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath()
                            + "/admin/ManageUsersServlet?error=Service ID is required for employees");
                    return;
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

            } else if ("admin".equals(newRole)) {
                Administrator newAdmin = new Administrator();
                newAdmin.setFirstName(firstName);
                newAdmin.setLastName(lastName);
                newAdmin.setEmail(email);
                newAdmin.setPassword(password);
                administratorDAO.create(newAdmin);
            }

            // Delete user from old role table
            if ("citizen".equals(currentRole)) {
                citizenDAO.delete(userId);
            } else if ("employee".equals(currentRole)) {
                employeeDAO.delete(userId);
            } else if ("admin".equals(currentRole)) {
                administratorDAO.delete(userId);
            }

            response.sendRedirect(
                    request.getContextPath() + "/admin/ManageUsersServlet?success=User role changed successfully");

        } catch (DAOException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=Failed to change role: "
                    + e.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=Invalid numeric value");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/ManageUsersServlet?error=An error occurred");
        }
    }
}
