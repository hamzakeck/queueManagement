package servlets.admin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.AgencyDAO;
import dao.CitizenDAO;
import dao.DAOException;
import dao.ServiceDAO;
import dao.TicketDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Agency;
import models.Citizen;
import models.Service;
import models.Ticket;

class ViewAllTicketsServletTest {

    private ViewAllTicketsServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TicketDAO ticketDAO;
    private ServiceDAO serviceDAO;
    private AgencyDAO agencyDAO;
    private CitizenDAO citizenDAO;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ViewAllTicketsServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        ticketDAO = mock(TicketDAO.class);
        serviceDAO = mock(ServiceDAO.class);
        agencyDAO = mock(AgencyDAO.class);
        citizenDAO = mock(CitizenDAO.class);
        dispatcher = mock(RequestDispatcher.class);

        Field ticketField = ViewAllTicketsServlet.class.getDeclaredField("ticketDAO");
        ticketField.setAccessible(true);
        ticketField.set(servlet, ticketDAO);

        Field serviceField = ViewAllTicketsServlet.class.getDeclaredField("serviceDAO");
        serviceField.setAccessible(true);
        serviceField.set(servlet, serviceDAO);

        Field agencyField = ViewAllTicketsServlet.class.getDeclaredField("agencyDAO");
        agencyField.setAccessible(true);
        agencyField.set(servlet, agencyDAO);

        Field citizenField = ViewAllTicketsServlet.class.getDeclaredField("citizenDAO");
        citizenField.setAccessible(true);
        citizenField.set(servlet, citizenDAO);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void doGetRedirectsUnauthenticatedUser() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doGetRedirectsNonAdminUser() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("citizen");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doGetLoadsTicketsAndMaps() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getRequestDispatcher("/admin/view-all-tickets.jsp")).thenReturn(dispatcher);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setTicketNumber("A001");
        ticket.setServiceId(10);
        ticket.setAgencyId(20);
        ticket.setCitizenId(30);
        when(ticketDAO.findAll()).thenReturn(Arrays.asList(ticket));

        Service service = new Service();
        service.setId(10);
        service.setName("Service A");
        when(serviceDAO.findAll()).thenReturn(Arrays.asList(service));

        Agency agency = new Agency();
        agency.setId(20);
        agency.setName("Agency B");
        when(agencyDAO.findAll()).thenReturn(Arrays.asList(agency));

        Citizen citizen = new Citizen();
        citizen.setId(30);
        citizen.setFirstName("John");
        citizen.setLastName("Doe");
        when(citizenDAO.findAll()).thenReturn(Arrays.asList(citizen));

        servlet.doGet(request, response);

        verify(request).setAttribute("tickets", Arrays.asList(ticket));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetThrowsServletExceptionOnDAOError() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(ticketDAO.findAll()).thenThrow(new DAOException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doGet(request, response));
    }

    @Test
    void doGetHandlesEmptyData() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getRequestDispatcher("/admin/view-all-tickets.jsp")).thenReturn(dispatcher);
        when(ticketDAO.findAll()).thenReturn(Collections.emptyList());
        when(serviceDAO.findAll()).thenReturn(Collections.emptyList());
        when(agencyDAO.findAll()).thenReturn(Collections.emptyList());
        when(citizenDAO.findAll()).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(request).setAttribute("tickets", Collections.emptyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetBuildsMapsCorrectly() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getRequestDispatcher("/admin/view-all-tickets.jsp")).thenReturn(dispatcher);
        when(ticketDAO.findAll()).thenReturn(Collections.emptyList());

        Service s1 = new Service();
        s1.setId(1);
        s1.setName("Svc1");
        Service s2 = new Service();
        s2.setId(2);
        s2.setName("Svc2");
        when(serviceDAO.findAll()).thenReturn(Arrays.asList(s1, s2));

        Agency a1 = new Agency();
        a1.setId(1);
        a1.setName("Ag1");
        when(agencyDAO.findAll()).thenReturn(Arrays.asList(a1));

        Citizen c1 = new Citizen();
        c1.setId(1);
        c1.setFirstName("First");
        c1.setLastName("Last");
        when(citizenDAO.findAll()).thenReturn(Arrays.asList(c1));

        servlet.doGet(request, response);

        // Verify maps are set (we check they're set as attributes)
        verify(request).setAttribute(org.mockito.ArgumentMatchers.eq("serviceNames"), 
            org.mockito.ArgumentMatchers.any());
        verify(request).setAttribute(org.mockito.ArgumentMatchers.eq("agencyNames"), 
            org.mockito.ArgumentMatchers.any());
        verify(request).setAttribute(org.mockito.ArgumentMatchers.eq("citizenNames"), 
            org.mockito.ArgumentMatchers.any());
    }
}
