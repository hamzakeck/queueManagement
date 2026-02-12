package servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class LogoutServletTest {

    private LogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        servlet = new LogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostInvalidatesExistingSession() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostHandlesNullSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doGetDelegatesToDoPost() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        servlet.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/app/login.jsp");
    }
}
