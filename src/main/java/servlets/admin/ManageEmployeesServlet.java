package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.DAOFactory;
import dao.EmployeeDAO;
import dao.AgencyDAO;
import dao.ServiceDAO;
import models.Employee;
import models.Agency;
import models.Service;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/ManageEmployeesServlet")
public class ManageEmployeesServlet extends HttpServlet {
    private EmployeeDAO employeeDAO;
    private AgencyDAO agencyDAO;
    private ServiceDAO serviceDAO;

    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.employeeDAO = daoFactory.getEmployeeDAO();
        this.agencyDAO = daoFactory.getAgencyDAO();
        this.serviceDAO = daoFactory.getServiceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
            return;
        }

        try {
            List<Employee> employees = employeeDAO.findAll();
            List<Agency> agencies = agencyDAO.findAll();
            List<Service> services = serviceDAO.findAll();

            request.setAttribute("employees", employees);
            request.setAttribute("agencies", agencies);
            request.setAttribute("services", services);
            request.getRequestDispatcher("/admin/manage-employees.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading employees: " + e.getMessage());
            request.getRequestDispatcher("/admin/manage-employees.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/AdminLoginServlet");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                int agencyId = Integer.parseInt(request.getParameter("agencyId"));
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));
                int counterId = Integer.parseInt(request.getParameter("counterId"));

                Employee employee = new Employee();
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setEmail(email);
                employee.setPassword(password);
                employee.setAgencyId(agencyId);
                employee.setServiceId(serviceId);
                employee.setCounterId(counterId);

                employeeDAO.save(employee);
                response.sendRedirect(request.getContextPath() + "/admin/ManageEmployeesServlet?success=added");

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String email = request.getParameter("email");
                int agencyId = Integer.parseInt(request.getParameter("agencyId"));
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));
                int counterId = Integer.parseInt(request.getParameter("counterId"));

                Employee employee = employeeDAO.findById(id);
                if (employee != null) {
                    employee.setFirstName(firstName);
                    employee.setLastName(lastName);
                    employee.setEmail(email);
                    employee.setAgencyId(agencyId);
                    employee.setServiceId(serviceId);
                    employee.setCounterId(counterId);

                    String password = request.getParameter("password");
                    if (password != null && !password.trim().isEmpty()) {
                        employee.setPassword(password);
                    }

                    employeeDAO.update(employee);
                    response.sendRedirect(request.getContextPath() + "/admin/ManageEmployeesServlet?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/ManageEmployeesServlet?error=notfound");
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                employeeDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/ManageEmployeesServlet?success=deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/ManageEmployeesServlet?error=" + e.getMessage());
        }
    }
}
