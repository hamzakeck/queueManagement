package servlets.citizen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.DAOException;
import dao.TicketDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Ticket;

class GetWaitTimeServletTest {

    private GetWaitTimeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private TicketDAO ticketDAO;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new GetWaitTimeServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        ticketDAO = mock(TicketDAO.class);

        Field field = GetWaitTimeServlet.class.getDeclaredField("ticketDAO");
        field.setAccessible(true);
        field.set(servlet, ticketDAO);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doGetSetsContentTypeAndCharset() throws Exception {
        when(request.getParameter("ticketNumber")).thenReturn("A001");
        when(ticketDAO.findByTicketNumber("A001")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void doGetTicketNotFound() throws Exception {
        when(request.getParameter("ticketNumber")).thenReturn("INVALID");
        when(ticketDAO.findByTicketNumber("INVALID")).thenReturn(null);

        servlet.doGet(request, response);
        printWriter.flush();

        String output = stringWriter.toString();
        assertTrue(output.contains("\"error\":\"Ticket not found\""));
    }

    @Test
    void doGetReturnsWaitTimeData() throws Exception {
        when(request.getParameter("ticketNumber")).thenReturn("A001");

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("A001");
        ticket.setStatus("WAITING");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);

        when(ticketDAO.findByTicketNumber("A001")).thenReturn(ticket);
        when(ticketDAO.getPositionInQueue(42)).thenReturn(5);
        when(ticketDAO.getAverageServiceTime(3, 5)).thenReturn(10.0);

        servlet.doGet(request, response);
        printWriter.flush();

        String output = stringWriter.toString();
        assertTrue(output.contains("\"ticketNumber\":\"A001\""));
        assertTrue(output.contains("\"status\":\"WAITING\""));
        assertTrue(output.contains("\"position\":5"));
        assertTrue(output.contains("\"avgServiceTime\":10.0"));
        assertTrue(output.contains("\"estimatedWaitMinutes\":50"));
    }

    @Test
    void doGetEstimatedWaitNeverNegative() throws Exception {
        when(request.getParameter("ticketNumber")).thenReturn("A001");

        Ticket ticket = new Ticket();
        ticket.setId(42);
        ticket.setTicketNumber("A001");
        ticket.setStatus("WAITING");
        ticket.setServiceId(3);
        ticket.setAgencyId(5);

        when(ticketDAO.findByTicketNumber("A001")).thenReturn(ticket);
        when(ticketDAO.getPositionInQueue(42)).thenReturn(0);
        when(ticketDAO.getAverageServiceTime(3, 5)).thenReturn(0.0);

        servlet.doGet(request, response);
        printWriter.flush();

        String output = stringWriter.toString();
        assertTrue(output.contains("\"estimatedWaitMinutes\":0"));
    }

    @Test
    void doGetHandlesDAOException() throws Exception {
        when(request.getParameter("ticketNumber")).thenReturn("A001");
        when(ticketDAO.findByTicketNumber("A001")).thenThrow(new DAOException("DB error"));

        servlet.doGet(request, response);
        printWriter.flush();

        String output = stringWriter.toString();
        assertTrue(output.contains("\"error\":\"DB error\""));
    }

    @Test
    void doGetCalculatesWaitTimeCorrectly() throws Exception {
        when(request.getParameter("ticketNumber")).thenReturn("B002");

        Ticket ticket = new Ticket();
        ticket.setId(10);
        ticket.setTicketNumber("B002");
        ticket.setStatus("WAITING");
        ticket.setServiceId(2);
        ticket.setAgencyId(1);

        when(ticketDAO.findByTicketNumber("B002")).thenReturn(ticket);
        when(ticketDAO.getPositionInQueue(10)).thenReturn(3);
        when(ticketDAO.getAverageServiceTime(2, 1)).thenReturn(5.5); // 3 * 5.5 = 16.5 -> ceil = 17

        servlet.doGet(request, response);
        printWriter.flush();

        String output = stringWriter.toString();
        assertTrue(output.contains("\"estimatedWaitMinutes\":17"));
    }
}
