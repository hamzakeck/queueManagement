package servlets.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.EmployeeDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Administrator;
import models.Citizen;
import models.Employee;

class ManageUsersServletTest {

    private ManageUsersServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AdministratorDAO administratorDAO;
    private EmployeeDAO employeeDAO;
    private CitizenDAO citizenDAO;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManageUsersServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        administratorDAO = mock(AdministratorDAO.class);
        employeeDAO = mock(EmployeeDAO.class);
        citizenDAO = mock(CitizenDAO.class);
        dispatcher = mock(RequestDispatcher.class);

        setField("administratorDAO", administratorDAO);
        setField("employeeDAO", employeeDAO);
        setField("citizenDAO", citizenDAO);

        when(request.getContextPath()).thenReturn("/app");
        when(request.getSession(false)).thenReturn(session);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = ManageUsersServlet.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(servlet, value);
    }

    // ---- doGet tests ----

    @Test
    void doGetLoadsAllUsers() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(citizenDAO.findAll()).thenReturn(Arrays.asList(new Citizen()));
        when(employeeDAO.findAll()).thenReturn(Arrays.asList(new Employee()));
        when(administratorDAO.findAll()).thenReturn(Arrays.asList(new Administrator()));
        when(request.getRequestDispatcher("/admin/manage-users.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetRedirectsNonAdmin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("citizen");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/admin/AdminLoginServlet");
    }

    @Test
    void doGetRedirectsNullSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/admin/AdminLoginServlet");
    }

    // ---- doPost tests ----

    @Test
    void doPostRedirectsNonAdmin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("employee");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/AdminLoginServlet");
    }

    @Test
    void doPostInvalidAction() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("unknown");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageUsersServlet?error=Invalid action");
    }

    @Test
    void doPostChangeRoleSameRole() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("changeRole");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("currentRole")).thenReturn("citizen");
        when(request.getParameter("newRole")).thenReturn("citizen");

        Citizen citizen = new Citizen();
        citizen.setFirstName("A");
        citizen.setLastName("B");
        citizen.setEmail("a@b.com");
        citizen.setPassword("p");
        when(citizenDAO.findById(1)).thenReturn(citizen);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageUsersServlet?success=No changes made");
    }

    @Test
    void doPostChangeRoleCitizenToAdmin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("changeRole");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("currentRole")).thenReturn("citizen");
        when(request.getParameter("newRole")).thenReturn("admin");

        Citizen citizen = new Citizen();
        citizen.setFirstName("John");
        citizen.setLastName("Doe");
        citizen.setEmail("john@test.com");
        citizen.setPassword("pass");
        when(citizenDAO.findById(1)).thenReturn(citizen);

        servlet.doPost(request, response);

        verify(administratorDAO).create(any(Administrator.class));
        verify(citizenDAO).delete(1);
        verify(response).sendRedirect("/app/admin/ManageUsersServlet?success=User role changed successfully");
    }

    @Test
    void doPostChangeRoleUserNotFound() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("changeRole");
        when(request.getParameter("userId")).thenReturn("999");
        when(request.getParameter("currentRole")).thenReturn("citizen");
        when(request.getParameter("newRole")).thenReturn("admin");

        when(citizenDAO.findById(999)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageUsersServlet?error=User not found");
    }

    @Test
    void doPostChangeRoleCitizenMissingCin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("changeRole");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("currentRole")).thenReturn("admin");
        when(request.getParameter("newRole")).thenReturn("citizen");
        when(request.getParameter("cin")).thenReturn("");

        Administrator admin = new Administrator();
        admin.setFirstName("A");
        admin.setLastName("B");
        admin.setEmail("a@b.com");
        admin.setPassword("p");
        when(administratorDAO.findById(1)).thenReturn(admin);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageUsersServlet?error=CIN is required for citizens");
    }
}
