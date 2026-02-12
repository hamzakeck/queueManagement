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
import dao.DAOException;
import dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Citizen;

class RegisterServletTest {

    private RegisterServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AdministratorDAO administratorDAO;
    private EmployeeDAO employeeDAO;
    private CitizenDAO citizenDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new RegisterServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        administratorDAO = mock(AdministratorDAO.class);
        employeeDAO = mock(EmployeeDAO.class);
        citizenDAO = mock(CitizenDAO.class);

        setField(servlet, "administratorDAO", administratorDAO);
        setField(servlet, "employeeDAO", employeeDAO);
        setField(servlet, "citizenDAO", citizenDAO);

        when(request.getContextPath()).thenReturn("/app");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void doPostSuccessfulRegistration() throws Exception {
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("email")).thenReturn("john@test.com");
        when(request.getParameter("password")).thenReturn("pass123");
        when(request.getParameter("cin")).thenReturn("AB123456");

        when(administratorDAO.findByEmail("john@test.com")).thenReturn(null);
        when(employeeDAO.findByEmail("john@test.com")).thenReturn(null);
        when(citizenDAO.findByEmail("john@test.com")).thenReturn(null);

        servlet.doPost(request, response);

        verify(citizenDAO).create(org.mockito.ArgumentMatchers.any(Citizen.class));
        verify(response).sendRedirect("/app/login.jsp?success=Registration successful");
    }

    @Test
    void doPostDuplicateEmailRedirects() throws Exception {
        when(request.getParameter("email")).thenReturn("dup@test.com");
        when(request.getParameter("password")).thenReturn("p");
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("B");
        when(request.getParameter("cin")).thenReturn("X");

        Citizen existing = new Citizen();
        when(citizenDAO.findByEmail("dup@test.com")).thenReturn(existing);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/register.jsp?error=Email already exists");
    }

    @Test
    void doPostMissingCinRedirects() throws Exception {
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("B");
        when(request.getParameter("email")).thenReturn("new@test.com");
        when(request.getParameter("password")).thenReturn("p");
        when(request.getParameter("cin")).thenReturn("");

        when(administratorDAO.findByEmail("new@test.com")).thenReturn(null);
        when(employeeDAO.findByEmail("new@test.com")).thenReturn(null);
        when(citizenDAO.findByEmail("new@test.com")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/register.jsp?error=CIN is required");
    }

    @Test
    void doPostNullCinRedirects() throws Exception {
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("B");
        when(request.getParameter("email")).thenReturn("new@test.com");
        when(request.getParameter("password")).thenReturn("p");
        when(request.getParameter("cin")).thenReturn(null);

        when(administratorDAO.findByEmail("new@test.com")).thenReturn(null);
        when(employeeDAO.findByEmail("new@test.com")).thenReturn(null);
        when(citizenDAO.findByEmail("new@test.com")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/register.jsp?error=CIN is required");
    }

    @Test
    void doPostDAOExceptionWrapped() throws Exception {
        when(request.getParameter("email")).thenReturn("a@b.com");
        when(request.getParameter("password")).thenReturn("p");
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("B");
        when(request.getParameter("cin")).thenReturn("C");

        when(administratorDAO.findByEmail(anyString())).thenThrow(new DAOException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doGetRedirectsToRegisterPage() throws Exception {
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/register.jsp");
    }
}
