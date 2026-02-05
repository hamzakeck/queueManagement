package models;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for Ticket model - edge cases and business logic
 */
class TicketEdgeCaseTest {

    private Ticket ticket;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        baseTime = LocalDateTime.of(2026, 2, 5, 10, 0, 0);
    }

    @Test
    void ticketPositionCanBeZero() {
        ticket.setPosition(0);
        assertEquals(0, ticket.getPosition());
    }

    @Test
    void ticketPositionCanBeLarge() {
        ticket.setPosition(999);
        assertEquals(999, ticket.getPosition());
    }

    @Test
    void ticketCounterIdCanBeZero() {
        ticket.setCounterId(0);
        assertEquals(0, ticket.getCounterId());
    }

    @Test
    void ticketNumberCanContainLettersAndNumbers() {
        ticket.setTicketNumber("AB123");
        assertEquals("AB123", ticket.getTicketNumber());
    }

    @Test
    void ticketNumberCanBeOnlyLetters() {
        ticket.setTicketNumber("ABCD");
        assertEquals("ABCD", ticket.getTicketNumber());
    }

    @Test
    void ticketNumberCanBeOnlyNumbers() {
        ticket.setTicketNumber("12345");
        assertEquals("12345", ticket.getTicketNumber());
    }

    @Test
    void ticketTimestampsCanBeNull() {
        Ticket newTicket = new Ticket();
        assertNull(newTicket.getCreatedAt());
        assertNull(newTicket.getCalledAt());
        assertNull(newTicket.getCompletedAt());
    }

    @Test
    void ticketTimestampsCanBeSet() {
        ticket.setCreatedAt(baseTime);
        ticket.setCalledAt(baseTime.plusMinutes(10));
        ticket.setCompletedAt(baseTime.plusMinutes(20));

        assertEquals(baseTime, ticket.getCreatedAt());
        assertEquals(baseTime.plusMinutes(10), ticket.getCalledAt());
        assertEquals(baseTime.plusMinutes(20), ticket.getCompletedAt());
    }

    @Test
    void ticketIdsCanBeLarge() {
        ticket.setCitizenId(Integer.MAX_VALUE);
        ticket.setServiceId(Integer.MAX_VALUE);
        ticket.setAgencyId(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, ticket.getCitizenId());
        assertEquals(Integer.MAX_VALUE, ticket.getServiceId());
        assertEquals(Integer.MAX_VALUE, ticket.getAgencyId());
    }

    @Test
    void ticketStatusWaiting() {
        ticket.setStatus("WAITING");
        assertEquals("WAITING", ticket.getStatus());
    }

    @Test
    void ticketStatusCalled() {
        ticket.setStatus("CALLED");
        assertEquals("CALLED", ticket.getStatus());
    }

    @Test
    void ticketStatusInProgress() {
        ticket.setStatus("IN_PROGRESS");
        assertEquals("IN_PROGRESS", ticket.getStatus());
    }

    @Test
    void ticketStatusCompleted() {
        ticket.setStatus("COMPLETED");
        assertEquals("COMPLETED", ticket.getStatus());
    }

    @Test
    void ticketStatusCancelled() {
        ticket.setStatus("CANCELLED");
        assertEquals("CANCELLED", ticket.getStatus());
    }

    @Test
    void ticketFromConstructorHasWaitingStatus() {
        Ticket constructedTicket = new Ticket("T001", 1, 2, 3);
        assertEquals("WAITING", constructedTicket.getStatus());
    }

    @Test
    void ticketFromConstructorHasCorrectTicketNumber() {
        Ticket constructedTicket = new Ticket("T001", 1, 2, 3);
        assertEquals("T001", constructedTicket.getTicketNumber());
    }

    @Test
    void ticketFromConstructorHasCorrectCitizenId() {
        Ticket constructedTicket = new Ticket("T001", 1, 2, 3);
        assertEquals(1, constructedTicket.getCitizenId());
    }

    @Test
    void ticketFromConstructorHasCorrectServiceId() {
        Ticket constructedTicket = new Ticket("T001", 1, 2, 3);
        assertEquals(2, constructedTicket.getServiceId());
    }

    @Test
    void ticketFromConstructorHasCorrectAgencyId() {
        Ticket constructedTicket = new Ticket("T001", 1, 2, 3);
        assertEquals(3, constructedTicket.getAgencyId());
    }
}
