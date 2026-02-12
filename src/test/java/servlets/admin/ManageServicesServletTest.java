package servlets.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.ServiceDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Service;

class ManageServicesServletTest {

    private ManageServicesServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServiceDAO serviceDAO;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManageServicesServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        serviceDAO = mock(ServiceDAO.class);
        dispatcher = mock(RequestDispatcher.class);

        Field field = ManageServicesServlet.class.getDeclaredField("serviceDAO");
        field.setAccessible(true);
        field.set(servlet, serviceDAO);

        when(request.getContextPath()).thenReturn("/app");
        when(request.getSession(false)).thenReturn(session);
    }

    // ---- doGet tests ----

    @Test
    void doGetLoadsServicesForAdmin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        java.util.List<Service> services = Arrays.asList(new Service(), new Service());
        when(serviceDAO.findAll()).thenReturn(services);
        when(request.getRequestDispatcher("/admin/manage-services.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("services", services);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetRedirectsNonAdmin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("citizen");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doGetRedirectsNullSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    // ---- doPost tests ----

    @Test
    void doPostRedirectsNonAdmin() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("employee");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/login.jsp");
    }

    @Test
    void doPostAddService() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Passport");
        when(request.getParameter("description")).thenReturn("Passport renewal");
        when(request.getParameter("estimatedTime")).thenReturn("15");

        servlet.doPost(request, response);

        verify(serviceDAO).save(any(Service.class));
        verify(response).sendRedirect("/app/admin/ManageServicesServlet?success=added");
    }

    @Test
    void doPostEditServiceExists() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Updated");
        when(request.getParameter("description")).thenReturn("Desc");
        when(request.getParameter("estimatedTime")).thenReturn("20");
        when(request.getParameter("active")).thenReturn("on");

        Service existing = new Service();
        existing.setId(1);
        when(serviceDAO.findById(1)).thenReturn(existing);

        servlet.doPost(request, response);

        verify(serviceDAO).update(existing);
        verify(response).sendRedirect("/app/admin/ManageServicesServlet?success=updated");
    }

    @Test
    void doPostEditServiceNotFound() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("name")).thenReturn("X");
        when(request.getParameter("description")).thenReturn("Y");
        when(request.getParameter("estimatedTime")).thenReturn("10");

        when(serviceDAO.findById(999)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageServicesServlet?error=notfound");
    }

    @Test
    void doPostDeleteService() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("5");

        servlet.doPost(request, response);

        verify(serviceDAO).delete(5);
        verify(response).sendRedirect("/app/admin/ManageServicesServlet?success=deleted");
    }
}
