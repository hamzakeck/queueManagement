package servlets.citizen;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.CitizenDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Citizen;

class CitizenLoginServletTest {

    private CitizenLoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private CitizenDAO citizenDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CitizenLoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        citizenDAO = mock(CitizenDAO.class);

        Field field = CitizenLoginServlet.class.getDeclaredField("citizenDAO");
        field.setAccessible(true);
        field.set(servlet, citizenDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostSuccessfulLogin() throws Exception {
        when(request.getParameter("email")).thenReturn("citizen@test.com");
        when(request.getParameter("password")).thenReturn("pass");

        Citizen citizen = new Citizen();
        citizen.setId(1);
        citizen.setEmail("citizen@test.com");
        citizen.setFirstName("John");
        citizen.setLastName("Doe");
        when(citizenDAO.authenticate("citizen@test.com", "pass")).thenReturn(citizen);

        servlet.doPost(request, response);

        verify(session).setAttribute("userEmail", "citizen@test.com");
        verify(session).setAttribute("userRole", "citizen");
        verify(session).setAttribute("userId", 1);
        verify(session).setAttribute("userName", "John Doe");
        verify(session).setMaxInactiveInterval(30 * 60);
        verify(response).sendRedirect("/app/citizen/index.jsp");
    }

    @Test
    void doPostFailedLogin() throws Exception {
        when(request.getParameter("email")).thenReturn("bad@test.com");
        when(request.getParameter("password")).thenReturn("wrong");
        when(citizenDAO.authenticate("bad@test.com", "wrong")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp?error=Invalid email or password&role=citizen");
    }

    @Test
    void doPostExceptionWrapped() throws Exception {
        when(request.getParameter("email")).thenReturn("a@b.com");
        when(request.getParameter("password")).thenReturn("p");
        when(citizenDAO.authenticate("a@b.com", "p")).thenThrow(new RuntimeException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doGetRedirectsToLoginPage() throws Exception {
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }
}
