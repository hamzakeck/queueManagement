package models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

/**
 * Business scenario tests - testing realistic usage patterns
 */
class BusinessScenarioTest {

    // ==================== TICKET LIFECYCLE SCENARIOS ====================

    @Test
    void ticketLifecycleFromCreationToCompletion() {
        // Create ticket
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        LocalDateTime createdAt = LocalDateTime.now();
        ticket.setCreatedAt(createdAt);
        ticket.setPosition(1);
        
        assertEquals("WAITING", ticket.getStatus());
        assertEquals(1, ticket.getPosition());
        
        // Ticket is called
        ticket.setStatus("CALLED");
        LocalDateTime calledAt = createdAt.plusMinutes(5);
        ticket.setCalledAt(calledAt);
        ticket.setCounterId(1);
        
        assertEquals("CALLED", ticket.getStatus());
        assertEquals(1, ticket.getCounterId());
        
        // Ticket is being processed
        ticket.setStatus("IN_PROGRESS");
        
        assertEquals("IN_PROGRESS", ticket.getStatus());
        
        // Ticket is completed
        ticket.setStatus("COMPLETED");
        LocalDateTime completedAt = createdAt.plusMinutes(15);
        ticket.setCompletedAt(completedAt);
        
        assertEquals("COMPLETED", ticket.getStatus());
        assertNotNull(ticket.getCompletedAt());
    }

    @Test
    void ticketCancellationScenario() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setPosition(1);
        
        // Ticket is cancelled before being called
        ticket.setStatus("CANCELLED");
        
