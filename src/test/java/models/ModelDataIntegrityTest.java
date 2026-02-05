package models;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Data integrity and boundary tests for all models
 */
class ModelDataIntegrityTest {

    // ==================== CITIZEN DATA INTEGRITY ====================

    @Test
    void citizenAllFieldsCanBeSetAndRetrieved() {
        Citizen citizen = new Citizen();
        LocalDateTime time = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 30, 0);
        
        citizen.setId(123);
        citizen.setFirstName("Test");
        citizen.setLastName("User");
        citizen.setEmail("test@example.com");
        citizen.setPhone("555-1234");
        citizen.setCin("ABC123");
        citizen.setPassword("secret123");
        citizen.setCreatedAt(time);
        
        assertEquals(123, citizen.getId());
        assertEquals("Test", citizen.getFirstName());
        assertEquals("User", citizen.getLastName());
        assertEquals("test@example.com", citizen.getEmail());
        assertEquals("555-1234", citizen.getPhone());
        assertEquals("ABC123", citizen.getCin());
        assertEquals("secret123", citizen.getPassword());
        assertEquals(time, citizen.getCreatedAt());
    }

    @Test
    void citizenFieldsAreIndependent() {
        Citizen c1 = new Citizen();
        Citizen c2 = new Citizen();
        
        c1.setId(1);
        c2.setId(2);
        
        assertEquals(1, c1.getId());
        assertEquals(2, c2.getId());
    }

    // ==================== EMPLOYEE DATA INTEGRITY ====================

    @Test
    void employeeAllFieldsCanBeSetAndRetrieved() {
        Employee employee = new Employee();
        LocalDateTime time = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 30, 0);
        
        employee.setId(456);
        employee.setFirstName("Test");
        employee.setLastName("Employee");
        employee.setEmail("employee@example.com");
        employee.setPassword("emppass");
        employee.setAgencyId(10);
        employee.setServiceId(20);
        employee.setCounterId(5);
        employee.setCreatedAt(time);
        
        assertEquals(456, employee.getId());
        assertEquals("Test", employee.getFirstName());
        assertEquals("Employee", employee.getLastName());
        assertEquals("employee@example.com", employee.getEmail());
        assertEquals("emppass", employee.getPassword());
        assertEquals(10, employee.getAgencyId());
        assertEquals(20, employee.getServiceId());
        assertEquals(5, employee.getCounterId());
        assertEquals(time, employee.getCreatedAt());
    }

    @Test
    void employeeFieldsAreIndependent() {
        Employee e1 = new Employee();
        Employee e2 = new Employee();
        
        e1.setAgencyId(1);
        e2.setAgencyId(2);
        
        assertEquals(1, e1.getAgencyId());
        assertEquals(2, e2.getAgencyId());
    }

    // ==================== ADMINISTRATOR DATA INTEGRITY ====================

    @Test
    void administratorAllFieldsCanBeSetAndRetrieved() {
        Administrator admin = new Administrator();
        LocalDateTime time = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 30, 0);
        
        admin.setId(789);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@example.com");
        admin.setPassword("adminpass");
        admin.setCreatedAt(time);
        
        assertEquals(789, admin.getId());
        assertEquals("Admin", admin.getFirstName());
        assertEquals("User", admin.getLastName());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("adminpass", admin.getPassword());
        assertEquals(time, admin.getCreatedAt());
    }

    // ==================== AGENCY DATA INTEGRITY ====================

    @Test
    void agencyAllFieldsCanBeSetAndRetrieved() {
        Agency agency = new Agency();
        LocalDateTime time = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 30, 0);
        
        agency.setId(100);
        agency.setName("Main Agency");
        agency.setAddress("123 Main St");
        agency.setCity("TestCity");
        agency.setPhone("555-9999");
        agency.setTotalCounters(10);
        agency.setActive(true);
        agency.setCreatedAt(time);
        
        assertEquals(100, agency.getId());
        assertEquals("Main Agency", agency.getName());
        assertEquals("123 Main St", agency.getAddress());
        assertEquals("TestCity", agency.getCity());
        assertEquals("555-9999", agency.getPhone());
        assertEquals(10, agency.getTotalCounters());
        assertTrue(agency.isActive());
        assertEquals(time, agency.getCreatedAt());
    }

    // ==================== SERVICE DATA INTEGRITY ====================

    @Test
    void serviceAllFieldsCanBeSetAndRetrieved() {
        Service service = new Service();
        LocalDateTime time = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 30, 0);
        
        service.setId(200);
        service.setName("ID Service");
        service.setDescription("National ID services");
        service.setEstimatedTime(30);
        service.setActive(true);
        service.setCreatedAt(time);
        
        assertEquals(200, service.getId());
        assertEquals("ID Service", service.getName());
        assertEquals("National ID services", service.getDescription());
        assertEquals(30, service.getEstimatedTime());
        assertTrue(service.isActive());
        assertEquals(time, service.getCreatedAt());
    }

    // ==================== TICKET DATA INTEGRITY ====================

    @Test
    void ticketAllFieldsCanBeSetAndRetrieved() {
        Ticket ticket = new Ticket();
        LocalDateTime createdTime = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 0, 0);
        LocalDateTime calledTime = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 15, 0);
        LocalDateTime completedTime = LocalDateTime.of(2026, Month.FEBRUARY, 5, 10, 30, 0);
        
        ticket.setId(300);
        ticket.setTicketNumber("A001");
        ticket.setCitizenId(1);
        ticket.setServiceId(2);
        ticket.setAgencyId(3);
        ticket.setStatus("COMPLETED");
        ticket.setPosition(5);
        ticket.setCounterId(2);
        ticket.setCreatedAt(createdTime);
        ticket.setCalledAt(calledTime);
        ticket.setCompletedAt(completedTime);
        
        assertEquals(300, ticket.getId());
        assertEquals("A001", ticket.getTicketNumber());
        assertEquals(1, ticket.getCitizenId());
        assertEquals(2, ticket.getServiceId());
        assertEquals(3, ticket.getAgencyId());
        assertEquals("COMPLETED", ticket.getStatus());
        assertEquals(5, ticket.getPosition());
        assertEquals(2, ticket.getCounterId());
        assertEquals(createdTime, ticket.getCreatedAt());
        assertEquals(calledTime, ticket.getCalledAt());
        assertEquals(completedTime, ticket.getCompletedAt());
    }

    @Test
    void ticketFieldsAreIndependent() {
        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket();
        
        t1.setTicketNumber("A001");
        t2.setTicketNumber("B001");
        
        assertEquals("A001", t1.getTicketNumber());
        assertEquals("B001", t2.getTicketNumber());
    }

    // ==================== BOUNDARY VALUE TESTS ====================

    @Test
    void allModelsAcceptZeroId() {
        Citizen citizen = new Citizen();
        Employee employee = new Employee();
        Administrator admin = new Administrator();
        Agency agency = new Agency();
        Service service = new Service();
        Ticket ticket = new Ticket();
        
        citizen.setId(0);
        employee.setId(0);
        admin.setId(0);
        agency.setId(0);
        service.setId(0);
        ticket.setId(0);
        
        assertEquals(0, citizen.getId());
        assertEquals(0, employee.getId());
        assertEquals(0, admin.getId());
        assertEquals(0, agency.getId());
        assertEquals(0, service.getId());
        assertEquals(0, ticket.getId());
    }

    @Test
    void allModelsAcceptNegativeId() {
        Citizen citizen = new Citizen();
        Employee employee = new Employee();
        Administrator admin = new Administrator();
        Agency agency = new Agency();
        Service service = new Service();
        Ticket ticket = new Ticket();
        
        citizen.setId(-1);
        employee.setId(-1);
        admin.setId(-1);
        agency.setId(-1);
        service.setId(-1);
        ticket.setId(-1);
        
        assertEquals(-1, citizen.getId());
        assertEquals(-1, employee.getId());
        assertEquals(-1, admin.getId());
        assertEquals(-1, agency.getId());
        assertEquals(-1, service.getId());
        assertEquals(-1, ticket.getId());
    }

    @Test
    void allModelsAcceptMaxIntId() {
        Citizen citizen = new Citizen();
        Employee employee = new Employee();
        Administrator admin = new Administrator();
        Agency agency = new Agency();
        Service service = new Service();
        Ticket ticket = new Ticket();
        
        citizen.setId(Integer.MAX_VALUE);
        employee.setId(Integer.MAX_VALUE);
        admin.setId(Integer.MAX_VALUE);
        agency.setId(Integer.MAX_VALUE);
        service.setId(Integer.MAX_VALUE);
        ticket.setId(Integer.MAX_VALUE);
        
        assertEquals(Integer.MAX_VALUE, citizen.getId());
        assertEquals(Integer.MAX_VALUE, employee.getId());
        assertEquals(Integer.MAX_VALUE, admin.getId());
        assertEquals(Integer.MAX_VALUE, agency.getId());
        assertEquals(Integer.MAX_VALUE, service.getId());
        assertEquals(Integer.MAX_VALUE, ticket.getId());
    }

    @Test
    void allModelsAcceptMinIntId() {
        Citizen citizen = new Citizen();
        Employee employee = new Employee();
        Administrator admin = new Administrator();
        Agency agency = new Agency();
        Service service = new Service();
        Ticket ticket = new Ticket();
        
        citizen.setId(Integer.MIN_VALUE);
        employee.setId(Integer.MIN_VALUE);
        admin.setId(Integer.MIN_VALUE);
        agency.setId(Integer.MIN_VALUE);
        service.setId(Integer.MIN_VALUE);
        ticket.setId(Integer.MIN_VALUE);
        
        assertEquals(Integer.MIN_VALUE, citizen.getId());
        assertEquals(Integer.MIN_VALUE, employee.getId());
        assertEquals(Integer.MIN_VALUE, admin.getId());
        assertEquals(Integer.MIN_VALUE, agency.getId());
        assertEquals(Integer.MIN_VALUE, service.getId());
        assertEquals(Integer.MIN_VALUE, ticket.getId());
    }

    // ==================== DATETIME BOUNDARY TESTS ====================

    @Test
    void modelsAcceptEpochDateTime() {
        LocalDateTime epoch = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        
        Citizen citizen = new Citizen();
        Employee employee = new Employee();
        Administrator admin = new Administrator();
        Agency agency = new Agency();
        Service service = new Service();
        Ticket ticket = new Ticket();
        
        citizen.setCreatedAt(epoch);
        employee.setCreatedAt(epoch);
        admin.setCreatedAt(epoch);
        agency.setCreatedAt(epoch);
        service.setCreatedAt(epoch);
        ticket.setCreatedAt(epoch);
        
        assertEquals(epoch, citizen.getCreatedAt());
        assertEquals(epoch, employee.getCreatedAt());
        assertEquals(epoch, admin.getCreatedAt());
        assertEquals(epoch, agency.getCreatedAt());
        assertEquals(epoch, service.getCreatedAt());
        assertEquals(epoch, ticket.getCreatedAt());
    }

    @Test
    void modelsAcceptFarFutureDateTime() {
        LocalDateTime future = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        
        Citizen citizen = new Citizen();
        Employee employee = new Employee();
        Administrator admin = new Administrator();
        Agency agency = new Agency();
        Service service = new Service();
        Ticket ticket = new Ticket();
        
        citizen.setCreatedAt(future);
        employee.setCreatedAt(future);
        admin.setCreatedAt(future);
        agency.setCreatedAt(future);
        service.setCreatedAt(future);
        ticket.setCreatedAt(future);
        
        assertEquals(future, citizen.getCreatedAt());
        assertEquals(future, employee.getCreatedAt());
        assertEquals(future, admin.getCreatedAt());
        assertEquals(future, agency.getCreatedAt());
        assertEquals(future, service.getCreatedAt());
        assertEquals(future, ticket.getCreatedAt());
    }
}
