package servlets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.AdministratorDAO;
import dao.CitizenDAO;
import dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Administrator;
import models.Citizen;
import models.Employee;

class LoginServletTest {

    private LoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AdministratorDAO administratorDAO;
    private EmployeeDAO employeeDAO;
    private CitizenDAO citizenDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new LoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        administratorDAO = mock(AdministratorDAO.class);
        employeeDAO = mock(EmployeeDAO.class);
        citizenDAO = mock(CitizenDAO.class);

        setField(servlet, "administratorDAO", administratorDAO);
        setField(servlet, "employeeDAO", employeeDAO);
        setField(servlet, "citizenDAO", citizenDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void doPostAdminLoginSuccess() throws Exception {
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        Administrator admin = new Administrator();
        admin.setId(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("John");
        admin.setLastName("Doe");
        when(administratorDAO.authenticate("admin@test.com", "pass")).thenReturn(admin);

        servlet.doPost(request, response);

        verify(session).setAttribute("userEmail", "admin@test.com");
        verify(session).setAttribute("userRole", "admin");
        verify(session).setAttribute("userId", 1);
        verify(session).setAttribute("userName", "John Doe");
        verify(response).sendRedirect("/app/admin/index.jsp");
    }

    @Test
    void doPostEmployeeLoginSuccess() throws Exception {
        when(request.getParameter("email")).thenReturn("emp@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        when(administratorDAO.authenticate("emp@test.com", "pass")).thenReturn(null);

        Employee employee = new Employee();
        employee.setId(2);
        employee.setEmail("emp@test.com");
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setAgencyId(5);
        when(employeeDAO.authenticate("emp@test.com", "pass")).thenReturn(employee);

        servlet.doPost(request, response);

        verify(session).setAttribute("userRole", "employee");
        verify(session).setAttribute("agencyId", 5);
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostCitizenLoginSuccess() throws Exception {
        when(request.getParameter("email")).thenReturn("citizen@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        when(administratorDAO.authenticate("citizen@test.com", "pass")).thenReturn(null);
        when(employeeDAO.authenticate("citizen@test.com", "pass")).thenReturn(null);

        Citizen citizen = new Citizen();
        citizen.setId(3);
        citizen.setEmail("citizen@test.com");
        citizen.setFirstName("Bob");
        citizen.setLastName("Lee");
        when(citizenDAO.authenticate("citizen@test.com", "pass")).thenReturn(citizen);

        servlet.doPost(request, response);

        verify(session).setAttribute("userRole", "citizen");
        verify(response).sendRedirect("/app/citizen/index.jsp");
    }

    @Test
    void doPostLoginFailure() throws Exception {
        when(request.getParameter("email")).thenReturn("bad@test.com");
        when(request.getParameter("password")).thenReturn("wrong");

        when(administratorDAO.authenticate("bad@test.com", "wrong")).thenReturn(null);
        when(employeeDAO.authenticate("bad@test.com", "wrong")).thenReturn(null);
        when(citizenDAO.authenticate("bad@test.com", "wrong")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp?error=1");
    }

    @Test
    void doPostExceptionWrapped() throws Exception {
        when(request.getParameter("email")).thenReturn("a@b.com");
        when(request.getParameter("password")).thenReturn("p");
        when(administratorDAO.authenticate(anyString(), anyString()))
                .thenThrow(new RuntimeException("DB down"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doPostSessionTimeoutSet() throws Exception {
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        Administrator admin = new Administrator();
        admin.setId(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("A");
        admin.setLastName("B");
        when(administratorDAO.authenticate("admin@test.com", "pass")).thenReturn(admin);

        servlet.doPost(request, response);

        verify(session).setMaxInactiveInterval(30 * 60);
    }

    @Test
    void doGetRedirectsToLoginPage() throws Exception {
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }
}
