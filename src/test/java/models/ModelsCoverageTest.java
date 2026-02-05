package models;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ModelsCoverageTest {

    // ==================== CITIZEN TESTS ====================

    @Test
    void citizenDefaultConstructorCreatesInstance() {
        Citizen citizen = new Citizen();
        assertNotNull(citizen);
        assertEquals(0, citizen.getId());
        assertNull(citizen.getFirstName());
    }

    @Test
    void citizenParameterizedConstructorSetsFields() {
        Citizen citizen = new Citizen(1, "Ada", "Lovelace", "ada@example.com", "123456", "CIN123");
        
        assertEquals(1, citizen.getId());
        assertEquals("Ada", citizen.getFirstName());
        assertEquals("Lovelace", citizen.getLastName());
        assertEquals("ada@example.com", citizen.getEmail());
        assertEquals("123456", citizen.getPhone());
        assertEquals("CIN123", citizen.getCin());
    }

    @Test
    void citizenGettersSettersWork() {
        Citizen citizen = new Citizen();
        citizen.setId(1);
        citizen.setFirstName("Ada");
        citizen.setLastName("Lovelace");
        citizen.setEmail("ada@example.com");
        citizen.setPhone("123");
        citizen.setCin("CIN123");
        citizen.setPassword("pw");
        LocalDateTime now = LocalDateTime.now();
        citizen.setCreatedAt(now);

        assertEquals(1, citizen.getId());
        assertEquals("Ada", citizen.getFirstName());
        assertEquals("Lovelace", citizen.getLastName());
        assertEquals("ada@example.com", citizen.getEmail());
        assertEquals("123", citizen.getPhone());
        assertEquals("CIN123", citizen.getCin());
        assertEquals("pw", citizen.getPassword());
        assertEquals(now, citizen.getCreatedAt());
    }

    // ==================== EMPLOYEE TESTS ====================

    @Test
    void employeeDefaultConstructorCreatesInstance() {
        Employee employee = new Employee();
        assertNotNull(employee);
        assertEquals(0, employee.getId());
        assertNull(employee.getFirstName());
    }

    @Test
    void employeeParameterizedConstructorSetsFields() {
        Employee employee = new Employee(7, "Grace", "Hopper", "grace@example.com", 1, 5);
        
        assertEquals(7, employee.getId());
        assertEquals("Grace", employee.getFirstName());
        assertEquals("Hopper", employee.getLastName());
        assertEquals("grace@example.com", employee.getEmail());
        assertEquals(1, employee.getAgencyId());
        assertEquals(5, employee.getCounterId());
    }

    @Test
    void employeeFieldsWork() {
        Employee employee = new Employee();
        employee.setId(7);
        employee.setFirstName("Grace");
        employee.setLastName("Hopper");
        employee.setEmail("grace@example.com");
        employee.setPassword("secret");
        employee.setAgencyId(2);
        employee.setServiceId(3);
        employee.setCounterId(4);
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);

        assertEquals(7, employee.getId());
        assertEquals("Grace", employee.getFirstName());
        assertEquals("Hopper", employee.getLastName());
        assertEquals("grace@example.com", employee.getEmail());
        assertEquals("secret", employee.getPassword());
        assertEquals(2, employee.getAgencyId());
        assertEquals(3, employee.getServiceId());
        assertEquals(4, employee.getCounterId());
        assertEquals(now, employee.getCreatedAt());
    }

    // ==================== ADMINISTRATOR TESTS ====================

    @Test
    void administratorDefaultConstructorCreatesInstance() {
        Administrator admin = new Administrator();
        assertNotNull(admin);
        assertEquals(0, admin.getId());
        assertNull(admin.getFirstName());
    }

    @Test
    void administratorParameterizedConstructorSetsFields() {
        Administrator admin = new Administrator(1, "John", "Doe", "john@example.com");
        
        assertEquals(1, admin.getId());
        assertEquals("John", admin.getFirstName());
        assertEquals("Doe", admin.getLastName());
        assertEquals("john@example.com", admin.getEmail());
    }

    @Test
    void administratorGettersSettersWork() {
        Administrator admin = new Administrator();
        admin.setId(1);
        admin.setFirstName("John");
        admin.setLastName("Doe");
        admin.setEmail("john@example.com");
        admin.setPassword("adminpass");
        LocalDateTime now = LocalDateTime.now();
        admin.setCreatedAt(now);

        assertEquals(1, admin.getId());
        assertEquals("John", admin.getFirstName());
        assertEquals("Doe", admin.getLastName());
        assertEquals("john@example.com", admin.getEmail());
        assertEquals("adminpass", admin.getPassword());
        assertEquals(now, admin.getCreatedAt());
    }

    // ==================== AGENCY TESTS ====================

    @Test
    void agencyDefaultConstructorCreatesInstance() {
        Agency agency = new Agency();
        assertNotNull(agency);
        assertEquals(0, agency.getId());
        assertNull(agency.getName());
        assertFalse(agency.isActive());
    }

    @Test
    void agencyParameterizedConstructorSetsFields() {
        Agency agency = new Agency(1, "Main Agency", "123 Street", "Casablanca", "0500000000", 5);
        
        assertEquals(1, agency.getId());
        assertEquals("Main Agency", agency.getName());
        assertEquals("123 Street", agency.getAddress());
        assertEquals("Casablanca", agency.getCity());
        assertEquals("0500000000", agency.getPhone());
        assertEquals(5, agency.getTotalCounters());
    }

    @Test
    void agencyGettersSettersWork() {
        Agency agency = new Agency();
        agency.setId(1);
        agency.setName("Main Agency");
        agency.setAddress("123 Street");
        agency.setCity("Casablanca");
        agency.setPhone("0500000000");
        agency.setTotalCounters(5);
        agency.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        agency.setCreatedAt(now);

        assertEquals(1, agency.getId());
        assertEquals("Main Agency", agency.getName());
        assertEquals("123 Street", agency.getAddress());
        assertEquals("Casablanca", agency.getCity());
        assertEquals("0500000000", agency.getPhone());
        assertEquals(5, agency.getTotalCounters());
        assertTrue(agency.isActive());
        assertEquals(now, agency.getCreatedAt());
    }

    // ==================== SERVICE TESTS ====================

    @Test
    void serviceDefaultConstructorCreatesInstance() {
        Service service = new Service();
        assertNotNull(service);
        assertEquals(0, service.getId());
        assertNull(service.getName());
        assertFalse(service.isActive());
    }

    @Test
    void serviceParameterizedConstructorSetsFields() {
        Service service = new Service(1, "ID Card", "National ID card services", 15);
        
        assertEquals(1, service.getId());
        assertEquals("ID Card", service.getName());
        assertEquals("National ID card services", service.getDescription());
        assertEquals(15, service.getEstimatedTime());
        assertTrue(service.isActive()); // Constructor sets active to true
    }

    @Test
    void serviceGettersSettersWork() {
        Service service = new Service();
        service.setId(1);
        service.setName("ID Card");
        service.setDescription("National ID card services");
        service.setEstimatedTime(15);
        service.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        service.setCreatedAt(now);

        assertEquals(1, service.getId());
        assertEquals("ID Card", service.getName());
        assertEquals("National ID card services", service.getDescription());
        assertEquals(15, service.getEstimatedTime());
        assertTrue(service.isActive());
        assertEquals(now, service.getCreatedAt());
    }

    // ==================== TICKET TESTS ====================

    @Test
    void ticketDefaultConstructorCreatesInstance() {
        Ticket ticket = new Ticket();
        assertNotNull(ticket);
        assertEquals(0, ticket.getId());
        assertNull(ticket.getTicketNumber());
        assertNull(ticket.getStatus());
    }

    @Test
    void ticketParameterizedConstructorSetsFields() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        
        assertEquals("A001", ticket.getTicketNumber());
        assertEquals(1, ticket.getCitizenId());
        assertEquals(2, ticket.getServiceId());
        assertEquals(3, ticket.getAgencyId());
        assertEquals("WAITING", ticket.getStatus()); // Default status
    }

    @Test
    void ticketConstructorAndFieldsWork() {
        Ticket ticket = new Ticket();
        ticket.setId(10);
        ticket.setTicketNumber("A001");
        ticket.setStatus("WAITING");
        ticket.setCitizenId(1);
        ticket.setServiceId(2);
        ticket.setAgencyId(3);
        ticket.setPosition(5);
        ticket.setCounterId(2);
        LocalDateTime now = LocalDateTime.now();
        ticket.setCreatedAt(now);
        ticket.setCalledAt(now.plusMinutes(5));
        ticket.setCompletedAt(now.plusMinutes(15));

        assertEquals(10, ticket.getId());
        assertEquals("A001", ticket.getTicketNumber());
        assertEquals("WAITING", ticket.getStatus());
        assertEquals(1, ticket.getCitizenId());
        assertEquals(2, ticket.getServiceId());
        assertEquals(3, ticket.getAgencyId());
        assertEquals(5, ticket.getPosition());
        assertEquals(2, ticket.getCounterId());
        assertEquals(now, ticket.getCreatedAt());
        assertEquals(now.plusMinutes(5), ticket.getCalledAt());
        assertEquals(now.plusMinutes(15), ticket.getCompletedAt());
    }

    @Test
    void ticketStatusTransitions() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        assertEquals("WAITING", ticket.getStatus());
        
        ticket.setStatus("CALLED");
        assertEquals("CALLED", ticket.getStatus());
        
        ticket.setStatus("IN_PROGRESS");
        assertEquals("IN_PROGRESS", ticket.getStatus());
        
        ticket.setStatus("COMPLETED");
        assertEquals("COMPLETED", ticket.getStatus());
    }

    @Test
    void ticketCancelledStatus() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        ticket.setStatus("CANCELLED");
        assertEquals("CANCELLED", ticket.getStatus());
    }
}
