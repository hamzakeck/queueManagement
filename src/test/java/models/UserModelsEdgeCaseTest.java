package models;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for Employee, Administrator, and Citizen models
 */
class UserModelsEdgeCaseTest {

    // ==================== CITIZEN EDGE CASES ====================

    @Test
    void citizenEmailCanBeNull() {
        Citizen citizen = new Citizen();
        assertNull(citizen.getEmail());
    }

    @Test
    void citizenEmailCanContainSubdomain() {
        Citizen citizen = new Citizen();
        citizen.setEmail("user@mail.subdomain.example.com");
        assertEquals("user@mail.subdomain.example.com", citizen.getEmail());
    }

    @Test
    void citizenPhoneCanBeNull() {
        Citizen citizen = new Citizen();
        assertNull(citizen.getPhone());
    }

    @Test
    void citizenPhoneCanHaveInternationalFormat() {
        Citizen citizen = new Citizen();
        citizen.setPhone("+212 6 12 34 56 78");
        assertEquals("+212 6 12 34 56 78", citizen.getPhone());
    }

    @Test
    void citizenCinCanBeAlphanumeric() {
        Citizen citizen = new Citizen();
        citizen.setCin("AB123456");
        assertEquals("AB123456", citizen.getCin());
    }

    @Test
    void citizenCinCanContainSpecialCharacters() {
        Citizen citizen = new Citizen();
        citizen.setCin("AB-123456");
        assertEquals("AB-123456", citizen.getCin());
    }

    @Test
    void citizenPasswordCanBeEmpty() {
        Citizen citizen = new Citizen();
        citizen.setPassword("");
        assertEquals("", citizen.getPassword());
    }

    @Test
    void citizenNameCanContainAccents() {
        Citizen citizen = new Citizen();
        citizen.setFirstName("José");
        citizen.setLastName("García");
        assertEquals("José", citizen.getFirstName());
        assertEquals("García", citizen.getLastName());
    }

    @Test
    void citizenNameCanContainHyphens() {
        Citizen citizen = new Citizen();
        citizen.setLastName("De La Cruz-Martinez");
        assertEquals("De La Cruz-Martinez", citizen.getLastName());
    }

    @Test
    void citizenCreatedAtDefaultIsNull() {
        Citizen citizen = new Citizen();
        assertNull(citizen.getCreatedAt());
    }

    @Test
    void citizenParameterizedConstructorDoesNotSetPassword() {
        Citizen citizen = new Citizen(1, "John", "Doe", "john@example.com", "123456", "CIN123");
        assertNull(citizen.getPassword());
    }

    // ==================== EMPLOYEE EDGE CASES ====================

    @Test
    void employeeAgencyIdDefaultIsZero() {
        Employee employee = new Employee();
        assertEquals(0, employee.getAgencyId());
    }

    @Test
    void employeeServiceIdDefaultIsZero() {
        Employee employee = new Employee();
        assertEquals(0, employee.getServiceId());
    }

    @Test
    void employeeCounterIdDefaultIsZero() {
        Employee employee = new Employee();
        assertEquals(0, employee.getCounterId());
    }

    @Test
    void employeePasswordCanBeNull() {
        Employee employee = new Employee();
        assertNull(employee.getPassword());
    }

    @Test
    void employeeEmailCanBeNull() {
        Employee employee = new Employee();
        assertNull(employee.getEmail());
    }

    @Test
    void employeeCanBeAssignedToMultipleCounters() {
        Employee employee = new Employee();
        employee.setCounterId(1);
        assertEquals(1, employee.getCounterId());
        employee.setCounterId(5);
        assertEquals(5, employee.getCounterId());
    }

    @Test
    void employeeCreatedAtDefaultIsNull() {
        Employee employee = new Employee();
        assertNull(employee.getCreatedAt());
    }

    @Test
    void employeeCreatedAtCanBeSet() {
        Employee employee = new Employee();
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        assertEquals(now, employee.getCreatedAt());
    }

    @Test
    void employeeParameterizedConstructorSetsAgencyAndCounter() {
        Employee employee = new Employee(1, "John", "Doe", "john@example.com", 5, 3);
        assertEquals(5, employee.getAgencyId());
        assertEquals(3, employee.getCounterId());
    }

    @Test
    void employeeParameterizedConstructorDoesNotSetServiceId() {
        Employee employee = new Employee(1, "John", "Doe", "john@example.com", 5, 3);
        assertEquals(0, employee.getServiceId());
    }

    @Test
    void employeeParameterizedConstructorDoesNotSetPassword() {
        Employee employee = new Employee(1, "John", "Doe", "john@example.com", 5, 3);
        assertNull(employee.getPassword());
    }

    // ==================== ADMINISTRATOR EDGE CASES ====================

    @Test
    void administratorPasswordCanBeNull() {
        Administrator admin = new Administrator();
        assertNull(admin.getPassword());
    }

    @Test
    void administratorEmailCanBeNull() {
        Administrator admin = new Administrator();
        assertNull(admin.getEmail());
    }

    @Test
    void administratorCreatedAtDefaultIsNull() {
        Administrator admin = new Administrator();
        assertNull(admin.getCreatedAt());
    }

    @Test
    void administratorCreatedAtCanBeSet() {
        Administrator admin = new Administrator();
        LocalDateTime now = LocalDateTime.now();
        admin.setCreatedAt(now);
        assertEquals(now, admin.getCreatedAt());
    }

    @Test
    void administratorParameterizedConstructorDoesNotSetPassword() {
        Administrator admin = new Administrator(1, "Admin", "User", "admin@example.com");
        assertNull(admin.getPassword());
    }

    @Test
    void administratorIdCanBeLarge() {
        Administrator admin = new Administrator();
        admin.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, admin.getId());
    }

    @Test
    void administratorNameCanBeVeryLong() {
        Administrator admin = new Administrator();
        String longName = "A".repeat(255);
        admin.setFirstName(longName);
        admin.setLastName(longName);
        assertEquals(255, admin.getFirstName().length());
        assertEquals(255, admin.getLastName().length());
    }

    @Test
    void administratorEmailCanHavePlusSign() {
        Administrator admin = new Administrator();
        admin.setEmail("admin+test@example.com");
        assertEquals("admin+test@example.com", admin.getEmail());
    }
}
