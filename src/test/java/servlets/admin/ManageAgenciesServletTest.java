package servlets.admin;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.AgencyDAO;
import dao.DAOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Agency;

class ManageAgenciesServletTest {

    private ManageAgenciesServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AgencyDAO agencyDAO;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManageAgenciesServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        agencyDAO = mock(AgencyDAO.class);
        dispatcher = mock(RequestDispatcher.class);

        Field field = ManageAgenciesServlet.class.getDeclaredField("agencyDAO");
        field.setAccessible(true);
        field.set(servlet, agencyDAO);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
    }

    // doGet tests

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
    void doGetLoadsAgencies() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getRequestDispatcher("/admin/manage-agencies.jsp")).thenReturn(dispatcher);

        Agency agency = new Agency();
        agency.setId(1);
        agency.setName("Agency 1");
        when(agencyDAO.findAll()).thenReturn(Arrays.asList(agency));

        servlet.doGet(request, response);

        verify(request).setAttribute("agencies", Arrays.asList(agency));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetThrowsServletExceptionOnDAOError() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(agencyDAO.findAll()).thenThrow(new DAOException("DB error"));

        assertThrows(ServletException.class, () -> servlet.doGet(request, response));
    }

    // doPost tests

    @Test
    void doPostRedirectsUnauthenticatedUser() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostRedirectsNonAdminUser() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("employee");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostAddAgency() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("New Agency");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("city")).thenReturn("TestCity");

        servlet.doPost(request, response);

        verify(agencyDAO).save(any(Agency.class));
        verify(response).sendRedirect("/app/admin/ManageAgenciesServlet?success=added");
    }

    @Test
    void doPostEditAgencySuccess() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Updated Agency");
        when(request.getParameter("address")).thenReturn("456 New St");
        when(request.getParameter("city")).thenReturn("NewCity");
        when(request.getParameter("active")).thenReturn("on");

        Agency agency = new Agency();
        agency.setId(1);
        when(agencyDAO.findById(1)).thenReturn(agency);

        servlet.doPost(request, response);

        verify(agencyDAO).update(agency);
        verify(response).sendRedirect("/app/admin/ManageAgenciesServlet?success=updated");
    }

    @Test
    void doPostEditAgencyNotFound() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("name")).thenReturn("Test");
        when(request.getParameter("address")).thenReturn("Test");
        when(request.getParameter("city")).thenReturn("Test");
        when(agencyDAO.findById(999)).thenReturn(null);

        servlet.doPost(request, response);

        verify(agencyDAO, never()).update(any());
        verify(response).sendRedirect("/app/admin/ManageAgenciesServlet?error=notfound");
    }

    @Test
    void doPostDeleteAgency() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("5");

        servlet.doPost(request, response);

        verify(agencyDAO).delete(5);
        verify(response).sendRedirect("/app/admin/ManageAgenciesServlet?success=deleted");
    }

    @Test
    void doPostHandlesException() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Test");
        when(request.getParameter("address")).thenReturn("Test");
        when(request.getParameter("city")).thenReturn("Test");
        when(agencyDAO.save(any())).thenThrow(new DAOException("DB error"));

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageAgenciesServlet?error=DB error");
    }
}
