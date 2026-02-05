package models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ModelsCoverageTest {

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

        assertEquals(1, citizen.getId());
        assertEquals("Ada", citizen.getFirstName());
        assertEquals("Lovelace", citizen.getLastName());
        assertEquals("ada@example.com", citizen.getEmail());
        assertEquals("123", citizen.getPhone());
        assertEquals("CIN123", citizen.getCin());
        assertEquals("pw", citizen.getPassword());
    }

    @Test
    void ticketConstructorAndFieldsWork() {
        Ticket ticket = new Ticket();
        ticket.setId(10);
        ticket.setTicketNumber("A001");
        ticket.setStatus("WAITING");
        ticket.setCreatedAt(LocalDateTime.now());

        assertEquals(10, ticket.getId());
        assertEquals("A001", ticket.getTicketNumber());
        assertEquals("WAITING", ticket.getStatus());
        assertNotNull(ticket.getCreatedAt());
    }

    @Test
    void employeeFieldsWork() {
        Employee employee = new Employee();
        employee.setId(7);
        employee.setFirstName("Grace");
        employee.setLastName("Hopper");
        employee.setEmail("grace@example.com");

        assertEquals(7, employee.getId());
        assertEquals("Grace", employee.getFirstName());
        assertEquals("Hopper", employee.getLastName());
        assertEquals("grace@example.com", employee.getEmail());
    }
}
