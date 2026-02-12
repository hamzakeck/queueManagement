package servlets.employee;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.DAOException;
import dao.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Ticket;

class HoldTicketServletTest {

    private HoldTicketServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TicketDAO ticketDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new HoldTicketServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        ticketDAO = mock(TicketDAO.class);

        Field field = HoldTicketServlet.class.getDeclaredField("ticketDAO");
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
    void doPostNoCurrentTicket() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(ticketDAO.getCurrentTicketForEmployee(1)).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "No ticket is currently in progress to put on hold.");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostHoldTicketSuccess() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("C003");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);
        ticket.setStatus("IN_PROGRESS");

        when(ticketDAO.getCurrentTicketForEmployee(1)).thenReturn(ticket);
        when(ticketDAO.getNextPosition(5, 3)).thenReturn(10);
        when(ticketDAO.update(ticket)).thenReturn(true);

        servlet.doPost(request, response);

        verify(session).setAttribute("successMessage", "Ticket put on hold: C003");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostHoldTicketUpdateFailure() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("C003");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);
        ticket.setStatus("IN_PROGRESS");

        when(ticketDAO.getCurrentTicketForEmployee(1)).thenReturn(ticket);
        when(ticketDAO.getNextPosition(5, 3)).thenReturn(10);
        when(ticketDAO.update(ticket)).thenReturn(false);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Failed to put the ticket on hold.");
        verify(response).sendRedirect("/app/employee/index.jsp");
    }

    @Test
    void doPostThrowsServletExceptionOnDAOError() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(ticketDAO.getCurrentTicketForEmployee(1)).thenThrow(new DAOException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doPost(request, response));
    }

    @Test
    void doPostVerifiesTicketStatusIsChangedToWaiting() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("C003");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);
        ticket.setStatus("IN_PROGRESS");
        ticket.setCounterId(7);

        when(ticketDAO.getCurrentTicketForEmployee(1)).thenReturn(ticket);
        when(ticketDAO.getNextPosition(5, 3)).thenReturn(15);
        when(ticketDAO.update(ticket)).thenReturn(true);

        servlet.doPost(request, response);

        // Verify ticket properties were updated before save
        org.junit.jupiter.api.Assertions.assertEquals("WAITING", ticket.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(15, ticket.getPosition());
        org.junit.jupiter.api.Assertions.assertEquals(0, ticket.getCounterId());
        org.junit.jupiter.api.Assertions.assertNull(ticket.getCalledAt());
    }
}
