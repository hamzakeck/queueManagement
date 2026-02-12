package servlets.citizen;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.DAOException;
import dao.TicketDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Ticket;

class CreateTicketServletTest {

    private CreateTicketServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TicketDAO ticketDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CreateTicketServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        ticketDAO = mock(TicketDAO.class);

        Field field = CreateTicketServlet.class.getDeclaredField("ticketDAO");
        field.setAccessible(true);
        field.set(servlet, ticketDAO);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doPostRedirectsUnauthenticatedUser() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("userRole")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostRedirectsNonCitizen() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("admin");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostRejectsActiveTicket() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("citizen");

        Ticket activeTicket = new Ticket();
        activeTicket.setStatus("WAITING");
        when(ticketDAO.findByCitizenId(1)).thenReturn(Arrays.asList(activeTicket));

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage",
                "You already have an active ticket. Please complete or cancel your current ticket before creating a new one.");
        verify(response).sendRedirect("/app/citizen/create-ticket.jsp");
    }

    @Test
    void doPostRejectsMissingParameters() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("citizen");
        when(ticketDAO.findByCitizenId(1)).thenReturn(Collections.emptyList());
        when(request.getParameter("agencyId")).thenReturn(null);
        when(request.getParameter("serviceId")).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Please select both agency and service");
        verify(response).sendRedirect("/app/citizen/create-ticket.jsp");
    }

    @Test
    void doPostSuccessfulTicketCreation() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("citizen");
        when(ticketDAO.findByCitizenId(1)).thenReturn(Collections.emptyList());
        when(request.getParameter("agencyId")).thenReturn("5");
        when(request.getParameter("serviceId")).thenReturn("3");
        when(ticketDAO.generateTicketNumber(5, 3)).thenReturn("T001");
        when(ticketDAO.getNextPosition(5, 3)).thenReturn(1);
        when(ticketDAO.create(any(Ticket.class))).thenReturn(42);

        servlet.doPost(request, response);

        verify(session).setAttribute("newTicketId", 42);
        verify(session).setAttribute("newTicketNumber", "T001");
        verify(session).setAttribute("newTicketPosition", 1);
        verify(response).sendRedirect("/app/citizen/ticket-confirmation.jsp");
    }

    @Test
    void doPostHandlesDAOException() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("citizen");
        when(ticketDAO.findByCitizenId(1)).thenThrow(new DAOException("DB error"));

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Failed to create ticket. Please try again.");
        verify(response).sendRedirect("/app/citizen/create-ticket.jsp");
    }

    @Test
    void doGetRedirectsToCreatePage() throws Exception {
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/citizen/create-ticket.jsp");
    }
}