        assertEquals("CANCELLED", ticket.getStatus());
        assertNull(ticket.getCalledAt());
        assertNull(ticket.getCompletedAt());
    }

    @Test
    void multipleTicketsInQueue() {
        Ticket t1 = new Ticket("A001", 1, 2, 3);
        Ticket t2 = new Ticket("A002", 2, 2, 3);
        Ticket t3 = new Ticket("A003", 3, 2, 3);
        
        t1.setPosition(1);
        t2.setPosition(2);
        t3.setPosition(3);
        
        assertEquals(1, t1.getPosition());
        assertEquals(2, t2.getPosition());
        assertEquals(3, t3.getPosition());
    }

    // ==================== EMPLOYEE WORKDAY SCENARIO ====================

    @Test
    void employeeAssignedToCounter() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setAgencyId(1);
        employee.setServiceId(1);
        employee.setCounterId(3);
        
        assertEquals(1, employee.getAgencyId());
        assertEquals(1, employee.getServiceId());
        assertEquals(3, employee.getCounterId());
    }

    @Test
    void employeeTransferBetweenCounters() {
        Employee employee = new Employee();
        employee.setCounterId(1);
        assertEquals(1, employee.getCounterId());
        
        // Transfer to different counter
        employee.setCounterId(5);
        assertEquals(5, employee.getCounterId());
    }

    @Test
    void employeeChangesService() {
        Employee employee = new Employee();
        employee.setServiceId(1);
        assertEquals(1, employee.getServiceId());
        
        // Reassign to different service
        employee.setServiceId(2);
        assertEquals(2, employee.getServiceId());
    }

    // ==================== AGENCY MANAGEMENT SCENARIOS ====================

    @Test
    void agencySetupWithCounters() {
        Agency agency = new Agency(1, "Main Branch", "123 Main St", "City", "555-1234", 10);
        agency.setActive(true);
        
        assertTrue(agency.isActive());
        assertEquals(10, agency.getTotalCounters());
    }

    @Test
    void agencyDeactivation() {
        Agency agency = new Agency();
        agency.setActive(true);
        assertTrue(agency.isActive());
        
        // Deactivate agency (e.g., for maintenance)
        agency.setActive(false);
        assertFalse(agency.isActive());
    }

    @Test
    void agencyCounterExpansion() {
        Agency agency = new Agency();
        agency.setTotalCounters(5);
        assertEquals(5, agency.getTotalCounters());
        
        // Agency expands with more counters
        agency.setTotalCounters(10);
        assertEquals(10, agency.getTotalCounters());
    }

    // ==================== SERVICE MANAGEMENT SCENARIOS ====================

    @Test
    void serviceCreationWithEstimatedTime() {
        Service service = new Service(1, "Passport", "Passport application", 45);
        
        assertEquals("Passport", service.getName());
        assertEquals(45, service.getEstimatedTime());
        assertTrue(service.isActive());
    }

    @Test
    void serviceDeactivation() {
        Service service = new Service(1, "Old Service", "Deprecated", 30);
        assertTrue(service.isActive());
        
        // Deactivate service
        service.setActive(false);
        assertFalse(service.isActive());
    }

    @Test
    void serviceEstimatedTimeUpdate() {
        Service service = new Service();
        service.setEstimatedTime(30);
        assertEquals(30, service.getEstimatedTime());
        
        // Update based on new data
        service.setEstimatedTime(25);
        assertEquals(25, service.getEstimatedTime());
    }

    // ==================== CITIZEN REGISTRATION SCENARIOS ====================

    @Test
    void citizenRegistration() {
        Citizen citizen = new Citizen();
        citizen.setFirstName("Jane");
        citizen.setLastName("Doe");
        citizen.setEmail("jane.doe@example.com");
        citizen.setPhone("555-9876");
        citizen.setCin("ABC123456");
        citizen.setPassword("securepassword");
        citizen.setCreatedAt(LocalDateTime.now());
        
        assertNotNull(citizen.getFirstName());
        assertNotNull(citizen.getEmail());
        assertNotNull(citizen.getPassword());
    }

    @Test
    void citizenProfileUpdate() {
        Citizen citizen = new Citizen(1, "Jane", "Doe", "old@example.com", "555-0000", "CIN123");
        
        // Update contact info
        citizen.setEmail("new@example.com");
        citizen.setPhone("555-1111");
        
        assertEquals("new@example.com", citizen.getEmail());
        assertEquals("555-1111", citizen.getPhone());
    }

    // ==================== ADMINISTRATOR SCENARIOS ====================

    @Test
    void administratorCreation() {
        Administrator admin = new Administrator(1, "Admin", "User", "admin@system.com");
        admin.setPassword("admin123");
        admin.setCreatedAt(LocalDateTime.now());
        
        assertNotNull(admin.getEmail());
        assertNotNull(admin.getPassword());
    }

    @Test
    void administratorPasswordChange() {
        Administrator admin = new Administrator();
        admin.setPassword("oldpassword");
        assertEquals("oldpassword", admin.getPassword());
        
        admin.setPassword("newpassword");
        assertEquals("newpassword", admin.getPassword());
    }

    // ==================== TIME-BASED SCENARIOS ====================

    @Test
    void ticketWaitTimeCalculation() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(15);
        LocalDateTime calledAt = LocalDateTime.now();
        
        ticket.setCreatedAt(createdAt);
        ticket.setCalledAt(calledAt);
        
        long waitTimeMinutes = ChronoUnit.MINUTES.between(ticket.getCreatedAt(), ticket.getCalledAt());
        assertEquals(15, waitTimeMinutes);
    }

    @Test
    void ticketServiceTimeCalculation() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        LocalDateTime calledAt = LocalDateTime.now().minusMinutes(10);
        LocalDateTime completedAt = LocalDateTime.now();
        
        ticket.setCalledAt(calledAt);
        ticket.setCompletedAt(completedAt);
        
        long serviceTimeMinutes = ChronoUnit.MINUTES.between(ticket.getCalledAt(), ticket.getCompletedAt());
        assertEquals(10, serviceTimeMinutes);
    }

    // ==================== MULTIPLE SERVICE TYPES ====================

    @Test
    void differentServicesHaveDifferentEstimatedTimes() {
        Service idCard = new Service(1, "ID Card", "National ID", 15);
        Service passport = new Service(2, "Passport", "Passport services", 45);
        Service drivingLicense = new Service(3, "Driving License", "License services", 20);
        
        assertEquals(15, idCard.getEstimatedTime());
        assertEquals(45, passport.getEstimatedTime());
        assertEquals(20, drivingLicense.getEstimatedTime());
    }

    // ==================== QUEUE POSITION SCENARIOS ====================

    @Test
    void ticketPositionDecrementsWhenOthersComplete() {
        Ticket t1 = new Ticket("A001", 1, 2, 3);
        Ticket t2 = new Ticket("A002", 2, 2, 3);
        Ticket t3 = new Ticket("A003", 3, 2, 3);
        
        t1.setPosition(1);
        t2.setPosition(2);
        t3.setPosition(3);
        
        // t1 completes
        t1.setStatus("COMPLETED");
        t1.setPosition(0);
        
        // t2 and t3 positions update
        t2.setPosition(1);
        t3.setPosition(2);
        
        assertEquals(1, t2.getPosition());
        assertEquals(2, t3.getPosition());
    }
}

    void serviceDeactivation() {
        Service service = new Service(1, "Old Service", "Deprecated", 30);
        assertTrue(service.isActive());
        
        // Deactivate service
        service.setActive(false);
        assertFalse(service.isActive());
    }

    @Test
    void serviceEstimatedTimeUpdate() {
        Service service = new Service();
        service.setEstimatedTime(30);
        assertEquals(30, service.getEstimatedTime());
        
        // Update based on new data
        service.setEstimatedTime(25);
        assertEquals(25, service.getEstimatedTime());
    }

    // ==================== CITIZEN REGISTRATION SCENARIOS ====================

    @Test
    void citizenRegistration() {
        Citizen citizen = new Citizen();
        citizen.setFirstName("Jane");
        citizen.setLastName("Doe");
        citizen.setEmail("jane.doe@example.com");
        citizen.setPhone("555-9876");
        citizen.setCin("ABC123456");
        citizen.setPassword("securepassword");
        citizen.setCreatedAt(LocalDateTime.now());
        
        assertNotNull(citizen.getFirstName());
        assertNotNull(citizen.getEmail());
        assertNotNull(citizen.getPassword());
    }

    @Test
    void citizenProfileUpdate() {
        Citizen citizen = new Citizen(1, "Jane", "Doe", "old@example.com", "555-0000", "CIN123");
        
        // Update contact info
        citizen.setEmail("new@example.com");
        citizen.setPhone("555-1111");
        
        assertEquals("new@example.com", citizen.getEmail());
        assertEquals("555-1111", citizen.getPhone());
    }

    // ==================== ADMINISTRATOR SCENARIOS ====================

    @Test
    void administratorCreation() {
        Administrator admin = new Administrator(1, "Admin", "User", "admin@system.com");
        admin.setPassword("admin123");
        admin.setCreatedAt(LocalDateTime.now());
        
        assertNotNull(admin.getEmail());
        assertNotNull(admin.getPassword());
    }

    @Test
    void administratorPasswordChange() {
        Administrator admin = new Administrator();
        admin.setPassword("oldpassword");
        assertEquals("oldpassword", admin.getPassword());
        
        admin.setPassword("newpassword");
        assertEquals("newpassword", admin.getPassword());
    }

    // ==================== TIME-BASED SCENARIOS ====================

    @Test
    void ticketWaitTimeCalculation() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(15);
        LocalDateTime calledAt = LocalDateTime.now();
        
        ticket.setCreatedAt(createdAt);
        ticket.setCalledAt(calledAt);
        
        long waitTimeMinutes = ChronoUnit.MINUTES.between(ticket.getCreatedAt(), ticket.getCalledAt());
        assertEquals(15, waitTimeMinutes);
    }

    @Test
    void ticketServiceTimeCalculation() {
        Ticket ticket = new Ticket("A001", 1, 2, 3);
        LocalDateTime calledAt = LocalDateTime.now().minusMinutes(10);
        LocalDateTime completedAt = LocalDateTime.now();
        
        ticket.setCalledAt(calledAt);
        ticket.setCompletedAt(completedAt);
        
        long serviceTimeMinutes = ChronoUnit.MINUTES.between(ticket.getCalledAt(), ticket.getCompletedAt());
        assertEquals(10, serviceTimeMinutes);
    }

    // ==================== MULTIPLE SERVICE TYPES ====================

    @Test
    void differentServicesHaveDifferentEstimatedTimes() {
        Service idCard = new Service(1, "ID Card", "National ID", 15);
        Service passport = new Service(2, "Passport", "Passport services", 45);
        Service drivingLicense = new Service(3, "Driving License", "License services", 20);
        
        assertEquals(15, idCard.getEstimatedTime());
        assertEquals(45, passport.getEstimatedTime());
        assertEquals(20, drivingLicense.getEstimatedTime());
    }

    // ==================== QUEUE POSITION SCENARIOS ====================

    @Test
    void ticketPositionDecrementsWhenOthersComplete() {
        Ticket t1 = new Ticket("A001", 1, 2, 3);
        Ticket t2 = new Ticket("A002", 2, 2, 3);
        Ticket t3 = new Ticket("A003", 3, 2, 3);
        
        t1.setPosition(1);
        t2.setPosition(2);
        t3.setPosition(3);
        
        // t1 completes
        t1.setStatus("COMPLETED");
        t1.setPosition(0);
        
        // t2 and t3 positions update
        t2.setPosition(1);
        t3.setPosition(2);
        
        assertEquals(1, t2.getPosition());
        assertEquals(2, t3.getPosition());
    }
}
