package servlets.employee;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Employee;

class EmployeeLoginServletTest {

    private EmployeeLoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private EmployeeDAO employeeDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new EmployeeLoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        employeeDAO = mock(EmployeeDAO.class);

        Field field = EmployeeLoginServlet.class.getDeclaredField("employeeDAO");
        field.setAccessible(true);
        field.set(servlet, employeeDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostSuccessfulLogin() throws Exception {
        when(request.getParameter("email")).thenReturn("emp@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        Employee record = new Employee();
        record.setId(1);
        record.setEmail("emp@test.com");
        when(employeeDAO.findByEmail("emp@test.com")).thenReturn(record);

        Employee authenticated = new Employee();
        authenticated.setId(1);
        authenticated.setEmail("emp@test.com");
        authenticated.setFirstName("Jane");
        authenticated.setLastName("Smith");
        authenticated.setAgencyId(5);
        when(employeeDAO.authenticate("emp@test.com", "pass")).thenReturn(authenticated);

        servlet.doPost(request, response);

        verify(session).setAttribute("userEmail", "emp@test.com");
        verify(session).setAttribute("userRole", "employee");
        verify(session).setAttribute("userId", 1);
        verify(session).setAttribute("userName", "Jane Smith");
        verify(session).setAttribute("agencyId", 5);
        verify(session).setAttribute("employeeId", 1);
        verify(session).setAttribute("employeeEmail", "emp@test.com");
        verify(session).setMaxInactiveInterval(30 * 60);
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostEmailNotFound() throws Exception {
        when(request.getParameter("email")).thenReturn("unknown@test.com");
        when(request.getParameter("password")).thenReturn("pass");
        when(employeeDAO.findByEmail("unknown@test.com")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp?error=Email not found&role=employee");
    }

    @Test
    void doPostWrongPassword() throws Exception {
        when(request.getParameter("email")).thenReturn("emp@test.com");
        when(request.getParameter("password")).thenReturn("wrong");

        Employee record = new Employee();
        when(employeeDAO.findByEmail("emp@test.com")).thenReturn(record);
        when(employeeDAO.authenticate("emp@test.com", "wrong")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp?error=Incorrect password&role=employee");
    }

    @Test
    void doPostExceptionWrapped() throws Exception {
        when(request.getParameter("email")).thenReturn("a@b.com");
        when(request.getParameter("password")).thenReturn("p");
        when(employeeDAO.findByEmail("a@b.com")).thenThrow(new RuntimeException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doGetRedirectsToLoginPage() throws Exception {
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }
}
