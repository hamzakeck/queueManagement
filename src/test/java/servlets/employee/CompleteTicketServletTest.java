package servlets.employee;

import java.lang.reflect.Field;

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

class CompleteTicketServletTest {

    private CompleteTicketServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TicketDAO ticketDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CompleteTicketServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        ticketDAO = mock(TicketDAO.class);

        Field field = CompleteTicketServlet.class.getDeclaredField("ticketDAO");
        field.setAccessible(true);
        field.set(servlet, ticketDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostRedirectsWhenNoTicketId() throws Exception {
        when(request.getParameter("ticketId")).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "No ticket ID");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostRedirectsWhenEmptyTicketId() throws Exception {
        when(request.getParameter("ticketId")).thenReturn("   ");

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "No ticket ID");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostTicketNotFound() throws Exception {
        when(request.getParameter("ticketId")).thenReturn("123");
        when(ticketDAO.findById(123)).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Ticket not found");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostCompleteTicketSuccess() throws Exception {
        when(request.getParameter("ticketId")).thenReturn("42");

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("A001");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);

        when(ticketDAO.findById(42)).thenReturn(ticket);
        when(ticketDAO.completeTicket(42)).thenReturn(true);
        when(ticketDAO.getWaitingQueue(5, 3)).thenReturn(java.util.Collections.emptyList());

        servlet.doPost(request, response);

        verify(session).setAttribute("successMessage", "Ticket completed: A001");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostCompleteTicketFailure() throws Exception {
        when(request.getParameter("ticketId")).thenReturn("42");

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("A001");

        when(ticketDAO.findById(42)).thenReturn(ticket);
        when(ticketDAO.completeTicket(42)).thenReturn(false);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Could not complete ticket");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostThrowsServletExceptionOnDAOError() throws Exception {
        when(request.getParameter("ticketId")).thenReturn("42");
        when(ticketDAO.findById(42)).thenThrow(new DAOException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doPostInvalidTicketIdFormat() throws Exception {
        when(request.getParameter("ticketId")).thenReturn("notanumber");

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }
}
