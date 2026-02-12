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
import dao.EmployeeDAO;
import dao.ServiceDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Agency;
import models.Employee;
import models.Service;

class ManageEmployeesServletTest {

    private ManageEmployeesServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private EmployeeDAO employeeDAO;
    private AgencyDAO agencyDAO;
    private ServiceDAO serviceDAO;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManageEmployeesServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        employeeDAO = mock(EmployeeDAO.class);
        agencyDAO = mock(AgencyDAO.class);
        serviceDAO = mock(ServiceDAO.class);
        dispatcher = mock(RequestDispatcher.class);

        Field employeeField = ManageEmployeesServlet.class.getDeclaredField("employeeDAO");
        employeeField.setAccessible(true);
        employeeField.set(servlet, employeeDAO);

        Field agencyField = ManageEmployeesServlet.class.getDeclaredField("agencyDAO");
        agencyField.setAccessible(true);
        agencyField.set(servlet, agencyDAO);

        Field serviceField = ManageEmployeesServlet.class.getDeclaredField("serviceDAO");
        serviceField.setAccessible(true);
        serviceField.set(servlet, serviceDAO);

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
    void doGetLoadsEmployeesAndDependencies() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getRequestDispatcher("/admin/manage-employees.jsp")).thenReturn(dispatcher);

        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        when(employeeDAO.findAll()).thenReturn(Arrays.asList(employee));

        Agency agency = new Agency();
        agency.setId(1);
        when(agencyDAO.findAll()).thenReturn(Arrays.asList(agency));

        Service service = new Service();
        service.setId(1);
        when(serviceDAO.findAll()).thenReturn(Arrays.asList(service));

        servlet.doGet(request, response);

        verify(request).setAttribute("employees", Arrays.asList(employee));
        verify(request).setAttribute("agencies", Arrays.asList(agency));
        verify(request).setAttribute("services", Arrays.asList(service));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetThrowsServletExceptionOnDAOError() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(employeeDAO.findAll()).thenThrow(new DAOException("DB error"));

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
    void doPostEditEmployeeSuccess() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("firstName")).thenReturn("Jane");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("email")).thenReturn("jane@test.com");
        when(request.getParameter("agencyId")).thenReturn("2");
        when(request.getParameter("serviceId")).thenReturn("3");
        when(request.getParameter("counterId")).thenReturn("4");
        when(request.getParameter("password")).thenReturn("");

        Employee employee = new Employee();
        employee.setId(1);
        when(employeeDAO.findById(1)).thenReturn(employee);

        servlet.doPost(request, response);

        verify(employeeDAO).update(employee);
        verify(response).sendRedirect("/app/admin/ManageEmployeesServlet?success=updated");
    }

    @Test
    void doPostEditEmployeeWithNewPassword() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("firstName")).thenReturn("Jane");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("email")).thenReturn("jane@test.com");
        when(request.getParameter("agencyId")).thenReturn("2");
        when(request.getParameter("serviceId")).thenReturn("3");
        when(request.getParameter("counterId")).thenReturn("4");
        when(request.getParameter("password")).thenReturn("newpassword");

        Employee employee = new Employee();
        employee.setId(1);
        when(employeeDAO.findById(1)).thenReturn(employee);

        servlet.doPost(request, response);

        verify(employeeDAO).update(employee);
        org.junit.jupiter.api.Assertions.assertEquals("newpassword", employee.getPassword());
    }

    @Test
    void doPostEditEmployeeNotFound() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("firstName")).thenReturn("Test");
        when(request.getParameter("lastName")).thenReturn("Test");
        when(request.getParameter("email")).thenReturn("test@test.com");
        when(request.getParameter("agencyId")).thenReturn("1");
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("counterId")).thenReturn("1");
        when(employeeDAO.findById(999)).thenReturn(null);

        servlet.doPost(request, response);

        verify(employeeDAO, never()).update(any());
        verify(response).sendRedirect("/app/admin/ManageEmployeesServlet?error=notfound");
    }

    @Test
    void doPostDeleteEmployee() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("5");

        servlet.doPost(request, response);

        verify(employeeDAO).delete(5);
        verify(response).sendRedirect("/app/admin/ManageEmployeesServlet?success=deleted");
    }

    @Test
    void doPostHandlesException() throws Exception {
        when(session.getAttribute("userRole")).thenReturn("admin");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");
        when(employeeDAO.delete(1)).thenThrow(new DAOException("Cannot delete"));

        servlet.doPost(request, response);

        verify(response).sendRedirect("/app/admin/ManageEmployeesServlet?error=Cannot delete");
    }
}
