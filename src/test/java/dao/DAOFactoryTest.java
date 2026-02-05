package dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class DAOFactoryTest {

    @Test
    void getInstanceReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory);
    }

    @Test
    void getInstanceReturnsSameInstance() {
        DAOFactory first = DAOFactory.getInstance();
        DAOFactory second = DAOFactory.getInstance();
        assertSame(first, second, "DAOFactory should return the same singleton instance");
    }

    @Test
    void getCitizenDAOReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory.getCitizenDAO());
    }

    @Test
    void getEmployeeDAOReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory.getEmployeeDAO());
    }

    @Test
    void getAdministratorDAOReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory.getAdministratorDAO());
    }

    @Test
    void getTicketDAOReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory.getTicketDAO());
    }

    @Test
    void getServiceDAOReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory.getServiceDAO());
    }

    @Test
    void getAgencyDAOReturnsNonNull() {
        DAOFactory factory = DAOFactory.getInstance();
        assertNotNull(factory.getAgencyDAO());
    }

    @Test
    void citizenDAOIsSameInstance() {
        DAOFactory factory = DAOFactory.getInstance();
        CitizenDAO first = factory.getCitizenDAO();
        CitizenDAO second = factory.getCitizenDAO();
        assertSame(first, second, "Should return the same DAO instance");
    }

    @Test
    void employeeDAOIsSameInstance() {
        DAOFactory factory = DAOFactory.getInstance();
        EmployeeDAO first = factory.getEmployeeDAO();
        EmployeeDAO second = factory.getEmployeeDAO();
        assertSame(first, second, "Should return the same DAO instance");
    }

    @Test
    void administratorDAOIsSameInstance() {
        DAOFactory factory = DAOFactory.getInstance();
        AdministratorDAO first = factory.getAdministratorDAO();
        AdministratorDAO second = factory.getAdministratorDAO();
        assertSame(first, second, "Should return the same DAO instance");
    }

    @Test
    void ticketDAOIsSameInstance() {
        DAOFactory factory = DAOFactory.getInstance();
        TicketDAO first = factory.getTicketDAO();
        TicketDAO second = factory.getTicketDAO();
        assertSame(first, second, "Should return the same DAO instance");
    }

    @Test
    void serviceDAOIsSameInstance() {
        DAOFactory factory = DAOFactory.getInstance();
        ServiceDAO first = factory.getServiceDAO();
        ServiceDAO second = factory.getServiceDAO();
        assertSame(first, second, "Should return the same DAO instance");
    }

    @Test
    void agencyDAOIsSameInstance() {
        DAOFactory factory = DAOFactory.getInstance();
        AgencyDAO first = factory.getAgencyDAO();
        AgencyDAO second = factory.getAgencyDAO();
        assertSame(first, second, "Should return the same DAO instance");
    }
}
