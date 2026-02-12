package servlets.admin;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.AdministratorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Administrator;

class AdminLoginServletTest {

    private AdminLoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AdministratorDAO administratorDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminLoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        administratorDAO = mock(AdministratorDAO.class);

        Field field = AdminLoginServlet.class.getDeclaredField("administratorDAO");
        field.setAccessible(true);
        field.set(servlet, administratorDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostSuccessfulLogin() throws Exception {
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        Administrator admin = new Administrator();
        admin.setId(1);
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        when(administratorDAO.authenticate("admin@test.com", "pass")).thenReturn(admin);

        servlet.doPost(request, response);

        verify(session).setAttribute("userEmail", "admin@test.com");
        verify(session).setAttribute("userRole", "admin");
        verify(session).setAttribute("userId", 1);
        verify(session).setAttribute("userName", "Admin User");
        verify(session).setAttribute("adminId", 1);
        verify(session).setAttribute("adminEmail", "admin@test.com");
        verify(session).setMaxInactiveInterval(30 * 60);
        verify(response).sendRedirect("/app/admin/index.jsp");
    }

    @Test
    void doPostFailedLogin() throws Exception {
        when(request.getParameter("email")).thenReturn("bad@test.com");
        when(request.getParameter("password")).thenReturn("wrong");
        when(administratorDAO.authenticate("bad@test.com", "wrong")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp?error=Invalid email or password&role=admin");
    }

    @Test
    void doPostExceptionWrapped() throws Exception {
        when(request.getParameter("email")).thenReturn("a@b.com");
        when(request.getParameter("password")).thenReturn("p");
        when(administratorDAO.authenticate("a@b.com", "p")).thenThrow(new RuntimeException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doGetRedirectsToLoginPage() throws Exception {
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }
}
