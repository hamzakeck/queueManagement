package servlets.employee;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.DAOException;
import dao.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Ticket;

class CallNextTicketServletTest {

    private CallNextTicketServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TicketDAO ticketDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CallNextTicketServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        ticketDAO = mock(TicketDAO.class);

        Field field = CallNextTicketServlet.class.getDeclaredField("ticketDAO");
        field.setAccessible(true);
        field.set(servlet, ticketDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostRedirectsUnauthenticatedUser() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostNoTicketsInQueue() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(ticketDAO.callNextTicket(1)).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "No tickets in queue");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostCallNextTicketSuccess() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("B002");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);

        when(ticketDAO.callNextTicket(1)).thenReturn(ticket);
        when(ticketDAO.getWaitingQueue(5, 3)).thenReturn(Collections.emptyList());

        servlet.doPost(request, response);

        verify(session).setAttribute("successMessage", "Called ticket: B002");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostThrowsServletExceptionOnDAOError() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(ticketDAO.callNextTicket(1)).thenThrow(new DAOException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }
}
